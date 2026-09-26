package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SocialPost
import com.example.data.model.User
import com.example.ui.components.SocialPostCard
import com.example.ui.theme.*
import com.example.ui.util.ShareHelper

@Composable
fun SocialScreen(
    posts: List<SocialPost>,
    currentUser: User,
    language: String,
    onLikePost: (String) -> Unit,
    onCommentPost: (String) -> Unit,
    onRepostPost: (String) -> Unit,
    onSavePost: (String) -> Unit,
    onCreatePost: (text: String, hashtags: List<String>, location: String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onReportPost: (String) -> Unit = {},
    onReportUser: (String, String) -> Unit = { _, _ -> },
    onBlockUser: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val isHindi = language == "hi"
    val context = LocalContext.current
    var selectedFeedType by remember { mutableStateOf("FOR_YOU") } // FOR_YOU, FOLLOWING, TRENDING, NEARBY
    var postInputText by remember { mutableStateOf("") }
    var selectedHashtag by remember { mutableStateOf("") }

    val popularHashtags = listOf("#Balaghat", "#MadhyaPradesh", "#StudentRooms", "#DigitalIndia", "#Agriculture", "#ViksitBharat")

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("social_screen")
    ) {
        val isLargeScreen = maxWidth >= 560.dp
        val horizontalContentPadding = if (isLargeScreen) 16.dp else 12.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Feed Tabs (For You | Following | Trending | Nearby)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SocialTab(
                    label = if (isHindi) "✨ आपके लिए" else "For You",
                    isSelected = selectedFeedType == "FOR_YOU",
                    onClick = { selectedFeedType = "FOR_YOU" }
                )
                SocialTab(
                    label = if (isHindi) "👥 फॉलो किए" else "Following",
                    isSelected = selectedFeedType == "FOLLOWING",
                    onClick = { selectedFeedType = "FOLLOWING" }
                )
                SocialTab(
                    label = if (isHindi) "🔥 ट्रेंडिंग" else "Trending",
                    isSelected = selectedFeedType == "TRENDING",
                    onClick = { selectedFeedType = "TRENDING" }
                )
                SocialTab(
                    label = if (isHindi) "📍 पास के लोग" else "Nearby",
                    isSelected = selectedFeedType == "NEARBY",
                    onClick = { selectedFeedType = "NEARBY" }
                )
            }

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(if (isLargeScreen) 2 else 1),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = horizontalContentPadding, end = horizontalContentPadding, top = 8.dp, bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalItemSpacing = 10.dp
            ) {
                // Post Composer Box (Full Width)
                item(span = StaggeredGridItemSpan.FullLine) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.Top) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(SaffronPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentUser.fullName.take(1),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                OutlinedTextField(
                                    value = postInputText,
                                    onValueChange = { postInputText = it },
                                    placeholder = {
                                        Text(
                                            text = if (isHindi) "आपके क्षेत्र में क्या चल रहा है? पोस्ट करें..." else "What is happening in your area? Share thoughts...",
                                            fontSize = 13.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("create_post_input"),
                                    minLines = 2,
                                    maxLines = 4,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Hashtag Suggestions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                popularHashtags.take(3).forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = SlateSurfaceVariant,
                                        modifier = Modifier.clickable {
                                            if (!postInputText.contains(tag)) {
                                                postInputText = "$postInputText $tag".trim()
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = tag,
                                            fontSize = 11.sp,
                                            color = BharatBlue,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Publish Post Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📍 ${currentUser.area}, ${currentUser.district}",
                                    fontSize = 11.sp,
                                    color = SlateTextSecondary
                                )

                                Button(
                                    onClick = {
                                        if (postInputText.isNotBlank()) {
                                            val tags = popularHashtags.filter { postInputText.contains(it) }
                                            onCreatePost(postInputText, tags, "${currentUser.area}, ${currentUser.district}")
                                            postInputText = ""
                                        }
                                    },
                                    enabled = postInputText.isNotBlank(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    modifier = Modifier.testTag("publish_post_btn")
                                ) {
                                    Text(
                                        text = if (isHindi) "पोस्ट करें" else "Post",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                if (posts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Forum,
                                    contentDescription = null,
                                    tint = SlateTextMuted,
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (isHindi) "अभी कोई पोस्ट नहीं है" else "No posts yet",
                                    fontWeight = FontWeight.Bold,
                                    color = SlateTextSecondary,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isHindi) "अपने विचार या अपडेट साझा करने के लिए ऊपर लिखें।" else "Share your thoughts or updates above.",
                                    color = SlateTextMuted,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Posts Stream (1 column on mobile, 2-column masonry grid on large screens)
                items(posts) { post ->
                    SocialPostCard(
                        post = post,
                        isHindi = isHindi,
                        onLikeClick = { onLikePost(post.id) },
                        onCommentClick = { onCommentPost(post.id) },
                        onRepostClick = { onRepostPost(post.id) },
                        onSaveClick = { onSavePost(post.id) },
                        onShareClick = { ShareHelper.shareSocialPost(context, post, isHindi) },
                        onAuthorClick = { onAuthorClick(post.authorId) },
                        onReportPost = onReportPost,
                        onReportUser = onReportUser,
                        onBlockUser = onBlockUser
                    )
                }
            }
        }
    }
}

@Composable
private fun SocialTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) SaffronPrimary else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}
