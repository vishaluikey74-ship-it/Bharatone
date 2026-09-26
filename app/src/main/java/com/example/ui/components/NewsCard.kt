package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NewsArticle
import com.example.ui.theme.*

@Composable
fun NewsCard(
    news: NewsArticle,
    isHindi: Boolean,
    onNewsClick: () -> Unit,
    onShareClick: () -> Unit,
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (news.isBreaking) BreakingNewsRedContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            if (news.isBreaking) BreakingNewsRed.copy(alpha = 0.4f) else SlateBorder.copy(alpha = 0.8f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("news_card_${news.id}")
            .clickable { onNewsClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Header Row: Category Badge + Location Tag + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Category Chip
                    Surface(
                        shape = RoundedCornerShape(5.dp),
                        color = if (news.isBreaking) BreakingNewsRed else SaffronContainer
                    ) {
                        Text(
                            text = if (isHindi) news.category.hindiName else news.category.displayName,
                            color = if (news.isBreaking) Color.White else OnSaffronContainer,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    // Location Chip
                    Surface(
                        shape = RoundedCornerShape(5.dp),
                        color = SlateSurfaceVariant
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${news.district} (${news.area})",
                                fontSize = 9.5.sp,
                                color = SlateTextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Verification Status Badge
                NewsStatusBadge(status = news.verificationStatus, isHindi = isHindi)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Headline (Hindi prioritized with high readability)
            Text(
                text = if (isHindi) news.hindiHeadline else news.englishHeadline,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Summary
            Text(
                text = if (isHindi) news.hindiSummary else news.englishSummary,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SlateTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(6.dp))

            // Footer: Source Attribution + Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Source attribution
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = BharatBlue,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = news.sourceName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Share & Report
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SaffronContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .testTag("share_news_btn_${news.id}")
                            .clickable { onShareClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = SaffronDark,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isHindi) "शेयर" else "Share",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronDark
                            )
                        }
                    }

                    IconButton(onClick = onReportClick, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = "Report",
                            tint = SlateTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

