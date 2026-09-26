package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.ListingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {
    @Query("SELECT * FROM listings ORDER BY isPromoted DESC, createdAt DESC")
    fun getAllListings(): Flow<List<ListingEntity>>

    @Query("SELECT * FROM listings WHERE domain = :domain ORDER BY isPromoted DESC, createdAt DESC")
    fun getListingsByDomain(domain: String): Flow<List<ListingEntity>>

    @Query("SELECT * FROM listings WHERE sourceDomain = :sourceDomain ORDER BY createdAt DESC")
    fun getListingsBySource(sourceDomain: String): Flow<List<ListingEntity>>

    @Query("SELECT * FROM listings WHERE id = :id LIMIT 1")
    suspend fun getListingById(id: String): ListingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListings(listings: List<ListingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: ListingEntity)

    @Update
    suspend fun updateListing(listing: ListingEntity)

    @Query("DELETE FROM listings WHERE id = :id")
    suspend fun deleteListingById(id: String)

    @Query("DELETE FROM listings WHERE sellerId = :sellerId")
    suspend fun deleteListingsBySeller(sellerId: String)

    @Query("DELETE FROM listings WHERE sourceDomain = :sourceDomain")
    suspend fun deleteListingsBySource(sourceDomain: String)

    @Query("SELECT COUNT(*) FROM listings")
    suspend fun getTotalListingCount(): Int

    @Query("SELECT COUNT(*) FROM listings WHERE sourceDomain = :sourceDomain")
    suspend fun getCountBySource(sourceDomain: String): Int
}
