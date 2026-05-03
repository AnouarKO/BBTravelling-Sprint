package com.example.bbtraveling.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bbtraveling.R
import com.example.bbtraveling.domain.AuthMessages
import com.example.bbtraveling.domain.AuthRegistration
import com.example.bbtraveling.domain.AuthResult
import com.example.bbtraveling.domain.AuthUser
import com.example.bbtraveling.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUser: AuthUser? = null,
    val loading: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val registrationCompleted: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(currentUser = repository.currentUser.value))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(currentUser = user)
            }
        }
    }

    fun isAuthenticated(): Boolean = repository.currentUser.value != null

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = AuthMessages.EMAIL_PASSWORD_REQUIRED.localizedAuthMessage(),
                message = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                loading = true,
                error = null,
                message = null,
                registrationCompleted = false
            )
            when (val result = repository.login(email, password)) {
                is AuthResult.Success -> {
                    Log.i(TAG, "login success")
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        message = result.message?.localizedAuthMessage(),
                        error = null,
                        registrationCompleted = false
                    )
                }
                is AuthResult.Failure -> {
                    Log.w(TAG, "login failed: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = result.message.localizedAuthMessage(),
                        message = null,
                        registrationCompleted = false
                    )
                }
            }
        }
    }

    fun register(
        email: String,
        password: String,
        confirmPassword: String,
        username: String,
        birthdate: LocalDate?,
        address: String,
        country: String,
        phone: String,
        acceptsReceiveEmails: Boolean
    ) {
        if (
            email.isBlank() ||
            password.isBlank() ||
            confirmPassword.isBlank() ||
            username.isBlank() ||
            birthdate == null ||
            address.isBlank() ||
            country.isBlank() ||
            phone.isBlank()
        ) {
            _uiState.value = _uiState.value.copy(
                error = AuthMessages.REGISTER_REQUIRED.localizedAuthMessage(),
                message = null
            )
            return
        }
        if (birthdate.isAfter(LocalDate.now().minusYears(MINIMUM_REGISTER_AGE_YEARS))) {
            _uiState.value = _uiState.value.copy(
                error = AuthMessages.MINIMUM_AGE_REQUIRED.localizedAuthMessage(),
                message = null
            )
            return
        }
        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                error = AuthMessages.PASSWORDS_DO_NOT_MATCH.localizedAuthMessage(),
                message = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                loading = true,
                error = null,
                message = null,
                registrationCompleted = false
            )
            val registration = AuthRegistration(
                email = email,
                password = password,
                username = username,
                birthdate = birthdate,
                address = address,
                country = country,
                phone = phone,
                acceptsReceiveEmails = acceptsReceiveEmails
            )
            when (val result = repository.register(registration)) {
                is AuthResult.Success -> {
                    Log.i(TAG, "register success")
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        message = result.message?.localizedAuthMessage(),
                        error = null,
                        registrationCompleted = true
                    )
                }
                is AuthResult.Failure -> {
                    Log.w(TAG, "register failed: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = result.message.localizedAuthMessage(),
                        message = null,
                        registrationCompleted = false
                    )
                }
            }
        }
    }

    fun recoverPassword(email: String) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = AuthMessages.EMAIL_REQUIRED.localizedAuthMessage(),
                message = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                loading = true,
                error = null,
                message = null,
                registrationCompleted = false
            )
            when (val result = repository.recoverPassword(email)) {
                is AuthResult.Success -> {
                    Log.i(TAG, "recover password success")
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        message = result.message?.localizedAuthMessage(),
                        error = null,
                        registrationCompleted = false
                    )
                }
                is AuthResult.Failure -> {
                    Log.w(TAG, "recover password failed: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = result.message.localizedAuthMessage(),
                        message = null,
                        registrationCompleted = false
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState()
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            message = null,
            error = null,
            registrationCompleted = false
        )
    }

    fun consumeRegistrationCompleted() {
        _uiState.value = _uiState.value.copy(registrationCompleted = false)
    }

    private fun String.localizedAuthMessage(): String {
        val stringRes = when (this) {
            AuthMessages.ACCOUNT_CREATED_VERIFY_EMAIL -> R.string.auth_message_account_created_verify_email
            AuthMessages.PASSWORD_RECOVERY_SENT -> R.string.auth_message_password_recovery_sent
            AuthMessages.EMAIL_PASSWORD_REQUIRED -> R.string.auth_error_email_password_required
            AuthMessages.REGISTER_REQUIRED -> R.string.auth_error_register_required
            AuthMessages.EMAIL_REQUIRED -> R.string.auth_error_email_required
            AuthMessages.INVALID_EMAIL -> R.string.auth_error_invalid_email
            AuthMessages.PASSWORDS_DO_NOT_MATCH -> R.string.auth_error_passwords_do_not_match
            AuthMessages.MINIMUM_AGE_REQUIRED -> R.string.auth_error_minimum_age_required
            AuthMessages.EMAIL_NOT_VERIFIED -> R.string.auth_error_email_not_verified
            AuthMessages.USERNAME_IN_USE -> R.string.auth_error_username_in_use
            AuthMessages.LOCAL_PROFILE_NOT_FOUND -> R.string.auth_error_local_profile_not_found
            AuthMessages.NETWORK -> R.string.auth_error_network
            AuthMessages.EMAIL_IN_USE -> R.string.auth_error_email_in_use
            AuthMessages.WEAK_PASSWORD -> R.string.auth_error_weak_password
            AuthMessages.INVALID_CREDENTIALS -> R.string.auth_error_invalid_credentials
            AuthMessages.USER_DISABLED -> R.string.auth_error_user_disabled
            AuthMessages.AUTHENTICATION_FAILED -> R.string.auth_error_authentication_failed
            AuthMessages.REGISTRATION_FAILED -> R.string.auth_error_registration_failed
            AuthMessages.PASSWORD_RECOVERY_FAILED -> R.string.auth_error_password_recovery_failed
            else -> null
        }
        return stringRes?.let(context::getString) ?: this
    }

    private companion object {
        const val TAG = "AuthViewModel"
        const val MINIMUM_REGISTER_AGE_YEARS = 18L
    }
}
