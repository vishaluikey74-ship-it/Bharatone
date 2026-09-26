package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingestion_logs")
data class IngestionLogEntity(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val sourceDomain: String, // "roomdekhobgt.com", "jobdekhobgt.com", "all"
    val syncedAt: Long = System.currentTimeMillis(),
    val itemsIngested: Int,
    val status: String, // "SUCCESS", "IN_PROGRESS", "FAILED"
    val summary: String,
    val durationMs: Long
)
