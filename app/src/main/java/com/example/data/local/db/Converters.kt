package com.example.data.local.db

import androidx.room.TypeConverter
import com.example.data.model.*

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString("|||") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("|||").filter { it.isNotBlank() }
    }

    @TypeConverter
    fun fromListingDomain(value: ListingDomain?): String {
        return value?.name ?: ListingDomain.ROOM_RENTAL.name
    }

    @TypeConverter
    fun toListingDomain(value: String?): ListingDomain {
        return try {
            if (value != null) ListingDomain.valueOf(value) else ListingDomain.ROOM_RENTAL
        } catch (e: Exception) {
            ListingDomain.ROOM_RENTAL
        }
    }

    @TypeConverter
    fun fromPropertyCategory(value: PropertyCategory?): String? {
        return value?.name
    }

    @TypeConverter
    fun toPropertyCategory(value: String?): PropertyCategory? {
        return if (value.isNullOrBlank()) null else try {
            PropertyCategory.valueOf(value)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun fromMarketCategory(value: MarketCategory?): String? {
        return value?.name
    }

    @TypeConverter
    fun toMarketCategory(value: String?): MarketCategory? {
        return if (value.isNullOrBlank()) null else try {
            MarketCategory.valueOf(value)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun fromVerificationBadge(value: VerificationBadge?): String? {
        return value?.name
    }

    @TypeConverter
    fun toVerificationBadge(value: String?): VerificationBadge? {
        return if (value.isNullOrBlank()) null else try {
            VerificationBadge.valueOf(value)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun fromJobType(value: JobType?): String {
        return value?.name ?: JobType.GOVERNMENT.name
    }

    @TypeConverter
    fun toJobType(value: String?): JobType {
        return try {
            if (value != null) JobType.valueOf(value) else JobType.GOVERNMENT
        } catch (e: Exception) {
            JobType.GOVERNMENT
        }
    }

    @TypeConverter
    fun fromEmployerVerification(value: EmployerVerification?): String {
        return value?.name ?: EmployerVerification.UNVERIFIED_EMPLOYER.name
    }

    @TypeConverter
    fun toEmployerVerification(value: String?): EmployerVerification {
        return try {
            if (value != null) EmployerVerification.valueOf(value) else EmployerVerification.UNVERIFIED_EMPLOYER
        } catch (e: Exception) {
            EmployerVerification.UNVERIFIED_EMPLOYER
        }
    }

    @TypeConverter
    fun fromPublicationLevel(value: PublicationLevel?): String {
        return value?.name ?: PublicationLevel.LEVEL_1_AUTO.name
    }

    @TypeConverter
    fun toPublicationLevel(value: String?): PublicationLevel {
        return try {
            if (value != null) PublicationLevel.valueOf(value) else PublicationLevel.LEVEL_1_AUTO
        } catch (e: Exception) {
            PublicationLevel.LEVEL_1_AUTO
        }
    }
}
