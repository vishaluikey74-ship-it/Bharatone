package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.AboutDisclaimerDialog
import com.example.ui.components.GovtSourcesDialog
import com.example.ui.components.ListingCard
import com.example.ui.components.PrivacyPolicyDialog
import com.example.ui.components.SocialPostCard
import com.example.ui.components.VerificationBadgeChip
import com.example.ui.theme.*
import com.example.ui.util.ShareHelper

@Composable
fun ProfileScreen(
    user: User,
    myListings: List<Listing>,
    myPosts: List<SocialPost>,
    savedListings: List<Listing>,
    friendRequests: List<FriendRequest>,
    language: String,
    onLanguageToggle: () -> Unit,
    onNavigateAdmin: () -> Unit,
    onListingClick: (Listing) -> Unit,
    onSaveClick: (String) -> Unit,
    onCallClick: (Listing) -> Unit,
    onChatClick: (Listing) -> Unit,
    onLikePost: (String) -> Unit,
    onCommentPost: (String) -> Unit,
    onRepostPost: (String) -> Unit,
    onAcceptFriendReq: (String) -> Unit,
    onRejectFriendReq: (String) -> Unit,
    onUpdateUser: (User) -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isHindi = language == "hi"
    val context = LocalContext.current
    var selectedProfileTab by remember { mutableStateOf(0) } // 0: Listings, 1: Posts, 2: Saved, 3: Friends
    var authDialogMode by remember { mutableStateOf<AuthMode?>(null) }
    var showGovtSourcesDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showAboutDisclaimerDialog by remember { mutableStateOf(false) }

    if (showAboutDisclaimerDialog) {
        AboutDisclaimerDialog(
            isHindi = isHindi,
            onDismiss = { showAboutDisclaimerDialog = false }
        )
    }

    if (showGovtSourcesDialog) {
        GovtSourcesDialog(
            isHindi = isHindi,
            onDismiss = { showGovtSourcesDialog = false }
        )
    }

    if (showPrivacyPolicyDialog) {
        PrivacyPolicyDialog(
            isHindi = isHindi,
            onDismiss = { showPrivacyPolicyDialog = false }
        )
    }

    if (authDialogMode != null) {
        AuthDialog(
            initialMode = authDialogMode!!,
            currentUser = user,
            language = language,
            onDismiss = { authDialogMode = null },
            onLoginSuccess = { updated ->
                onUpdateUser(updated)
                authDialogMode = null
            },
            onLogout = {
                val guest = User(
                    id = "guest_user",
                    fullName = "Guest User",
                    username = "guest",
                    email = "",
                    phone = "",
                    bio = "Guest User",
                    role = UserRole.USER,
                    verificationBadges = emptySet(),
                    isLoggedIn = false
                )
                onUpdateUser(guest)
                authDialogMode = null
            },
            onDeleteAccount = {
                onDeleteAccount()
                authDialogMode = null
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Profile Card Header
        item {
            Card(
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Big Avatar
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SaffronPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.fullName.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.fullName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                user.verificationBadges.forEach { badge ->
                                    VerificationBadgeChip(badge = badge)
                                }
                            }
                            Text(
                                text = "@${user.username} • ${user.area}, ${user.district}",
                                fontSize = 12.sp,
                                color = SlateTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = user.bio,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileStatItem(count = myListings.size, label = if (isHindi) "लिस्टिंग्स" else "Listings")
                        ProfileStatItem(count = myPosts.size, label = if (isHindi) "पोस्ट" else "Posts")
                        ProfileStatItem(count = user.friendsCount, label = if (isHindi) "मित्र (Friends)" else "Friends")
                        ProfileStatItem(count = user.followersCount, label = if (isHindi) "फॉलोअर्स" else "Followers")
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = SlateBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Account & Security Management Action Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = { authDialogMode = AuthMode.LOGIN },
                            label = { Text(if (isHindi) "🔐 खाता / लॉगिन" else "🔐 Sign In / Account", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        AssistChip(
                            onClick = { ShareHelper.shareApp(context, isHindi) },
                            label = { Text(if (isHindi) "📲 ऐप शेयर करें (Play Store)" else "📲 Share App", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = AssistChipDefaults.assistChipColors(labelColor = SaffronDark),
                            modifier = Modifier.testTag("profile_share_chip")
                        )
                        AssistChip(
                            onClick = { authDialogMode = AuthMode.PRIVACY_SETTINGS },
                            label = { Text(if (isHindi) "🛡️ गोपनीयता सेटिंग्स" else "🛡️ Privacy Settings", fontSize = 11.sp) }
                        )
                        AssistChip(
                            onClick = { showAboutDisclaimerDialog = true },
                            label = { Text(if (isHindi) "ℹ️ अबाउट व अस्वीकरण (About & Disclaimer)" else "ℹ️ About & Disclaimer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BharatBlue) },
                            modifier = Modifier.testTag("profile_about_disclaimer_chip")
                        )
                        AssistChip(
                            onClick = { showPrivacyPolicyDialog = true },
                            label = { Text(if (isHindi) "📄 गोपनीयता नीति (Policy)" else "📄 Privacy Policy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SaffronDark) },
                            modifier = Modifier.testTag("profile_privacy_policy_chip")
                        )
                        AssistChip(
                            onClick = { showGovtSourcesDialog = true },
                            label = { Text(if (isHindi) "🔗 आधिकारिक स्रोत (.gov.in)" else "🔗 Official Sources (.gov.in)", fontSize = 11.sp, color = BharatBlue) },
                            modifier = Modifier.testTag("profile_govt_sources_chip")
                        )
                        AssistChip(
                            onClick = { authDialogMode = AuthMode.OTP_VERIFY },
                            label = { Text(if (isHindi) "📱 मोबाइल OTP" else "📱 Mobile OTP", fontSize = 11.sp) }
                        )
                        AssistChip(
                            onClick = { authDialogMode = AuthMode.EMAIL_VERIFY },
                            label = { Text(if (isHindi) "✉️ ईमेल सत्यापन" else "✉️ Email Verify", fontSize = 11.sp) }
                        )
                        AssistChip(
                            onClick = { authDialogMode = AuthMode.CHANGE_PASSWORD },
                            label = { Text(if (isHindi) "🔒 पासवर्ड बदलें" else "🔒 Password", fontSize = 11.sp) }
                        )
                        AssistChip(
                            onClick = { authDialogMode = AuthMode.DELETE_ACCOUNT },
                            label = { Text(if (isHindi) "⚠️ खाता हटाएं" else "⚠️ Delete", fontSize = 11.sp, color = BreakingNewsRed) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Admin Dashboard & Language Switcher Shortcuts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Language Switcher
                        OutlinedButton(
                            onClick = onLanguageToggle,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isHindi) "🌐 English में बदलें" else "🌐 Switch to हिंदी",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Share App Button
                        Button(
                            onClick = { ShareHelper.shareApp(context, isHindi) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_share_app_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHindi) "ऐप शेयर करें" else "Share App",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Admin Dashboard (If admin role and Debug build only)
                    if (com.example.BuildConfig.DEBUG && (user.role == UserRole.ADMIN || user.role == UserRole.SUPER_ADMIN)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onNavigateAdmin,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BharatNavy),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("open_admin_dashboard_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = SaffronLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "🛡️ एडमिन डैशबोर्ड (Admin Panel)" else "🛡️ Admin Panel",
                                fontSize = 12.5.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Profile Tabs: My Listings | My Posts | Saved | Friends
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TabButton(
                    title = if (isHindi) "मेरी लिस्टिंग्स (${myListings.size})" else "Listings (${myListings.size})",
                    isSelected = selectedProfileTab == 0,
                    onClick = { selectedProfileTab = 0 }
                )
                TabButton(
                    title = if (isHindi) "मेरी पोस्ट्स (${myPosts.size})" else "Posts (${myPosts.size})",
                    isSelected = selectedProfileTab == 1,
                    onClick = { selectedProfileTab = 1 }
                )
                TabButton(
                    title = if (isHindi) "सेव्ड (${savedListings.size})" else "Saved (${savedListings.size})",
                    isSelected = selectedProfileTab == 2,
                    onClick = { selectedProfileTab = 2 }
                )
                TabButton(
                    title = if (isHindi) "रिक्वेस्ट्स (${friendRequests.size})" else "Requests (${friendRequests.size})",
                    isSelected = selectedProfileTab == 3,
                    onClick = { selectedProfileTab = 3 }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Content based on selected tab
        when (selectedProfileTab) {
            0 -> {
                // My Listings
                if (myListings.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Default.MeetingRoom,
                            message = if (isHindi) "आपने अभी तक कोई लिस्टिंग नहीं बनाई है" else "No listings posted yet"
                        )
                    }
                } else {
                    items(myListings) { listing ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            ListingCard(
                                listing = listing,
                                isSaved = false,
                                isHindi = isHindi,
                                onCardClick = { onListingClick(listing) },
                                onSaveClick = { onSaveClick(listing.id) },
                                onCallClick = { onCallClick(listing) },
                                onChatClick = { onChatClick(listing) },
                                onReportClick = { }
                            )
                        }
                    }
                }
            }
            1 -> {
                // My Posts
                if (myPosts.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Default.DynamicFeed,
                            message = if (isHindi) "आपने अभी तक कोई सोशल पोस्ट नहीं की है" else "No social posts published yet"
                        )
                    }
                } else {
                    items(myPosts) { post ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            SocialPostCard(
                                post = post,
                                isHindi = isHindi,
                                onLikeClick = { onLikePost(post.id) },
                                onCommentClick = { onCommentPost(post.id) },
                                onRepostClick = { onRepostPost(post.id) },
                                onSaveClick = { onSaveClick(post.id) },
                                onShareClick = { },
                                onAuthorClick = { }
                            )
                        }
                    }
                }
            }
            2 -> {
                // Saved Items
                if (savedListings.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Default.BookmarkBorder,
                            message = if (isHindi) "कोई पसंदीदा लिस्टिंग सेव नहीं की गई" else "No saved listings"
                        )
                    }
                } else {
                    items(savedListings) { listing ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            ListingCard(
                                listing = listing,
                                isSaved = true,
                                isHindi = isHindi,
                                onCardClick = { onListingClick(listing) },
                                onSaveClick = { onSaveClick(listing.id) },
                                onCallClick = { onCallClick(listing) },
                                onChatClick = { onChatClick(listing) },
                                onReportClick = { }
                            )
                        }
                    }
                }
            }
            3 -> {
                // Friend Requests
                if (friendRequests.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Default.PeopleOutline,
                            message = if (isHindi) "कोई नई मित्र रिक्वेस्ट नहीं है" else "No pending friend requests"
                        )
                    }
                } else {
                    items(friendRequests) { req ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(BharatNavy),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = req.fromUserName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = req.fromUserName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = req.fromUserCity, fontSize = 11.sp, color = SlateTextSecondary)
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = { onAcceptFriendReq(req.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = IndiaGreen),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Text(if (isHindi) "स्वीकार करें" else "Accept", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { onRejectFriendReq(req.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(if (isHindi) "रद्द करें" else "Reject", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = SlateTextSecondary
        )
    }
}

@Composable
private fun TabButton(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) SaffronContainer else SlateSurfaceVariant,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) OnSaffronContainer else SlateTextSecondary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = SlateTextMuted, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, color = SlateTextSecondary, fontSize = 13.sp)
        }
    }
}
