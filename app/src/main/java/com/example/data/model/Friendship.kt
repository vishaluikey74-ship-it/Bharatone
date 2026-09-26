package com.example.data.model

enum class FriendStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}

data class FriendRequest(
    val id: String,
    val fromUserId: String,
    val fromUserName: String,
    val fromUserAvatar: String = "",
    val fromUserCity: String = "Balaghat, MP",
    val toUserId: String = "usr_me",
    val timestamp: Long = System.currentTimeMillis(),
    val status: FriendStatus = FriendStatus.PENDING
)
