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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ingestion.IngestionSyncState
import com.example.data.local.entity.IngestionLogEntity
import com.example.data.model.*
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    stats: AdminStats,
    pendingNews: List<NewsArticle>,
    reports: List<ReportItem>,
    language: String,
    ingestionState: IngestionSyncState = IngestionSyncState.Idle(),
    ingestionLogs: List<IngestionLogEntity> = emptyList(),
    onBack: () -> Unit,
    onApproveNews: (String) -> Unit,
    onRejectNews: (String) -> Unit,
    onResolveReport: (String, Boolean) -> Unit,
    onRunAutoNews: () -> Unit,
    onTriggerIngestion: (String?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isHindi = language == "hi"
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    var adminActiveTab by remember { mutableStateOf(0) } // 0: Overview, 1: Pending News, 2: Reports Queue, 3: Tools

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen")
    ) {
        // Admin Top Bar
        Surface(
            color = BharatNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) "🛡️ भारतवन एडमिन पोर्टल" else "🛡️ BharatOne Admin Portal",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Super Admin • India-wide Moderation & Controls",
                        fontSize = 11.sp,
                        color = SaffronLight
                    )
                }
                IconButton(onClick = onRunAutoNews) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = "Run Ingestion", tint = SaffronPrimary)
                }
            }
        }

        // Admin Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AdminTabItem(
                title = if (isHindi) "📊 डैशबोर्ड आँकड़े" else "📊 Overview",
                isSelected = adminActiveTab == 0,
                onClick = { adminActiveTab = 0 }
            )
            AdminTabItem(
                title = if (isHindi) "📝 समाचार समीक्षा (${pendingNews.size})" else "📝 Pending News (${pendingNews.size})",
                isSelected = adminActiveTab == 1,
                badge = if (pendingNews.isNotEmpty()) "${pendingNews.size}" else null,
                onClick = { adminActiveTab = 1 }
            )
            AdminTabItem(
                title = if (isHindi) "🚨 शिकायतें / रिपोर्ट (${reports.size})" else "🚨 Reports (${reports.size})",
                isSelected = adminActiveTab == 2,
                badge = if (reports.isNotEmpty()) "${reports.size}" else null,
                onClick = { adminActiveTab = 2 }
            )
            AdminTabItem(
                title = if (isHindi) "⚙️ सिस्टम टूल्स व AI" else "⚙️ AI & System Tools",
                isSelected = adminActiveTab == 3,
                onClick = { adminActiveTab = 3 }
            )
        }

        // Tab Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (adminActiveTab) {
                0 -> {
                    // KPI Cards Grid
                    item {
                        Text(
                            text = if (isHindi) "लाइव प्लेटफॉर्म मेट्रिक्स (Real-time Analytics)" else "Real-time Platform Metrics",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            KpiCard(
                                title = if (isHindi) "कुल उपयोगकर्ता" else "Total Users",
                                value = formatter.format(stats.totalUsers),
                                icon = Icons.Default.People,
                                color = SaffronPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            KpiCard(
                                title = if (isHindi) "सक्रिय आज" else "Active Today",
                                value = formatter.format(stats.activeUsers),
                                icon = Icons.Default.TrendingUp,
                                color = IndiaGreen,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            KpiCard(
                                title = if (isHindi) "कमरा/PG लिस्टिंग्स" else "Room Listings",
                                value = formatter.format(stats.totalRooms),
                                icon = Icons.Default.MeetingRoom,
                                color = BharatBlue,
                                modifier = Modifier.weight(1f)
                            )
                            KpiCard(
                                title = if (isHindi) "मार्केट डील्स" else "Marketplace Ads",
                                value = formatter.format(stats.totalMarketplace),
                                icon = Icons.Default.ShoppingBag,
                                color = SaffronDark,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            KpiCard(
                                title = if (isHindi) "प्रकाशित समाचार" else "Published News",
                                value = formatter.format(stats.totalNews),
                                icon = Icons.Default.Newspaper,
                                color = BreakingNewsRed,
                                modifier = Modifier.weight(1f)
                            )
                            KpiCard(
                                title = if (isHindi) "अनुमानित राजस्व" else "Total Revenue",
                                value = "₹" + formatter.format(stats.revenueRupees.toInt()),
                                icon = Icons.Default.CurrencyRupee,
                                color = IndiaGreenDark,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isHindi) "📌 शीर्ष सक्रिय जिले (Top Active Districts)" else "Top Active Districts",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("1. Balaghat, MP (1,420 listings)", fontSize = 12.sp)
                                    Text("2. Bhopal, MP (890 listings)", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("3. Indore, MP (760 listings)", fontSize = 12.sp)
                                    Text("4. Lucknow, UP (610 listings)", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Pending News Moderation
                    if (pendingNews.isEmpty()) {
                        item {
                            EmptyAdminState(message = if (isHindi) "कोई लंबित समाचार समीक्षा के लिए नहीं है" else "No news articles pending review")
                        }
                    } else {
                        items(pendingNews) { article ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = SaffronContainer
                                        ) {
                                            Text(
                                                text = if (isHindi) article.category.hindiName else article.category.displayName,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = OnSaffronContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(
                                            text = "📍 ${article.district}, ${article.state}",
                                            fontSize = 11.sp,
                                            color = SlateTextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = article.hindiHeadline,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = article.hindiSummary,
                                        fontSize = 12.sp,
                                        color = SlateTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "स्रोत: ${article.sourceName}",
                                        fontSize = 11.sp,
                                        color = BharatBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Divider(color = SlateBorder)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedButton(
                                            onClick = { onRejectNews(article.id) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BreakingNewsRed)
                                        ) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isHindi) "अस्वीकार करें" else "Reject", fontSize = 11.sp)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = { onApproveNews(article.id) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = IndiaGreen)
                                        ) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isHindi) "✓ स्वीकृत व प्रकाशित करें" else "Approve & Publish", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Reports Queue
                    if (reports.isEmpty()) {
                        item {
                            EmptyAdminState(message = if (isHindi) "सभी रिपोर्ट हल कर दी गई हैं" else "No pending abuse reports")
                        }
                    } else {
                        items(reports) { rep ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = BreakingNewsRedContainer
                                        ) {
                                            Text(
                                                text = "🚨 ${rep.reason.name}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BreakingNewsRed,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(
                                            text = rep.targetType,
                                            fontSize = 11.sp,
                                            color = SlateTextSecondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "लक्ष्य: ${rep.targetTitle}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "विवरण: ${rep.details}",
                                        fontSize = 12.sp,
                                        color = SlateTextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Divider(color = SlateBorder)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        OutlinedButton(
                                            onClick = { onResolveReport(rep.id, true) },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(if (isHindi) "खारिज करें" else "Dismiss", fontSize = 11.sp)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = { onResolveReport(rep.id, false) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = BreakingNewsRed)
                                        ) {
                                            Text(if (isHindi) "हटाएं व कार्रवाई करें" else "Remove & Resolve", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // 1. Data Ingestion Service Management (roomdekhobgt.com & jobdekhobgt.com)
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CloudSync,
                                            contentDescription = null,
                                            tint = BharatBlue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (isHindi) "🌐 स्वचालित डेटा इनजेशन सर्विस (Data Ingestion)" else "🌐 Automated Data Ingestion Service",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = "roomdekhobgt.com & jobdekhobgt.com -> Local Room DB",
                                                fontSize = 11.sp,
                                                color = SlateTextSecondary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Real-time Ingestion State Card
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = when (ingestionState) {
                                        is IngestionSyncState.Syncing -> BharatBlue.copy(alpha = 0.1f)
                                        is IngestionSyncState.Success -> IndiaGreenLight.copy(alpha = 0.2f)
                                        is IngestionSyncState.Failed -> BreakingNewsRed.copy(alpha = 0.1f)
                                        is IngestionSyncState.Idle -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        when (ingestionState) {
                                            is IngestionSyncState.Syncing -> {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(18.dp),
                                                        strokeWidth = 2.dp,
                                                        color = BharatBlue
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = ingestionState.statusMessage,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = BharatBlue
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                LinearProgressIndicator(
                                                    progress = { ingestionState.progressPercent },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    color = BharatBlue
                                                )
                                            }
                                            is IngestionSyncState.Success -> {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = IndiaGreen, modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = if (isHindi) "डेटा सफलतापूर्वक सिंक हुआ (Room DB अद्यतित)" else "Data Synced Successfully (Local Room DB Updated)",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.5.sp,
                                                        color = IndiaGreenDark
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = ingestionState.summary,
                                                    fontSize = 11.5.sp,
                                                    color = SlateTextPrimary
                                                )
                                                val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault())
                                                Text(
                                                    text = "Last Synced: ${sdf.format(Date(ingestionState.syncTimestamp))}",
                                                    fontSize = 10.5.sp,
                                                    color = SlateTextSecondary
                                                )
                                            }
                                            is IngestionSyncState.Failed -> {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = BreakingNewsRed, modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = if (isHindi) "इनजेशन त्रुटि" else "Sync Ingestion Failed",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = BreakingNewsRed
                                                    )
                                                }
                                                Text(
                                                    text = ingestionState.errorMessage,
                                                    fontSize = 11.sp,
                                                    color = SlateTextSecondary
                                                )
                                            }
                                            is IngestionSyncState.Idle -> {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, tint = IndiaGreen, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = if (isHindi) "ऑटो-इनजेशन सक्रिय • डेटाबेस अप-टू-डेट" else "Auto-Ingestion Active • Database Up-to-date",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                                Text(
                                                    text = "Background daemon checks roomdekhobgt.com and jobdekhobgt.com automatically.",
                                                    fontSize = 11.sp,
                                                    color = SlateTextSecondary
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Ingestion Action Buttons
                                Button(
                                    onClick = { onTriggerIngestion(null) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BharatNavy),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.Sync, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isHindi) "🚀 अभी सभी स्रोतों से डेटा सिंक करें (Full Ingestion)" else "🚀 Ingest & Sync All Sources Now",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onTriggerIngestion("roomdekhobgt.com") },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = if (isHindi) "🏠 केवल RoomDekho" else "🏠 Sync Rooms",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = { onTriggerIngestion("jobdekhobgt.com") },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = if (isHindi) "💼 केवल JobDekho" else "💼 Sync Jobs",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Ingestion Audit Logs
                    if (ingestionLogs.isNotEmpty()) {
                        item {
                            Text(
                                text = if (isHindi) "📋 डेटा इनजेशन ऑडिट लॉग (Ingestion Audit Logs)" else "📋 Ingestion Audit History (Room DB)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        items(ingestionLogs.take(6)) { log ->
                            val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (log.status == "SUCCESS") IndiaGreenLight.copy(alpha = 0.3f) else BreakingNewsRed.copy(alpha = 0.2f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (log.status == "SUCCESS") Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                tint = if (log.status == "SUCCESS") IndiaGreenDark else BreakingNewsRed,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = log.sourceDomain,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp
                                            )
                                            Text(
                                                text = sdf.format(Date(log.syncedAt)),
                                                fontSize = 10.sp,
                                                color = SlateTextSecondary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = log.summary,
                                            fontSize = 11.sp,
                                            color = SlateTextPrimary
                                        )
                                        Text(
                                            text = "Ingested ${log.itemsIngested} items in ${log.durationMs}ms",
                                            fontSize = 10.sp,
                                            color = SlateTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. News RSS Pipeline Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isHindi) "⚡ स्वचालित हिंदी समाचार स्क्रैपर व AI अनुवादक" else "⚡ Automated Hindi News RSS & AI Pipeline",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isHindi) "पूरे भारत और मध्य प्रदेश (बालाघाट, भोपाल, इंदौर) से ताज़ा खबरें प्राप्त कर स्वचालित रूप से श्रेणीबद्ध और हिंदी में अनुवाद करता है।" else "Collects news feeds from official Indian sources, categorizes them by state/district, and translates to Hindi.",
                                    fontSize = 12.sp,
                                    color = SlateTextSecondary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onRunAutoNews,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "अभी स्वचालित समाचार इनजेशन चलाएं" else "Execute News Ingestion Pipeline Now",
                                        fontWeight = FontWeight.Bold
                                    )
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
private fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 11.sp, color = SlateTextSecondary, fontWeight = FontWeight.SemiBold)
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AdminTabItem(
    title: String,
    isSelected: Boolean,
    badge: String? = null,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) SaffronPrimary else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyAdminState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = IndiaGreen, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, color = SlateTextSecondary, fontSize = 13.sp)
        }
    }
}
