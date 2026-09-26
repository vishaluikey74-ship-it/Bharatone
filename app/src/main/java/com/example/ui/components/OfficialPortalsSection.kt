package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class OfficialPortal(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val descEn: String,
    val descHi: String,
    val domain: String,
    val url: String,
    val icon: ImageVector
)

val OFFICIAL_GOVT_PORTALS = listOf(
    OfficialPortal(
        id = "ssc",
        nameEn = "Staff Selection Commission (SSC)",
        nameHi = "कर्मचारी चयन आयोग (SSC)",
        descEn = "Central Govt Recruitment, CGL, CHSL, MTS & GD Exams",
        descHi = "केंद्रीय सरकारी भर्ती व परीक्षाएं (CGL, CHSL, MTS)",
        domain = "ssc.gov.in",
        url = "https://ssc.gov.in",
        icon = Icons.Default.AccountBalance
    ),
    OfficialPortal(
        id = "upsc",
        nameEn = "Union Public Service Commission (UPSC)",
        nameHi = "संघ लोक सेवा आयोग (UPSC)",
        descEn = "Civil Services, NDA, CDS & Central Examinations",
        descHi = "सिविल सेवा, एनडीए, सीडीएस व राष्ट्रीय परीक्षाएं",
        domain = "upsc.gov.in",
        url = "https://upsc.gov.in",
        icon = Icons.Default.AccountBalance
    ),
    OfficialPortal(
        id = "ncs",
        nameEn = "National Career Service (NCS)",
        nameHi = "राष्ट्रीय करियर सेवा (NCS)",
        descEn = "Ministry of Labour & Employment — Verified Jobs & Careers",
        descHi = "श्रम व रोजगार मंत्रालय — अखिल भारतीय रोजगार पोर्टल",
        domain = "ncs.gov.in",
        url = "https://www.ncs.gov.in",
        icon = Icons.Default.WorkOutline
    ),
    OfficialPortal(
        id = "mp_gov",
        nameEn = "Government of Madhya Pradesh",
        nameHi = "मध्य प्रदेश शासन आधिकारिक पोर्टल",
        descEn = "Official State Gazette, Notifications & Citizen Portals",
        descHi = "राज्य शासन आधिकारिक अधिसूचनाएं, भर्ती व नागरिक सेवाएं",
        domain = "mp.gov.in",
        url = "https://mp.gov.in",
        icon = Icons.Default.Language
    )
)

@Composable
fun OfficialPortalsSection(
    isHindi: Boolean,
    modifier: Modifier = Modifier,
    titleOverride: String? = null,
    subtitleOverride: String? = null
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BharatBlueContainer.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, BharatBlue.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = BharatBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = titleOverride ?: if (isHindi) "आधिकारिक सरकारी पोर्टल लिंक्स" else "Official Government Portals",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BharatNavy
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitleOverride ?: if (isHindi)
                        "सत्यापित भर्ती व शासकीय विज्ञप्ति हेतु केवल अधिकृत .gov.in पोर्टल्स पर जाएं:"
                    else
                        "Access authentic notifications directly from verified government websites:",
                    fontSize = 11.5.sp,
                    color = SlateTextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OFFICIAL_GOVT_PORTALS.forEach { portal ->
                        OfficialPortalItem(
                            portal = portal,
                            isHindi = isHindi,
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(portal.url)).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OfficialPortalItem(
    portal: OfficialPortal,
    isHindi: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, SlateBorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("official_portal_${portal.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BharatBlue.copy(alpha = 0.1f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = portal.icon,
                            contentDescription = null,
                            tint = BharatBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = if (isHindi) portal.nameHi else portal.nameEn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = if (isHindi) portal.descHi else portal.descEn,
                        fontSize = 10.5.sp,
                        color = SlateTextMuted,
                        maxLines = 1
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = ForestGreenContainer.copy(alpha = 0.7f),
                border = BorderStroke(0.5.dp, ForestGreen.copy(alpha = 0.4f)),
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = portal.domain,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open Portal",
                        tint = ForestGreen,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }
}
