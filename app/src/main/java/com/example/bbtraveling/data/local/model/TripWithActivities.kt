package com.example.bbtraveling.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.bbtraveling.data.local.entity.ItineraryItemEntity
import com.example.bbtraveling.data.local.entity.TripEntity

data class TripWithActivities(
    @Embedded val trip: TripEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val activities: List<ItineraryItemEntity>
)
