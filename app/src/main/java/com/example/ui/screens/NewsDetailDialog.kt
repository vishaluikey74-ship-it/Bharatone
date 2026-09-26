package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.NewsArticle
import com.example.ui.components.NewsStatusBadge
import com.example.ui.theme.*
import com.example.ui.util.ShareHelper

@Composable
fun NewsDetailDialog(
    news: NewsArticle,
    language: String,
    onDismiss: () -> Unit,
    onReportClick: () -> Unit
) {
    val isHindi = language == "hi"
    val context = LocalContext.current
    var showEnglish by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NewsStatusBadge(status = news.verificationStatus, isHindi = isHindi)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { ShareHelper.shareNews(context, news, isHindi) },
                            modifier = Modifier.testTag("share_news_dialog_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = SaffronPrimary)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Divider(color = SlateBorder)

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Category & Location Tag
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${news.category.emoji} ${news.category.displayName}",
                                color = SaffronPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "📍 ${news.district}, ${news.state}",
                                color = SlateTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Headline
                    item {
                        Text(
                            text = if (showEnglish) news.englishHeadline else news.hindiHeadline,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 28.sp
                            )
                        )
                    }

                    // Language toggle chip (Hindi / English for the article)
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SlateSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Button(
                                    onClick = { showEnglish = false },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (!showEnglish) SaffronPrimary else Color.Transparent
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        text = "🇮🇳 हिन्दी",
                                        fontSize = 11.sp,
                                        color = if (!showEnglish) Color.White else SlateTextSecondary
                                    )
                                }
                                Button(
                                    onClick = { showEnglish = true },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (showEnglish) SaffronPrimary else Color.Transparent
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        text = "🇬🇧 English",
                                        fontSize = 11.sp,
                                        color = if (showEnglish) Color.White else SlateTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Full Content / Summary
                    item {
                        Text(
                            text = if (showEnglish) news.englishSummary else news.hindiSummary,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    // Source & Verification Attribution Box (MANDATORY POLICY COMPLIANCE)
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (news.isFactChecked) ForestGreenContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (news.sourceAttribution.isOfficialGovSource) Icons.Default.Gavel else Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = if (news.sourceAttribution.isOfficialGovSource) SaffronPrimary else BharatBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isHindi) "मूल स्रोत: ${news.sourceName}" else "Source: ${news.sourceName}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    // Publication Level Tag
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = when (news.publicationLevel) {
                                            com.example.data.model.PublicationLevel.LEVEL_1_AUTO -> ForestGreen
                                            com.example.data.model.PublicationLevel.LEVEL_2_AI_ADMIN -> BharatBlue
                                            com.example.data.model.PublicationLevel.LEVEL_3_MANUAL_REVIEW -> SaffronPrimary
                                        }
                                    ) {
                                        Text(
                                            text = if (isHindi) news.publicationLevel.hindiTitle else news.publicationLevel.levelName,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (news.sourceUrl.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = BharatBlue.copy(alpha = 0.1f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BharatBlue.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { ShareHelper.openWebUrl(context, news.sourceUrl) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.OpenInNew,
                                                contentDescription = null,
                                                tint = BharatBlue,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isHindi) "🌐 मूल स्रोत पर पढ़ें (${news.sourceName}) →" else "🌐 Read Full Article at Source (${news.sourceName}) →",
                                                fontSize = 11.5.sp,
                                                color = BharatBlue,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isHindi) "📅 प्रकाशित: ${news.sourceAttribution.publicationDateTime}" else "Published: ${news.sourceAttribution.publicationDateTime}",
                                        fontSize = 10.sp,
                                        color = SlateTextSecondary
                                    )
                                    Text(
                                        text = if (isHindi) "🔄 अद्यतन: ${news.sourceAttribution.updatedDateTime}" else "Updated: ${news.sourceAttribution.updatedDateTime}",
                                        fontSize = 10.sp,
                                        color = SlateTextSecondary
                                    )
                                }

                                if (news.unconfirmedNote != null) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = BreakingNewsRedContainer.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "⚠️ ${news.unconfirmedNote}",
                                            fontSize = 11.sp,
                                            color = BreakingNewsRed,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }

                                Divider(color = SlateBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 2.dp))

                                Text(
                                    text = "⚖️ ${news.sourceAttribution.copyrightNotice}",
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    color = SlateTextMuted
                                )
                            }
                        }
                    }
                }

                // Footer Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        // Prominent Share Action Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SaffronContainer,
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, SaffronPrimary.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { ShareHelper.shareNews(context, news, isHindi) }
                                .testTag("share_news_dialog_main_btn")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 9.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = SaffronDark,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "📲 खबर शेयर करें (Play Store डाउनलोड लिंक)" else "📲 Share News (Play Store Download Link)",
                                    color = SaffronDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = onReportClick) {
                                Icon(imageVector = Icons.Outlined.Flag, contentDescription = null, tint = SlateTextMuted)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isHindi) "गलत खबर की रिपोर्ट करें" else "Report Story", color = SlateTextMuted)
                            }

                            Button(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                            ) {
                                Text(if (isHindi) "बंद करें" else "Close")
                            }
                        }
                    }
                }
            }
        }
    }
}
