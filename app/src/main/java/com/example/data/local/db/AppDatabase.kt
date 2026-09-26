package com.example.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.IngestionLogDao
import com.example.data.local.dao.JobDao
import com.example.data.local.dao.ListingDao
import com.example.data.local.entity.IngestionLogEntity
import com.example.data.local.entity.JobEntity
import com.example.data.local.entity.ListingEntity

@Database(
    entities = [ListingEntity::class, JobEntity::class, IngestionLogEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun listingDao(): ListingDao
    abstract fun jobDao(): JobDao
    abstract fun ingestionLogDao(): IngestionLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bharat_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
