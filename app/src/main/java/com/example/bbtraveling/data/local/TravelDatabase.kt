package com.example.bbtraveling.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bbtraveling.data.local.dao.AccessLogDao
import com.example.bbtraveling.data.local.dao.ItineraryItemDao
import com.example.bbtraveling.data.local.dao.TripDao
import com.example.bbtraveling.data.local.dao.UserProfileDao
import com.example.bbtraveling.data.local.entity.AccessLogEntity
import com.example.bbtraveling.data.local.entity.ItineraryItemEntity
import com.example.bbtraveling.data.local.entity.TripEntity
import com.example.bbtraveling.data.local.entity.UserProfileEntity

@Database(
    entities = [TripEntity::class, ItineraryItemEntity::class, UserProfileEntity::class, AccessLogEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class TravelDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun itineraryItemDao(): ItineraryItemDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun accessLogDao(): AccessLogDao
}
