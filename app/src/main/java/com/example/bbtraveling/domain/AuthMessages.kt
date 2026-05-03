package com.example.bbtraveling.domain

object AuthMessages {
    const val ACCOUNT_CREATED_VERIFY_EMAIL = "auth_message_account_created_verify_email"
    const val PASSWORD_RECOVERY_SENT = "auth_message_password_recovery_sent"
    const val EMAIL_PASSWORD_REQUIRED = "auth_error_email_password_required"
    const val REGISTER_REQUIRED = "auth_error_register_required"
    const val EMAIL_REQUIRED = "auth_error_email_required"
    const val INVALID_EMAIL = "auth_error_invalid_email"
    const val PASSWORDS_DO_NOT_MATCH = "auth_error_passwords_do_not_match"
    const val MINIMUM_AGE_REQUIRED = "auth_error_minimum_age_required"
    const val EMAIL_NOT_VERIFIED = "auth_error_email_not_verified"
    const val USERNAME_IN_USE = "auth_error_username_in_use"
    const val LOCAL_PROFILE_NOT_FOUND = "auth_error_local_profile_not_found"
    const val NETWORK = "auth_error_network"
    const val EMAIL_IN_USE = "auth_error_email_in_use"
    const val WEAK_PASSWORD = "auth_error_weak_password"
    const val INVALID_CREDENTIALS = "auth_error_invalid_credentials"
    const val USER_DISABLED = "auth_error_user_disabled"
    const val AUTHENTICATION_FAILED = "auth_error_authentication_failed"
    const val REGISTRATION_FAILED = "auth_error_registration_failed"
    const val PASSWORD_RECOVERY_FAILED = "auth_error_password_recovery_failed"
}
