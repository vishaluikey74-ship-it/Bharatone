package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.util.ShareHelper
import com.example.ui.viewmodel.AppTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: String,
    district: String,
    area: String,
    language: String,
    rooms: List<Listing>,
    marketplaceItems: List<Listing>,
    newsList: List<NewsArticle>,
    jobsList: List<JobListing> = emptyList(),
    socialPosts: List<SocialPost>,
    savedIds: Set<String>,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onNavigateTab: (AppTab) -> Unit,
    onOpenAITool: (AIStudioTool) -> Unit = {},
    onListingClick: (Listing) -> Unit,
    onNewsClick: (NewsArticle) -> Unit,
    onJobClick: (JobListing) -> Unit = {},
    onOpenPipelineSimulator: () -> Unit = {},
    onSaveClick: (String) -> Unit,
    onCallClick: (Listing) -> Unit,
    onChatClick: (Listing) -> Unit,
    onLikePost: (String) -> Unit,
    onCommentPost: (String) -> Unit,
    onRepostPost: (String) -> Unit,
    onOpenReport: (String, String, String) -> Unit,
    onPostFABClick: () -> Unit,
    onRunAutoNews: () -> Unit,
    onShareApp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isHindi = language == "hi"
    val context = LocalContext.current
    val breakingNews = newsList.find { it.isBreaking } ?: newsList.firstOrNull()
    var showGovtDisclaimerDialog by remember { mutableStateOf(false) }

    if (showGovtDisclaimerDialog) {
        AboutDisclaimerDialog(
            isHindi = isHindi,
            onDismiss = { showGovtDisclaimerDialog = false }
        )
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val isLargeScreen = maxWidth >= 560.dp

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_pull_to_refresh")
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("home_screen_content"),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
        // 1. Breaking News Ticker Banner with High-Contrast Pulse
        if (breakingNews != null) {
            item {
                Surface(
                    color = BreakingNewsRed,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("breaking_news_ticker")
                        .clickable { onNewsClick(breakingNews) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(BreakingNewsRed)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isHindi) "ताज़ा ख़बर" else "BREAKING",
                                    color = BreakingNewsRed,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) breakingNews.hindiHeadline else breakingNews.englishHeadline,
                            color = Color.White,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 2. Ultra-Modern Hero Banner Card with Indian Flag Accent & Service Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavyHeroGradient)
                    ) {
                        // Subtle Tricolor Accent Top Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(SaffronPrimary))
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White))
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(IndiaGreen))
                        }

                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                            // Top Row: Location Header + Post Ad CTA
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🇮🇳", fontSize = 18.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (isHindi) "नमस्ते, $district" else "Welcome, $district",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 15.sp
                                            )
                                        )
                                        Text(
                                            text = if (area.isNotEmpty() && area != "All Areas") "$area • $state" else "$district • $state",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = SaffronLight,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                // Quick Post Button with Saffron Gradient
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .shadow(3.dp, RoundedCornerShape(12.dp))
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SaffronGradient)
                                        .clickable { onPostFABClick() }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddCircle,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = if (isHindi) "विज्ञापन +" else "Post +",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick Domain Action Grid with Glassmorphic Circular Squircle Tiles
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                QuickActionItem(
                                    icon = Icons.Default.MeetingRoom,
                                    label = if (isHindi) "कमरा/PG" else "Rooms",
                                    color = SaffronLight,
                                    onClick = { onNavigateTab(AppTab.ROOMS) }
                                )
                                QuickActionItem(
                                    icon = Icons.Default.ShoppingBag,
                                    label = if (isHindi) "मार्केट" else "Market",
                                    color = BharatBlue,
                                    onClick = { onNavigateTab(AppTab.MARKETPLACE) }
                                )
                                QuickActionItem(
                                    icon = Icons.Default.Work,
                                    label = if (isHindi) "भर्ती/जॉब्स" else "Jobs",
                                    color = ForestGreen,
                                    onClick = { onNavigateTab(AppTab.JOBS) }
                                )
                                QuickActionItem(
                                    icon = Icons.Default.Newspaper,
                                    label = if (isHindi) "समाचार" else "News",
                                    color = BreakingNewsRed,
                                    onClick = { onNavigateTab(AppTab.NEWS) }
                                )
                                QuickActionItem(
                                    icon = Icons.Default.DynamicFeed,
                                    label = if (isHindi) "सोशल मंच" else "Social",
                                    color = IndiaGreen,
                                    onClick = { onNavigateTab(AppTab.SOCIAL) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Bharat AI Studio Suite Carousel (6 Powerful AI Tools)
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                SectionHeader(
                    title = if (isHindi) "⚡ भारत एआई स्टूडियो टूल्स" else "⚡ Bharat AI Studio Suite",
                    actionText = if (isHindi) "सभी खोलें →" else "Open Hub →",
                    onActionClick = { onNavigateTab(AppTab.AI_STUDIO) }
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    items(AIStudioTool.values()) { tool ->
                        val (toolIcon, bgGradient) = when (tool) {
                            AIStudioTool.MUSIC_GEN -> Pair(Icons.Default.MusicNote, listOf(Color(0xFFEA580C), Color(0xFFC2410C)))
                            AIStudioTool.IMAGE_CREATE_EDIT -> Pair(Icons.Default.AutoFixHigh, listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)))
                            AIStudioTool.TEXT_TO_VIDEO -> Pair(Icons.Default.Videocam, listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                            AIStudioTool.MAPS_GROUNDING -> Pair(Icons.Default.LocationOn, listOf(Color(0xFF059669), Color(0xFF047857)))
                            AIStudioTool.IMAGE_TO_VIDEO -> Pair(Icons.Default.MovieCreation, listOf(Color(0xFFD97706), Color(0xFFB45309)))
                            AIStudioTool.SEARCH_GROUNDING -> Pair(Icons.Default.Search, listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)))
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .testTag("home_ai_tool_${tool.id}")
                                .clickable {
                                    onOpenAITool(tool)
                                    onNavigateTab(AppTab.AI_STUDIO)
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Brush.horizontalGradient(bgGradient))
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(Color.White.copy(alpha = 0.22f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = toolIcon,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = if (isHindi) tool.titleHi else tool.titleEn,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = tool.modelName,
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. AI Verification Pipeline Status Ribbon Card (Debug Only)
        if (com.example.BuildConfig.DEBUG) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .clickable { onOpenPipelineSimulator() }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SaffronContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = SaffronDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isHindi) "100% सत्यापित सूचना इंजन (11-Step AI)" else "100% Verified Information Engine",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = SlateTextPrimary
                                )
                            }
                            Text(
                                text = if (isHindi) "बिना आधिकारिक पुष्टि के कोई भर्ती या समाचार नहीं। जांचें →" else "Zero fake recruitment or news. Tap to inspect 11-step audit logs →",
                                fontSize = 11.sp,
                                color = SlateTextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = null,
                            tint = SaffronPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        val verifiedJobs = jobsList.filter { job ->
            if (job.jobType == JobType.GOVERNMENT) job.hasValidOfficialGovUrl() else true
        }

        // 5. Verified Jobs & Recruitment Section (Public & Local)
        if (verifiedJobs.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SectionHeader(
                    title = if (isHindi) "💼 सार्वजनिक व स्थानीय भर्तियां" else "💼 Public & Local Recruitment",
                    actionText = BharatStrings.t("view_all", language),
                    onActionClick = { onNavigateTab(AppTab.JOBS) }
                )
                GovtDisclaimerBanner(
                    isHindi = isHindi,
                    onSourcesClick = { showGovtDisclaimerDialog = true },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(verifiedJobs.take(6)) { job ->
                        Box(modifier = Modifier.width(if (isLargeScreen) 280.dp else 250.dp)) {
                            JobCard(
                                job = job,
                                isHindi = isHindi,
                                onJobClick = { onJobClick(job) },
                                onReportClick = { onOpenReport("JOB", job.id, job.postName) }
                            )
                        }
                    }
                }
            }
        }

        // 6. Recommended Rooms Section (Near You)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(
                title = BharatStrings.t("rooms_near_you", language),
                actionText = BharatStrings.t("view_all", language),
                onActionClick = { onNavigateTab(AppTab.ROOMS) }
            )
        }

        item {
            val roomList = rooms.take(6)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(roomList) { room ->
                    Box(modifier = Modifier.width(if (isLargeScreen) 260.dp else 235.dp)) {
                        ListingCard(
                            listing = room,
                            isSaved = savedIds.contains(room.id),
                            isHindi = isHindi,
                            onCardClick = { onListingClick(room) },
                            onSaveClick = { onSaveClick(room.id) },
                            onCallClick = { onCallClick(room) },
                            onChatClick = { onChatClick(room) },
                            onReportClick = { onOpenReport("LISTING", room.id, room.title) }
                        )
                    }
                }
            }
        }

        // 7. Marketplace Deals
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(
                title = BharatStrings.t("marketplace_deals", language),
                actionText = BharatStrings.t("view_all", language),
                onActionClick = { onNavigateTab(AppTab.MARKETPLACE) }
            )
        }

        item {
            val marketList = marketplaceItems.take(6)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(marketList) { item ->
                    Box(modifier = Modifier.width(if (isLargeScreen) 260.dp else 235.dp)) {
                        ListingCard(
                            listing = item,
                            isSaved = savedIds.contains(item.id),
                            isHindi = isHindi,
                            onCardClick = { onListingClick(item) },
                            onSaveClick = { onSaveClick(item.id) },
                            onCallClick = { onCallClick(item) },
                            onChatClick = { onChatClick(item) },
                            onReportClick = { onOpenReport("LISTING", item.id, item.title) }
                        )
                    }
                }
            }
        }

        // 8. Sponsored Business Banner
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SaffronContainer.copy(alpha = 0.6f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, SaffronLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SaffronPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isHindi) "प्रायोजित विज्ञापन (Sponsored)" else "Sponsored Business",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronDark
                            )
                        }
                        Text(
                            text = if (isHindi) "अपने स्थानीय व्यापार को पूरे मध्य प्रदेश में प्रमोट करें" else "Promote your local business across district & state with BharatOne Ads",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                        )
                    }
                }
            }
        }

        // 9. Latest Hindi News Section (Regional & National Live Updates)
        if (newsList.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isHindi) "📰 ताज़ा व मुख्य समाचार" else "📰 Latest & Top News",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ForestGreenContainer
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(ForestGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isHindi) "हर मिनट लाइव" else "1m Live",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                            }
                        }
                    }
                    Text(
                        text = BharatStrings.t("view_all", language),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavigateTab(AppTab.NEWS) }
                            .padding(4.dp)
                    )
                }
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(newsList.take(6)) { newsItem ->
                        Box(modifier = Modifier.width(if (isLargeScreen) 290.dp else 260.dp)) {
                            NewsCard(
                                news = newsItem,
                                isHindi = isHindi,
                                onNewsClick = { onNewsClick(newsItem) },
                                onShareClick = { ShareHelper.shareNews(context, newsItem, isHindi) },
                                onReportClick = { onOpenReport("NEWS", newsItem.id, newsItem.hindiHeadline) }
                            )
                        }
                    }
                }
            }
        }

        // 10. Community Social Buzz Section (Horizontally Scrolling LazyRow)
        if (socialPosts.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SectionHeader(
                    title = BharatStrings.t("social_buzz", language),
                    actionText = BharatStrings.t("view_all", language),
                    onActionClick = { onNavigateTab(AppTab.SOCIAL) }
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(socialPosts.take(6)) { post ->
                        Box(modifier = Modifier.width(if (isLargeScreen) 310.dp else 275.dp)) {
                            SocialPostCard(
                                post = post,
                                isHindi = isHindi,
                                onLikeClick = { onLikePost(post.id) },
                                onCommentClick = { onCommentPost(post.id) },
                                onRepostClick = { onRepostPost(post.id) },
                                onSaveClick = { onSaveClick(post.id) },
                                onShareClick = { ShareHelper.shareSocialPost(context, post, isHindi) },
                                onAuthorClick = { onNavigateTab(AppTab.PROFILE) }
                            )
                        }
                    }
                }
            }
        }
    }

        // Floating Corner Share Symbol (Icon only as requested)
        FloatingActionButton(
            onClick = onShareApp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 20.dp)
                .size(48.dp)
                .testTag("corner_share_symbol_btn"),
            shape = CircleShape,
            containerColor = SaffronPrimary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = if (isHindi) "शेयर करें" else "Share App",
                modifier = Modifier.size(20.dp)
            )
        }
}
}
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SaffronPrimary,
                    fontSize = 12.sp
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onActionClick() }
                    .padding(4.dp)
            )
        }
    }
}
