package com.example.bbtraveling.domain

sealed class AuthResult {
    data class Success(val message: String? = null) : AuthResult()
    data class Failure(val message: String) : AuthResult()
}
