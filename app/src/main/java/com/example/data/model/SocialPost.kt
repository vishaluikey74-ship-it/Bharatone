package com.example.data.model

data class PostComment(
    val id: String,
    val postId: String,
    val authorName: String,
    val authorAvatar: String = "",
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class SocialPost(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatar: String = "",
    val authorBadge: VerificationBadge? = null,
    val text: String,
    val imageUrls: List<String> = emptyList(),
    val hashtags: List<String> = emptyList(),
    val locationName: String = "Balaghat, MP",
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val repostsCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isReposted: Boolean = false,
    val comments: List<PostComment> = emptyList()
)
