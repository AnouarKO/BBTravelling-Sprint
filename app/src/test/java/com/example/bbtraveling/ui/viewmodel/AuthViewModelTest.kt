package com.example.bbtraveling.ui.viewmodel

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.bbtraveling.R
import com.example.bbtraveling.domain.AuthMessages
import com.example.bbtraveling.domain.AuthRegistration
import com.example.bbtraveling.domain.AuthResult
import com.example.bbtraveling.domain.AuthUser
import com.example.bbtraveling.domain.repository.AuthRepository
import com.example.bbtraveling.testutil.MainDispatcherRule
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var context: Context
    private lateinit var repository: FakeAuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        repository = FakeAuthRepository()
        viewModel = AuthViewModel(repository, context)
    }

    @Test
    fun login_withBlankFields_returnsLocalizedValidationError() {
        viewModel.login(email = "", password = "")

        assertEquals(context.getString(R.string.auth_error_email_password_required), viewModel.uiState.value.error)
        assertEquals(0, repository.loginCalls)
    }

    @Test
    fun login_withVerifiedUser_updatesCurrentUser() {
        repository.loginResult = AuthResult.Success()

        viewModel.login(email = "test@example.com", password = "Test1234!")

        assertFalse(viewModel.uiState.value.loading)
        assertEquals("test@example.com", viewModel.uiState.value.currentUser?.email)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun login_withNotVerifiedEmail_returnsLocalizedError() {
        repository.loginResult = AuthResult.Failure(AuthMessages.EMAIL_NOT_VERIFIED)

        viewModel.login(email = "test@example.com", password = "Test1234!")

        assertEquals(context.getString(R.string.auth_error_email_not_verified), viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.currentUser)
    }

    @Test
    fun register_successSetsVerificationMessageAndCompletionFlag() {
        repository.registerResult = AuthResult.Success(AuthMessages.ACCOUNT_CREATED_VERIFY_EMAIL)

        viewModel.register(
            email = "new@example.com",
            password = "Test1234!",
            confirmPassword = "Test1234!",
            username = "test01",
            birthdate = LocalDate.of(2000, 1, 1),
            address = "Test street 1",
            country = "Spain",
            phone = "600000000",
            acceptsReceiveEmails = true
        )

        assertEquals(
            context.getString(R.string.auth_message_account_created_verify_email),
            viewModel.uiState.value.message
        )
        assertTrue(viewModel.uiState.value.registrationCompleted)
        assertEquals("test01", repository.lastRegistration?.username)
        assertEquals(LocalDate.of(2000, 1, 1), repository.lastRegistration?.birthdate)
    }

    @Test
    fun register_withDifferentPasswords_returnsLocalizedErrorAndSkipsRepository() {
        viewModel.register(
            email = "new@example.com",
            password = "Test1234!",
            confirmPassword = "Mismatch123!",
            username = "test01",
            birthdate = LocalDate.of(2000, 1, 1),
            address = "Test street 1",
            country = "Spain",
            phone = "600000000",
            acceptsReceiveEmails = true
        )

        assertEquals(
            context.getString(R.string.auth_error_passwords_do_not_match),
            viewModel.uiState.value.error
        )
        assertNull(repository.lastRegistration)
        assertFalse(viewModel.uiState.value.registrationCompleted)
    }

    @Test
    fun register_withMissingProfileFields_returnsLocalizedErrorAndSkipsRepository() {
        viewModel.register(
            email = "new@example.com",
            password = "Test1234!",
            confirmPassword = "Test1234!",
            username = "test01",
            birthdate = null,
            address = "",
            country = "Spain",
            phone = "600000000",
            acceptsReceiveEmails = false
        )

        assertEquals(
            context.getString(R.string.auth_error_register_required),
            viewModel.uiState.value.error
        )
        assertNull(repository.lastRegistration)
        assertFalse(viewModel.uiState.value.registrationCompleted)
    }

    @Test
    fun register_withMinorBirthdate_returnsLocalizedErrorAndSkipsRepository() {
        viewModel.register(
            email = "new@example.com",
            password = "Test1234!",
            confirmPassword = "Test1234!",
            username = "test01",
            birthdate = LocalDate.now().minusYears(18).plusDays(1),
            address = "Test street 1",
            country = "Spain",
            phone = "600000000",
            acceptsReceiveEmails = false
        )

        assertEquals(
            context.getString(R.string.auth_error_minimum_age_required),
            viewModel.uiState.value.error
        )
        assertNull(repository.lastRegistration)
        assertFalse(viewModel.uiState.value.registrationCompleted)
    }

    @Test
    fun recoverPassword_successReturnsLocalizedMessage() {
        repository.recoverPasswordResult = AuthResult.Success(AuthMessages.PASSWORD_RECOVERY_SENT)

        viewModel.recoverPassword("test@example.com")

        assertEquals(
            context.getString(R.string.auth_message_password_recovery_sent),
            viewModel.uiState.value.message
        )
    }

    @Test
    fun logout_clearsCurrentUserState() {
        repository.currentUserFlow.value = AuthUser(
            userId = "uid",
            login = "test@example.com",
            email = "test@example.com"
        )

        viewModel.logout()

        assertNull(viewModel.uiState.value.currentUser)
        assertEquals(1, repository.logoutCalls)
    }

    private class FakeAuthRepository : AuthRepository {
        val currentUserFlow = MutableStateFlow<AuthUser?>(null)
        override val currentUser: StateFlow<AuthUser?> = currentUserFlow

        var loginResult: AuthResult = AuthResult.Success()
        var registerResult: AuthResult = AuthResult.Success()
        var recoverPasswordResult: AuthResult = AuthResult.Success()
        var lastRegistration: AuthRegistration? = null
        var loginCalls = 0
        var logoutCalls = 0

        override suspend fun login(email: String, password: String): AuthResult {
            loginCalls += 1
            if (loginResult is AuthResult.Success) {
                val normalizedEmail = email.trim().lowercase()
                currentUserFlow.value = AuthUser(
                    userId = "uid",
                    login = normalizedEmail,
                    email = normalizedEmail
                )
            }
            return loginResult
        }

        override suspend fun register(registration: AuthRegistration): AuthResult {
            lastRegistration = registration
            return registerResult
        }

        override suspend fun recoverPassword(email: String): AuthResult = recoverPasswordResult

        override suspend fun logout() {
            logoutCalls += 1
            currentUserFlow.value = null
        }
    }
}

