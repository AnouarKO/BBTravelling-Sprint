package com.example.bbtraveling.domain

import java.time.LocalDate

data class UserProfile(
    val login: String,
    val username: String,
    val birthdate: LocalDate?,
    val address: String,
    val country: String,
    val phone: String,
    val acceptsReceiveEmails: Boolean
)
