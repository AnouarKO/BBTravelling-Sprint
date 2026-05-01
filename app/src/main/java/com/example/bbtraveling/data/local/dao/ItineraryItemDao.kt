package com.example.bbtraveling.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.bbtraveling.data.local.entity.ItineraryItemEntity

@Dao
interface ItineraryItemDao {

    @Upsert
    suspend fun upsertItem(item: ItineraryItemEntity)

    @Upsert
    suspend fun upsertItems(items: List<ItineraryItemEntity>)

    @Query("DELETE FROM itinerary_items WHERE id = :activityId")
    suspend fun deleteItemById(activityId: String)
}
