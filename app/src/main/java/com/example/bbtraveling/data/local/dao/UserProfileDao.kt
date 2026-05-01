package com.example.bbtraveling.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.bbtraveling.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    suspend fun getUser(login: String): UserProfileEntity?

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    fun observeUser(login: String): Flow<UserProfileEntity?>

    @Upsert
    suspend fun upsertUser(user: UserProfileEntity)

    @Query("DELETE FROM users WHERE login = :login")
    suspend fun deleteUser(login: String)
}
