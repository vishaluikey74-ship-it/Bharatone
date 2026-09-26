package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab

@Composable
fun BharatBottomNavBar(
    currentTab: AppTab,
    language: String,
    unreadChatCount: Int,
    onTabSelected: (AppTab) -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Home
            NavBarItem(
                icon = if (currentTab == AppTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                label = BharatStrings.t("home", language),
                isSelected = currentTab == AppTab.HOME,
                testTag = "nav_home",
                onClick = { onTabSelected(AppTab.HOME) }
            )

            // 2. Rooms & PG
            NavBarItem(
                icon = if (currentTab == AppTab.ROOMS) Icons.Filled.MeetingRoom else Icons.Outlined.MeetingRoom,
                label = BharatStrings.t("rooms", language),
                isSelected = currentTab == AppTab.ROOMS,
                testTag = "nav_rooms",
                onClick = { onTabSelected(AppTab.ROOMS) }
            )

            // 3. Market
            NavBarItem(
                icon = if (currentTab == AppTab.MARKETPLACE) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                label = BharatStrings.t("market", language),
                isSelected = currentTab == AppTab.MARKETPLACE,
                testTag = "nav_market",
                onClick = { onTabSelected(AppTab.MARKETPLACE) }
            )

            // 4. Center Floating Gradient "+ POST" FAB
            Box(
                modifier = Modifier
                    .testTag("nav_post_fab")
                    .offset(y = (-12).dp)
                    .size(54.dp)
                    .shadow(12.dp, CircleShape, spotColor = SaffronPrimary)
                    .clip(CircleShape)
                    .background(SaffronGradient)
                    .clickable { onPostClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Post or Listing",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // 5. News
            NavBarItem(
                icon = if (currentTab == AppTab.NEWS) Icons.Filled.Newspaper else Icons.Outlined.Newspaper,
                label = BharatStrings.t("news", language),
                isSelected = currentTab == AppTab.NEWS,
                testTag = "nav_news",
                onClick = { onTabSelected(AppTab.NEWS) }
            )

            // 6. Chat
            NavBarItem(
                icon = if (currentTab == AppTab.CHAT) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                label = BharatStrings.t("chat", language),
                isSelected = currentTab == AppTab.CHAT,
                badgeCount = unreadChatCount,
                testTag = "nav_chat",
                onClick = { onTabSelected(AppTab.CHAT) }
            )

            // 7. Profile
            NavBarItem(
                icon = if (currentTab == AppTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                label = BharatStrings.t("profile", language),
                isSelected = currentTab == AppTab.PROFILE,
                testTag = "nav_profile",
                onClick = { onTabSelected(AppTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun NavBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    badgeCount: Int = 0,
    testTag: String,
    onClick: () -> Unit
) {
    val animatedIconColor by animateColorAsState(
        targetValue = if (isSelected) SaffronPrimary else SlateTextSecondary,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "navIconColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 28.dp)
            ) { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isSelected) SaffronContainer.copy(alpha = 0.8f) else Color.Transparent
                )
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            BadgedBox(
                badge = {
                    if (badgeCount > 0) {
                        Badge(
                            containerColor = BreakingNewsRed,
                            contentColor = Color.White
                        ) {
                            Text("$badgeCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = animatedIconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isSelected) SaffronDark else SlateTextSecondary,
            maxLines = 1
        )
    }
}


