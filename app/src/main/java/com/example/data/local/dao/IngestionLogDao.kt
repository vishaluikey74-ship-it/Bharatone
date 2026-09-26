package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.IngestionLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngestionLogDao {
    @Query("SELECT * FROM ingestion_logs ORDER BY syncedAt DESC")
    fun getAllLogs(): Flow<List<IngestionLogEntity>>

    @Query("SELECT * FROM ingestion_logs ORDER BY syncedAt DESC LIMIT 1")
    suspend fun getLatestLog(): IngestionLogEntity?

    @Query("SELECT * FROM ingestion_logs WHERE sourceDomain = :sourceDomain ORDER BY syncedAt DESC LIMIT 1")
    suspend fun getLatestLogForSource(sourceDomain: String): IngestionLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: IngestionLogEntity): Long

    @Query("DELETE FROM ingestion_logs")
    suspend fun clearLogs()
}
