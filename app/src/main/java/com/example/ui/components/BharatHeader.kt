package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun BharatHeader(
    state: String,
    district: String,
    area: String,
    language: String,
    searchQuery: String,
    unreadNotifCount: Int,
    onLocationClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onLanguageToggle: () -> Unit,
    onNotifClick: () -> Unit,
    onProfileClick: () -> Unit,
    onShareClick: () -> Unit = {},
    onAIStudioClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Top Row: Location Selector + AI Studio Chip + Language Switch + Notifications + Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Location Selector Chip with Live Pulse Dot
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = SaffronContainer.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .testTag("location_picker_chip")
                        .clip(RoundedCornerShape(22.dp))
                        .clickable { onLocationClick() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        // Live Green indicator
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(IndiaGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = SaffronPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (area.isNotEmpty() && area != "All Areas") "$area, $district" else "$district, $state",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSaffronContainer,
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Location",
                            tint = OnSaffronContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Actions: AI Studio + Language Toggle + Notifications + Profile Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // AI Studio Glowing Chip
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SaffronPrimary,
                        modifier = Modifier
                            .testTag("header_ai_studio_chip")
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onAIStudioClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Studio",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "AI Studio",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Language Switcher Chip (Glass style)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                        modifier = Modifier
                            .testTag("language_toggle_btn")
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onLanguageToggle() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (language == "hi") "🇮🇳 हिंदी" else "🇬🇧 ENG",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronPrimary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Notifications Icon with Badge
                    IconButton(
                        onClick = onNotifClick,
                        modifier = Modifier
                            .testTag("notification_btn")
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifCount > 0) {
                                    Badge(
                                        containerColor = BreakingNewsRed,
                                        contentColor = Color.White
                                    ) {
                                        Text("$unreadNotifCount", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Share App Button
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .testTag("header_share_btn")
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SaffronContainer.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share BharatOne",
                            tint = SaffronPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Profile Avatar with Gradient Ring
                    Box(
                        modifier = Modifier
                            .testTag("header_profile_avatar")
                            .size(36.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SaffronGradient)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🇮🇳",
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Search Bar with Modern Styling
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_search_input"),
                placeholder = {
                    Text(
                        text = if (language == "hi") "🔍 कमरा, सामान, जॉब्स व भर्ती, समाचार खोजें..." else "🔍 Search rooms, market, jobs, news...",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = SlateTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SaffronPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = SlateTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SaffronPrimary,
                    unfocusedBorderColor = SlateBorder,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            )
        }
    }
}
