package com.example

import android.app.Application
import com.example.data.ingestion.DataIngestionService
import com.example.data.local.db.AppDatabase
import com.example.data.repository.BharatRepository

class BharatApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val ingestionService: DataIngestionService by lazy { DataIngestionService(database) }
    val repository: BharatRepository by lazy { BharatRepository(database, ingestionService) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: BharatApplication
            private set
    }
}
