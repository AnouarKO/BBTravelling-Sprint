package com.example.bbtraveling.data.repository

import android.util.Log
import com.example.bbtraveling.data.local.dao.AccessLogDao
import com.example.bbtraveling.data.local.dao.UserProfileDao
import com.example.bbtraveling.data.local.entity.AccessLogEntity
import com.example.bbtraveling.data.local.entity.UserProfileEntity
import com.example.bbtraveling.domain.AuthMessages
import com.example.bbtraveling.domain.AuthRegistration
import com.example.bbtraveling.domain.AuthResult
import com.example.bbtraveling.domain.AuthUser
import com.example.bbtraveling.domain.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.time.Clock
import java.time.LocalDateTime
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userProfileDao: UserProfileDao,
    private val accessLogDao: AccessLogDao,
    private val clock: Clock
) : AuthRepository {

    private val _currentUser = MutableStateFlow(firebaseAuth.currentUser.toAuthUserOrNull())
    override val currentUser: StateFlow<AuthUser?> = _currentUser

    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        _currentUser.value = auth.currentUser.toAuthUserOrNull()
    }

    init {
        firebaseAuth.addAuthStateListener(authListener)
        if (firebaseAuth.currentUser != null && firebaseAuth.currentUser.toAuthUserOrNull() == null) {
            firebaseAuth.signOut()
        }
    }

    override suspend fun login(email: String, password: String): AuthResult {
        return runCatching {
            val normalizedEmail = email.normalizedEmail()
            val result = firebaseAuth.signInWithEmailAndPassword(normalizedEmail, password).awaitTask()
            val user = result.user ?: error("Firebase user not available")

            if (!user.isEmailVerified) {
                firebaseAuth.signOut()
                Log.w(TAG, "login rejected: email not verified")
                return AuthResult.Failure(AuthMessages.EMAIL_NOT_VERIFIED)
            }

            if (userProfileDao.getUser(normalizedEmail) == null) {
                firebaseAuth.signOut()
                Log.w(TAG, "login rejected: local profile not found")
                return AuthResult.Failure(AuthMessages.LOCAL_PROFILE_NOT_FOUND)
            }

            accessLogDao.insertAccessLog(
                AccessLogEntity(
                    userId = user.uid,
                    eventType = ACCESS_LOGIN,
                    occurredAt = LocalDateTime.now(clock)
                )
            )
            _currentUser.value = user.toAuthUserOrNull()
            Log.i(TAG, "login success")
            AuthResult.Success()
        }.getOrElse { error ->
            Log.w(TAG, "login failed", error)
            AuthResult.Failure(error.toAuthMessage(AuthMessages.AUTHENTICATION_FAILED))
        }
    }

    override suspend fun register(registration: AuthRegistration): AuthResult {
        return runCatching {
            val normalizedEmail = registration.email.normalizedEmail()
            val normalizedUsername = registration.username.trim()

            val existingUsername = userProfileDao.getUserByUsername(normalizedUsername)
            if (existingUsername != null) {
                return AuthResult.Failure(AuthMessages.USERNAME_IN_USE)
            }

            val result = firebaseAuth
                .createUserWithEmailAndPassword(normalizedEmail, registration.password)
                .awaitTask()
            val user = result.user ?: error("Firebase user not available")

            userProfileDao.upsertUser(
                UserProfileEntity(
                    login = normalizedEmail,
                    username = normalizedUsername,
                    birthdate = registration.birthdate,
                    address = registration.address.trim(),
                    country = registration.country.trim(),
                    phone = registration.phone.trim(),
                    acceptsReceiveEmails = registration.acceptsReceiveEmails
                )
            )

            user.sendEmailVerification().awaitTask()
            firebaseAuth.signOut()
            _currentUser.value = null
            Log.i(TAG, "register success: verification email sent")
            AuthResult.Success(AuthMessages.ACCOUNT_CREATED_VERIFY_EMAIL)
        }.getOrElse { error ->
            Log.w(TAG, "register failed", error)
            AuthResult.Failure(error.toAuthMessage(AuthMessages.REGISTRATION_FAILED))
        }
    }

    override suspend fun recoverPassword(email: String): AuthResult {
        return runCatching {
            firebaseAuth.sendPasswordResetEmail(email.normalizedEmail()).awaitTask()
            Log.i(TAG, "password recovery email sent")
            AuthResult.Success(AuthMessages.PASSWORD_RECOVERY_SENT)
        }.getOrElse { error ->
            Log.w(TAG, "password recovery failed", error)
            AuthResult.Failure(error.toAuthMessage(AuthMessages.PASSWORD_RECOVERY_FAILED))
        }
    }

    override suspend fun logout() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            accessLogDao.insertAccessLog(
                AccessLogEntity(
                    userId = user.uid,
                    eventType = ACCESS_LOGOUT,
                    occurredAt = LocalDateTime.now(clock)
                )
            )
        }
        firebaseAuth.signOut()
        _currentUser.value = null
        Log.i(TAG, "logout success")
    }

    private fun FirebaseUser?.toAuthUserOrNull(): AuthUser? {
        val user = this ?: return null
        val email = user.email?.normalizedEmail().orEmpty()
        if (email.isBlank() || !user.isEmailVerified) return null
        return AuthUser(
            userId = user.uid,
            login = email,
            email = email
        )
    }

    private fun String.normalizedEmail(): String = trim().lowercase(Locale.ROOT)

    private fun Throwable.toAuthMessage(defaultMessage: String): String {
        return when (this) {
            is FirebaseNetworkException,
            is TimeoutCancellationException -> AuthMessages.NETWORK
            is FirebaseAuthException -> when (errorCode) {
                "ERROR_INVALID_EMAIL" -> AuthMessages.INVALID_EMAIL
                "ERROR_EMAIL_ALREADY_IN_USE",
                "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> AuthMessages.EMAIL_IN_USE
                "ERROR_WEAK_PASSWORD" -> AuthMessages.WEAK_PASSWORD
                "ERROR_USER_DISABLED" -> AuthMessages.USER_DISABLED
                "ERROR_USER_NOT_FOUND",
                "ERROR_WRONG_PASSWORD",
                "ERROR_INVALID_CREDENTIAL" -> AuthMessages.INVALID_CREDENTIALS
                else -> defaultMessage
            }
            else -> defaultMessage
        }
    }

    private suspend fun <T> Task<T>.awaitTask(): T {
        return withTimeout(FIREBASE_TIMEOUT_MS) {
            suspendCancellableCoroutine { continuation ->
                addOnCompleteListener { task ->
                    if (!continuation.isActive) return@addOnCompleteListener
                    if (task.isSuccessful) {
                        continuation.resume(task.result)
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: IllegalStateException("Firebase task failed.")
                        )
                    }
                }
            }
        }
    }

    private companion object {
        const val TAG = "FirebaseAuthRepository"
        const val ACCESS_LOGIN = "LOGIN"
        const val ACCESS_LOGOUT = "LOGOUT"
        const val FIREBASE_TIMEOUT_MS = 20_000L
    }
}
