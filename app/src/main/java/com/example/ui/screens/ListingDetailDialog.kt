package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.Listing
import com.example.data.model.ListingDomain
import com.example.ui.components.VerificationBadgeChip
import com.example.ui.theme.*
import com.example.ui.util.ShareHelper
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ListingDetailDialog(
    listing: Listing,
    language: String,
    onDismiss: () -> Unit,
    onCallClick: () -> Unit,
    onChatClick: () -> Unit,
    onReportClick: () -> Unit
) {
    val isHindi = language == "hi"
    val context = LocalContext.current
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    val formattedPrice = "₹" + formatter.format(listing.price.toInt())

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
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (listing.domain == ListingDomain.ROOM_RENTAL) "🏠 Room Details" else "🛒 Product Details",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { ShareHelper.shareListing(context, listing, isHindi) },
                            modifier = Modifier.testTag("share_listing_dialog_btn")
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Photos Gallery (if available)
                    if (listing.imageUrls.isNotEmpty()) {
                        item {
                            if (listing.imageUrls.size == 1) {
                                AsyncImage(
                                    model = listing.imageUrls.first(),
                                    contentDescription = listing.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                )
                            } else {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(listing.imageUrls) { imgUrl ->
                                        AsyncImage(
                                            model = imgUrl,
                                            contentDescription = listing.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .width(220.dp)
                                                .height(160.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Price & Title
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SaffronPrimary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = formattedPrice,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                if (listing.domain == ListingDomain.ROOM_RENTAL) {
                                    Text(
                                        text = if (isHindi) " / महीना" else " / month",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = listing.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Location
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = SaffronPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${listing.area}, ${listing.district}, ${listing.state}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    if (listing.address.isNotBlank()) {
                                        Text(text = listing.address, fontSize = 11.sp, color = SlateTextSecondary)
                                    }
                                }
                            }
                        }
                    }

                    // Specific Properties (Deposit, Furnishing, Electricity, Amenities or Specs)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (listing.domain == ListingDomain.ROOM_RENTAL) {
                                if (listing.securityDeposit > 0) {
                                    DetailRow(label = if (isHindi) "सुरक्षा राशि (Deposit):" else "Security Deposit:", value = "₹${formatter.format(listing.securityDeposit.toInt())}")
                                }
                                DetailRow(label = if (isHindi) "फर्निशिंग:" else "Furnishing:", value = listing.isFurnished)
                                DetailRow(label = if (isHindi) "किरायेदार पसंद:" else "Tenant Preference:", value = listing.tenantPreference)
                                DetailRow(label = if (isHindi) "बिजली / पानी:" else "Electricity / Water:", value = listing.electricityWater)
                                DetailRow(label = if (isHindi) "पोस्ट कर्ता:" else "Posted By:", value = listing.ownerOrBroker)
                            } else {
                                if (listing.brand.isNotEmpty()) DetailRow(label = if (isHindi) "ब्रांड (Brand):" else "Brand:", value = listing.brand)
                                if (listing.model.isNotEmpty()) DetailRow(label = if (isHindi) "मॉडल (Model):" else "Model:", value = listing.model)
                                if (listing.year.isNotEmpty()) DetailRow(label = if (isHindi) "वर्ष (Year):" else "Year:", value = listing.year)
                                DetailRow(label = if (isHindi) "कंडीशन (Condition):" else "Condition:", value = listing.condition)
                            }
                        }
                    }

                    // Description
                    item {
                        Text(
                            text = if (isHindi) "विवरण (Description)" else "Description",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = listing.description,
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    // Amenities / Features tags
                    if (listing.amenities.isNotEmpty()) {
                        item {
                            Text(
                                text = if (isHindi) "उपलब्ध सुविधाएं (Amenities & Features)" else "Amenities & Features",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listing.amenities.forEach { am ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = SaffronContainer
                                    ) {
                                        Text(
                                            text = "✓ $am",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = OnSaffronContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Seller Info Card
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isHindi) "विक्रेता / मालिक की जानकारी" else "Seller / Owner Details",
                                    fontSize = 11.sp,
                                    color = SlateTextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = listing.sellerName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    VerificationBadgeChip(badge = listing.sellerBadge)
                                }
                                Text(
                                    text = "📞 ${listing.sellerPhone}",
                                    fontSize = 13.sp,
                                    color = IndiaGreenDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Action Bar
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
                        // Prominent Share Action Button with Play Store link
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SaffronContainer,
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, SaffronPrimary.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { ShareHelper.shareListing(context, listing, isHindi) }
                                .testTag("share_listing_dialog_main_btn")
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
                                    text = if (isHindi) "📲 विज्ञापन शेयर करें (Play Store लिंक सहित)" else "📲 Share Listing (with Play Store Link)",
                                    color = SaffronDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                        OutlinedButton(
                            onClick = onCallClick,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = IndiaGreenDark)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHindi) "कॉल करें" else "Call", color = IndiaGreenDark, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onChatClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHindi) "चैट करें" else "Chat Now", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = SlateTextSecondary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
