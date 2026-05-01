package com.example.bbtraveling.domain.repository

import com.example.bbtraveling.domain.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    suspend fun getUser(login: String): UserProfile?
    fun observeUser(login: String): Flow<UserProfile?>
    suspend fun upsertUser(profile: UserProfile)
    suspend fun deleteUser(login: String)
}
