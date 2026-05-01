package com.example.bbtraveling.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.bbtraveling.data.local.entity.TripEntity
import com.example.bbtraveling.data.local.model.TripWithActivities
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Transaction
    @Query("SELECT * FROM trips ORDER BY startDate ASC")
    fun observeTripsWithActivities(): Flow<List<TripWithActivities>>

    @Query("SELECT COUNT(*) FROM trips")
    suspend fun countTrips(): Int

    @Upsert
    suspend fun upsertTrip(trip: TripEntity)

    @Upsert
    suspend fun upsertTrips(trips: List<TripEntity>)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: String)
}
