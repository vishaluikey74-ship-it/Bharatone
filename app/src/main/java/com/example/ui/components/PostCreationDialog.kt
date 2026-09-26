package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ListingDomain
import com.example.ui.theme.*

@Composable
fun PostCreationDialog(
    language: String,
    onDismiss: () -> Unit,
    onSelectCreateRoom: () -> Unit,
    onSelectCreateMarketplace: () -> Unit,
    onSelectCreateSocialPost: () -> Unit,
    onSelectSubmitNews: () -> Unit
) {
    val isHindi = language == "hi"

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "➕ नया क्या पोस्ट करना चाहते हैं?" else "➕ What would you like to post?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Room / PG / Property Rental
                PostOptionRow(
                    icon = Icons.Default.MeetingRoom,
                    iconBg = SaffronContainer,
                    iconTint = SaffronPrimary,
                    title = if (isHindi) "🏠 कमरा / PG / फ्लैट किराए पर दें" else "🏠 Room / PG / Flat Rental",
                    subtitle = if (isHindi) "छात्र कमरा, 1/2 BHK, PG, हॉस्टल या दुकान लिस्ट करें" else "List student room, flat, PG, hostel or shop",
                    testTag = "post_option_room",
                    onClick = onSelectCreateRoom
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Sell Product / OLX Marketplace
                PostOptionRow(
                    icon = Icons.Default.ShoppingBag,
                    iconBg = BharatBlueContainer,
                    iconTint = BharatBlue,
                    title = if (isHindi) "🛒 सामान बेचें (OLX स्टाइल मार्केट)" else "🛒 Sell Product (OLX Marketplace)",
                    subtitle = if (isHindi) "कार, बाइक, मोबाइल, इलेक्ट्रॉनिक्स, ट्रैक्टर, फर्नीचर बेचें" else "Sell cars, bikes, mobiles, agri equipment, furniture",
                    testTag = "post_option_marketplace",
                    onClick = onSelectCreateMarketplace
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Social Post
                PostOptionRow(
                    icon = Icons.Default.DynamicFeed,
                    iconBg = IndiaGreenContainer,
                    iconTint = IndiaGreen,
                    title = if (isHindi) "🐦 सोशल पोस्ट बनाएं (Twitter/X स्टाइल)" else "🐦 Create Social Post (X / Twitter)",
                    subtitle = if (isHindi) "अपने विचार, फोटो, हैशटैग और स्थानीय अपडेट शेयर करें" else "Share thoughts, photos, hashtags & local updates",
                    testTag = "post_option_social",
                    onClick = onSelectCreateSocialPost
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Submit Citizen News
                PostOptionRow(
                    icon = Icons.Default.Newspaper,
                    iconBg = BreakingNewsRedContainer,
                    iconTint = BreakingNewsRed,
                    title = if (isHindi) "📝 स्थानीय समाचार भेजें (नागरिक पत्रकार)" else "📝 Submit Local News (Citizen Journalism)",
                    subtitle = if (isHindi) "अपने गांव/शहर की ब्रेकिंग या आवश्यक खबर सबमिट करें" else "Submit local news report for editor moderation",
                    testTag = "post_option_news",
                    onClick = onSelectSubmitNews
                )
            }
        }
    }
}

@Composable
private fun PostOptionRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateTextSecondary),
                    fontSize = 11.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SlateTextMuted
            )
        }
    }
}
