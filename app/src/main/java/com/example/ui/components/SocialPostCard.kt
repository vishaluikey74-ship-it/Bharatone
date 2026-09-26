package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SocialPost
import com.example.ui.theme.*

@Composable
fun SocialPostCard(
    post: SocialPost,
    isHindi: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onRepostClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onAuthorClick: () -> Unit,
    onReportPost: (String) -> Unit = {},
    onReportUser: (String, String) -> Unit = { _, _ -> },
    onBlockUser: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("social_post_${post.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Author Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SaffronLight)
                        .clickable { onAuthorClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorName.take(1),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (post.authorBadge != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VerificationBadgeChip(badge = post.authorBadge)
                        }
                    }
                    Text(
                        text = "@${post.authorUsername} • ${post.locationName}",
                        fontSize = 11.sp,
                        color = SlateTextSecondary
                    )
                }

                IconButton(onClick = onShareClick, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = SlateTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // 3-dots Menu for UGC Compliance: Report Post, Report User, Block User
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = SlateTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isHindi) "🚩 पोस्ट रिपोर्ट करें" else "🚩 Report post") },
                            onClick = {
                                showMenu = false
                                onReportPost(post.id)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isHindi) "⚠️ यूज़र रिपोर्ट करें" else "⚠️ Report user") },
                            onClick = {
                                showMenu = false
                                onReportUser(post.authorId, post.authorName)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isHindi) "🚫 यूज़र ब्लॉक करें" else "🚫 Block user", color = BreakingNewsRed) },
                            onClick = {
                                showMenu = false
                                onBlockUser(post.authorId, post.authorName)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Text
            Text(
                text = post.text,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Hashtags
            if (post.hashtags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    post.hashtags.forEach { tag ->
                        Text(
                            text = tag,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BharatBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons: ❤️ Like | 💬 Comment | 🔁 Repost | 🔖 Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .testTag("post_like_btn_${post.id}")
                        .clickable { onLikeClick() }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) BreakingNewsRed else SlateTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.likesCount}",
                        fontSize = 12.sp,
                        color = if (post.isLiked) BreakingNewsRed else SlateTextSecondary
                    )
                }

                // Comment Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .testTag("post_comment_btn_${post.id}")
                        .clickable { onCommentClick() }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = SlateTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.commentsCount}",
                        fontSize = 12.sp,
                        color = SlateTextSecondary
                    )
                }

                // Repost Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .testTag("post_repost_btn_${post.id}")
                        .clickable { onRepostClick() }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repost",
                        tint = if (post.isReposted) IndiaGreen else SlateTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.repostsCount}",
                        fontSize = 12.sp,
                        color = if (post.isReposted) IndiaGreen else SlateTextSecondary
                    )
                }

                // Share Button (Play Store link)
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier
                        .testTag("post_share_btn_${post.id}")
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = SaffronDark,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Save Bookmark Button
                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .testTag("post_save_btn_${post.id}")
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (post.isSaved) SaffronPrimary else SlateTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Quick display of comments if any
            if (post.comments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SlateSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        val latestComment = post.comments.last()
                        Text(
                            text = "${latestComment.authorName}: ${latestComment.text}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
