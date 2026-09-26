package com.example.data.model

enum class UserRole {
    SUPER_ADMIN,
    ADMIN,
    EDITOR,
    REPORTER,
    MODERATOR,
    USER
}

enum class VerificationBadge {
    VERIFIED_USER,
    VERIFIED_SELLER,
    VERIFIED_BUSINESS,
    VERIFIED_NEWS,
    EDITOR_REVIEWED
}

data class User(
    val id: String,
    val fullName: String,
    val username: String,
    val email: String,
    val phone: String,
    val bio: String = "",
    val avatarUrl: String = "",
    val state: String = "Madhya Pradesh",
    val district: String = "Balaghat",
    val city: String = "Balaghat City",
    val area: String = "Paraswada",
    val language: String = "hi", // "hi" or "en"
    val interests: List<String> = listOf("Rooms", "PG", "Local News", "Mobiles", "Education"),
    val joinedDate: String = "Sep 2026",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val friendsCount: Int = 0,
    val role: UserRole = UserRole.USER,
    val verificationBadges: Set<VerificationBadge> = emptySet(),
    val isSuspended: Boolean = false,
    val isBlocked: Boolean = false,
    val isLoggedIn: Boolean = false
)

object AllInterests {
    val list = listOf(
        "Rooms", "PG", "Property", "Office", "Shop",
        "Cars", "Bikes", "Mobiles", "Electronics",
        "Jobs", "Education", "Agriculture", "Business",
        "Local News", "National News", "Sports", "Politics",
        "Government Schemes"
    )
}
