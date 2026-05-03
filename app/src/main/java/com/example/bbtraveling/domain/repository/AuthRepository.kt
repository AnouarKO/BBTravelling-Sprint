package com.example.bbtraveling.domain.repository

import com.example.bbtraveling.domain.AuthRegistration
import com.example.bbtraveling.domain.AuthResult
import com.example.bbtraveling.domain.AuthUser
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<AuthUser?>

    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(registration: AuthRegistration): AuthResult
    suspend fun recoverPassword(email: String): AuthResult
    suspend fun logout()
}
