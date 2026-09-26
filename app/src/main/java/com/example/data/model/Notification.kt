package com.example.data.model

enum class NotificationType {
    FRIEND_REQ,
    FRIEND_ACCEPTED,
    NEW_MESSAGE,
    LISTING_INTEREST,
    COMMENT,
    LIKE,
    REPOST,
    BREAKING_NEWS,
    AREA_NEWS,
    LISTING_SOLD,
    ADMIN_MSG
}

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: NotificationType,
    val isRead: Boolean = false,
    val targetId: String? = null
)
