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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Listing
import com.example.data.model.ListingDomain
import com.example.ui.theme.*
import com.example.ui.util.ShareHelper
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ListingCard(
    listing: Listing,
    isSaved: Boolean,
    isHindi: Boolean,
    onCardClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCallClick: () -> Unit,
    onChatClick: () -> Unit,
    onReportClick: () -> Unit,
    onShareClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val handleShare = {
        if (onShareClick != null) onShareClick()
        else ShareHelper.shareListing(context, listing, isHindi)
    }
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    val formattedPrice = "₹" + formatter.format(listing.price.toInt())

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("listing_card_${listing.id}")
            .clickable { onCardClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Image Box with Real Photos & Gradient Fallback (110dp height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = if (listing.domain == ListingDomain.ROOM_RENTAL)
                                listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                            else
                                listOf(Color(0xFF1E293B), Color(0xFF0E253F))
                        )
                    )
            ) {
                // If real listing photo is available, show AsyncImage with dark scrim
                if (listing.imageUrls.isNotEmpty()) {
                    AsyncImage(
                        model = listing.imageUrls.first(),
                        contentDescription = listing.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Dark top and bottom scrim for contrast
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.55f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.65f)
                                    )
                                )
                            )
                    )
                } else {
                    // Large Category Icon Art in background fallback
                    Icon(
                        imageVector = if (listing.domain == ListingDomain.ROOM_RENTAL) Icons.Default.MeetingRoom else Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier
                            .size(65.dp)
                            .align(Alignment.Center)
                    )
                }

                // Top Left: Domain & Promoted Pill & Balaghat Source Badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (listing.isPromoted) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldStar
                        ) {
                            Text(
                                text = "⭐ FEATURED",
                                color = Color.Black,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Text(
                            text = if (listing.domain == ListingDomain.ROOM_RENTAL) {
                                if (isHindi) listing.propertyCategory?.hindiName ?: "कमरा" else listing.propertyCategory?.displayName ?: "Room"
                            } else {
                                if (isHindi) listing.marketCategory?.hindiName ?: "सामान" else listing.marketCategory?.displayName ?: "Item"
                            },
                            color = Color.White,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    if (listing.imageUrls.size > 1) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = "📷 ${listing.imageUrls.size}",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Top Right: Share & Save Bookmark Icons
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = handleShare,
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .testTag("share_listing_img_btn_${listing.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) SaffronPrimary else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Bottom Left: Price Tag overlay with Gradient Pill
                Surface(
                    shape = RoundedCornerShape(topEnd = 12.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(SaffronGradient, RoundedCornerShape(topEnd = 12.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = formattedPrice,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.5.sp,
                            color = Color.White
                        )
                        if (listing.domain == ListingDomain.ROOM_RENTAL) {
                            Text(
                                text = if (isHindi) " /माह" else " /mo",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Body Details - Compact Spacing
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                // Title
                Text(
                    text = listing.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Location line
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SaffronPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${listing.area}, ${listing.district}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateTextSecondary,
                            fontSize = 10.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Specific Specs Pills - Compact
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (listing.domain == ListingDomain.ROOM_RENTAL) {
                        SpecPill(text = listing.isFurnished)
                        SpecPill(text = listing.tenantPreference)
                        SpecPill(text = listing.ownerOrBroker)
                    } else {
                        if (listing.brand.isNotEmpty()) SpecPill(text = listing.brand)
                        if (listing.year.isNotEmpty()) SpecPill(text = listing.year)
                        SpecPill(text = listing.condition)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = SlateBorderLight)
                Spacer(modifier = Modifier.height(6.dp))

                // Seller & Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Seller info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = listing.sellerName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        VerificationBadgeChip(badge = listing.sellerBadge)
                    }

                    // Action buttons: Share + Call + Chat
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Share button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SaffronContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .height(28.dp)
                                .clickable { handleShare() }
                                .testTag("share_listing_action_${listing.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp)
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
                                    fontSize = 10.sp,
                                    color = SaffronDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Call button
                        OutlinedButton(
                            onClick = onCallClick,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = ForestGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (isHindi) "कॉल" else "Call",
                                fontSize = 10.sp,
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Chat button
                        Button(
                            onClick = onChatClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Chat",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (isHindi) "चैट" else "Chat",
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactListingGridCard(
    listing: Listing,
    isSaved: Boolean,
    isHindi: Boolean,
    onCardClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    val formattedPrice = "₹" + formatter.format(listing.price.toInt())

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("compact_listing_${listing.id}")
            .clickable { onCardClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Visual header with photo support
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(
                        Brush.linearGradient(
                            if (listing.domain == ListingDomain.ROOM_RENTAL)
                                listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                            else
                                listOf(Color(0xFF1E293B), Color(0xFF0E253F))
                        )
                    )
            ) {
                if (listing.imageUrls.isNotEmpty()) {
                    AsyncImage(
                        model = listing.imageUrls.first(),
                        contentDescription = listing.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.45f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.6f)
                                    )
                                )
                            )
                    )
                } else {
                    Icon(
                        imageVector = if (listing.domain == ListingDomain.ROOM_RENTAL) Icons.Default.MeetingRoom else Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp).align(Alignment.Center)
                    )
                }

                // Price badge
                Surface(
                    shape = RoundedCornerShape(topEnd = 10.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(SaffronGradient, RoundedCornerShape(topEnd = 10.dp))
                ) {
                    Text(
                        text = formattedPrice,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Top Right: Share & Bookmark icons
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val context = LocalContext.current
                    IconButton(
                        onClick = { ShareHelper.shareListing(context, listing, isHindi) },
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = null,
                            tint = if (isSaved) SaffronPrimary else Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // Compact body
            Column(modifier = Modifier.padding(7.dp)) {
                Text(
                    text = listing.title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${listing.area}, ${listing.district}",
                        fontSize = 9.5.sp,
                        color = SlateTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecPill(text: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = SlateSurfaceVariant
    ) {
        Text(
            text = text,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Medium,
            color = SlateTextSecondary,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
        )
    }
}

