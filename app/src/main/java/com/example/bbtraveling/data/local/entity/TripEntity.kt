package com.example.bbtraveling.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bbtraveling.domain.TripStatus
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "trips",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["login"],
            childColumns = ["ownerLogin"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("ownerLogin")]
)
data class TripEntity(
    @PrimaryKey val id: String,
    val ownerLogin: String?,
    val title: String,
    val description: String,
    val city: String,
    val country: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: TripStatus,
    val accommodation: String,
    val transport: String,
    val travelers: Int,
    val budgetEur: Double,
    val createdAt: LocalDateTime
)
