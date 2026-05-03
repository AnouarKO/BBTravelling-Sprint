package com.example.bbtraveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bbtraveling.data.local.entity.AccessLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessLogDao {

    @Insert
    suspend fun insertAccessLog(accessLog: AccessLogEntity)

    @Query("SELECT * FROM access_logs WHERE userId = :userId ORDER BY occurredAt DESC")
    fun observeAccessLogs(userId: String): Flow<List<AccessLogEntity>>
}
