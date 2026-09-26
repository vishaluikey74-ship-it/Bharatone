package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.example.data.model.NewsArticle
import com.example.data.model.NewsCategory
import com.example.ui.components.NewsCard
import com.example.ui.components.OfficialPortalsSection
import com.example.ui.theme.*
import com.example.ui.util.ShareHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    newsList: List<NewsArticle>,
    selectedCategory: NewsCategory?,
    selectedScope: String,
    state: String,
    district: String,
    area: String,
    language: String,
    isRefreshing: Boolean = false,
    newsRefreshError: String? = null,
    onRefresh: () -> Unit = {},
    onCategorySelect: (NewsCategory?) -> Unit,
    onScopeSelect: (String) -> Unit,
    onNewsClick: (NewsArticle) -> Unit,
    onSubmitNewsClick: () -> Unit,
    onRunAutoNews: () -> Unit,
    onOpenReport: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = language == "hi"
    val context = LocalContext.current
    var showDisclaimerDialog by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("news_screen")
    ) {
        val isLargeScreen = maxWidth >= 560.dp
        val horizontalContentPadding = if (isLargeScreen) 16.dp else 12.dp

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Location Scope Tabs (Area / District / State / All India)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ScopeTab(
                        label = if (isHindi) "📍 क्षेत्र" else "Area",
                        isSelected = selectedScope == "MY_AREA",
                        onClick = { onScopeSelect("MY_AREA") }
                    )
                    ScopeTab(
                        label = if (isHindi) "🏙️ जिला ($district)" else "District",
                        isSelected = selectedScope == "MY_DISTRICT",
                        onClick = { onScopeSelect("MY_DISTRICT") }
                    )
                    ScopeTab(
                        label = if (isHindi) "🏛️ राज्य ($state)" else "State",
                        isSelected = selectedScope == "MY_STATE",
                        onClick = { onScopeSelect("MY_STATE") }
                    )
                    ScopeTab(
                        label = if (isHindi) "🇮🇳 देश (PIB)" else "All India",
                        isSelected = selectedScope == "ALL",
                        onClick = { onScopeSelect("ALL") }
                    )
                }

                // Live PIB Update Status Ribbon
                Surface(
                    color = ForestGreenContainer.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .testTag("live_news_status_ribbon")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ForestGreen)
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isHindi) "आधिकारिक PIB लाइव बुलेटिन" else "Official PIB Live Bulletin",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { showDisclaimerDialog = true },
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Info,
                                            contentDescription = "Disclaimer",
                                            tint = ForestGreen,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isHindi) "प्रेस सूचना ब्यूरो (PIB) स्रोत फ़ीड • 100% प्रामाणिक" else "Press Information Bureau (PIB) feeds • Authentic",
                                    fontSize = 10.sp,
                                    color = SlateTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ForestGreen,
                            modifier = Modifier
                                .clickable { onRunAutoNews() }
                                .testTag("refresh_live_news_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isHindi) "ताज़ा करें" else "Refresh",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                    }
                }

                // Error Notice if Refresh Failed but previous news is preserved
                if (newsRefreshError != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "ताज़ा नहीं हो सका, इंटरनेट कनेक्शन जांचें" else "Could not refresh, check internet",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Action Ribbon: "📝 Submit News" + "⚡ Run Auto Ingestion"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onSubmitNewsClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                        modifier = Modifier.testTag("submit_news_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) "📝 खबर भेजें" else "📝 Submit News",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onRunAutoNews,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp),
                        modifier = Modifier.testTag("auto_ingest_news_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = BreakingNewsRed, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isHindi) "⚡ PIB लाइव फेच" else "⚡ Fetch PIB News",
                            color = BreakingNewsRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    }
                }

                // Category Horizontal Filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onCategorySelect(null) },
                        label = { Text(if (isHindi) "सभी" else "All", fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.White
                        )
                    )

                    NewsCategory.values().forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelect(if (isSelected) null else cat) },
                            label = { Text("${cat.emoji} " + if (isHindi) cat.hindiName else cat.displayName, fontSize = 11.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaffronPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // News Articles List (1-col mobile, 2-col masonry grid on larger devices)
                if (newsList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = horizontalContentPadding, vertical = 16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 640.dp)
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Icon(
                                imageVector = Icons.Default.Newspaper,
                                contentDescription = null,
                                tint = SlateTextMuted,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isHindi) "इस क्षेत्र/श्रेणी में कोई खबर नहीं मिली" else "No news found for this location/category",
                                fontWeight = FontWeight.Bold,
                                color = SlateTextSecondary,
                                fontSize = 15.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isHindi)
                                    "भारतवन पर केवल लाइव व अधिकृत स्रोतों से सत्यापित समाचार प्रसारित किए जाते हैं। आधिकारिक सरकारी विज्ञप्ति व सूचनाओं के लिए नीचे दिए गए अधिकृत पोर्टल्स देखें:"
                                else
                                    "BharatOne only broadcasts authentic news from verified feeds. For official government notifications and press releases, please visit the authorized portals below:",
                                fontSize = 12.sp,
                                color = SlateTextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onRunAutoNews,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                            ) {
                                Text(if (isHindi) "⚡ अभी लाइव बुलेटिन फेच करें" else "⚡ Check Live News Feed", fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OfficialPortalsSection(
                                isHindi = isHindi,
                                titleOverride = if (isHindi) "आधिकारिक सरकारी समाचार व पोर्टल" else "Official Government Portals & Releases",
                                subtitleOverride = if (isHindi)
                                    "केंद्र व राज्य शासन की आधिकारिक विज्ञप्ति व सूचनाओं के लिए सीधे अधिकृत पोर्टल्स पर जाएं:"
                                else
                                    "Access authorized government releases, gazette notifications & public information:"
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(if (isLargeScreen) 2 else 1),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = horizontalContentPadding, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalItemSpacing = 8.dp
                    ) {
                        items(newsList) { item ->
                            NewsCard(
                                news = item,
                                isHindi = isHindi,
                                onNewsClick = { onNewsClick(item) },
                                onShareClick = { ShareHelper.shareNews(context, item, isHindi) },
                                onReportClick = { onOpenReport("NEWS", item.id, item.hindiHeadline) }
                            )
                        }
                    }
                }
            }
        }

        if (showDisclaimerDialog) {
            AlertDialog(
                onDismissRequest = { showDisclaimerDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = if (isHindi) "क्षेत्रीय समाचार नीति व पारदर्शिता" else "News Sourcing & Aggregation Policy",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isHindi)
                                "1. भारतवन एक स्वतंत्र क्षेत्रीय मंच है जो उपयोगकर्ताओं के लाभार्थ प्रेस सूचना ब्यूरो (PIB, भारत सरकार) के आधिकारिक RSS फ़ीड्स से समाचार शीर्षक व लिंक प्रदर्शित करता है।\n\n2. सभी बौद्धिक संपदा अधिकार मूल प्रकाशक (प्रेस सूचना ब्यूरो, भारत सरकार) के पास सुरक्षित हैं।\n\n3. समाचार कार्ड पर टैप करने पर मूल आधिकारिक लिंक सीधे ब्राउज़र में खुलता है।"
                            else
                                "1. BharatOne is an independent community platform providing headlines and links directly from the official RSS feeds of the Press Information Bureau (PIB, Govt. of India).\n\n2. All intellectual property rights belong to Press Information Bureau, Govt. of India.\n\n3. Tapping a news card opens the authentic original source directly in your browser.",
                            fontSize = 12.sp,
                            color = SlateTextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDisclaimerDialog = false }) {
                        Text(if (isHindi) "समझ गया" else "Got it", fontWeight = FontWeight.Bold, color = SaffronPrimary)
                    }
                }
            )
        }
    }
}

@Composable
private fun ScopeTab(
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
