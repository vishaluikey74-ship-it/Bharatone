package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.JobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY publishedAt DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE jobType = :jobType ORDER BY publishedAt DESC")
    fun getJobsByType(jobType: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE sourceDomain = :sourceDomain ORDER BY publishedAt DESC")
    fun getJobsBySource(sourceDomain: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    suspend fun getJobById(id: String): JobEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity)

    @Update
    suspend fun updateJob(job: JobEntity)

    @Query("DELETE FROM jobs WHERE id = :id")
    suspend fun deleteJobById(id: String)

    @Query("DELETE FROM jobs WHERE sourceDomain = :sourceDomain")
    suspend fun deleteJobsBySource(sourceDomain: String)

    @Query("SELECT COUNT(*) FROM jobs")
    suspend fun getTotalJobCount(): Int

    @Query("SELECT COUNT(*) FROM jobs WHERE sourceDomain = :sourceDomain")
    suspend fun getCountBySource(sourceDomain: String): Int
}
