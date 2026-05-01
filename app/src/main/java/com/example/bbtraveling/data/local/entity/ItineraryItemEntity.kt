package com.example.bbtraveling.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bbtraveling.domain.ActivityCategory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(
    tableName = "itinerary_items",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class ItineraryItemEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val title: String,
    val description: String,
    val date: LocalDate,
    val time: LocalTime,
    val category: ActivityCategory,
    val costEur: Double,
    val createdAt: LocalDateTime
)
