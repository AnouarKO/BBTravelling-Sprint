package com.example.bbtraveling.domain

import java.time.LocalDate

data class AuthRegistration(
    val email: String,
    val password: String,
    val username: String,
    val birthdate: LocalDate?,
    val address: String,
    val country: String,
    val phone: String,
    val acceptsReceiveEmails: Boolean
)
