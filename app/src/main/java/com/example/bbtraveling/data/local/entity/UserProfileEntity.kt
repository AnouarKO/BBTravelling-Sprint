package com.example.bbtraveling.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "users")
data class UserProfileEntity(
    @PrimaryKey val login: String,
    val username: String,
    val birthdate: LocalDate?,
    val address: String,
    val country: String,
    val phone: String,
    val acceptsReceiveEmails: Boolean
)
