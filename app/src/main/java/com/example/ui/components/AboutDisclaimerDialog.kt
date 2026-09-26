package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

data class MandatoryOfficialSource(
    val name: String,
    val hindiName: String,
    val url: String,
    val description: String,
    val hindiDescription: String
)

val MANDATORY_GOVT_SOURCES = listOf(
    MandatoryOfficialSource(
        name = "National Portal of India",
        hindiName = "भारत सरकार का आधिकारिक पोर्टल",
        url = "https://www.india.gov.in",
        description = "Single-window access to all official services, acts, and announcements of Govt of India",
        hindiDescription = "भारत सरकार की सभी आधिकारिक सेवाओं, नियमों व विज्ञप्तियों का मुख्य पोर्टल"
    ),
    MandatoryOfficialSource(
        name = "myScheme (National Government Schemes Portal)",
        hindiName = "माई-स्कीम राष्ट्रीय सरकारी योजना पोर्टल",
        url = "https://www.myscheme.gov.in",
        description = "Official national platform for discovery of Central and State government welfare schemes",
        hindiDescription = "केंद्र व राज्य सरकारों की जनकल्याणकारी योजनाओं की खोज हेतु आधिकारिक पोर्टल"
    ),
    MandatoryOfficialSource(
        name = "Union Public Service Commission (UPSC)",
        hindiName = "संघ लोक सेवा आयोग (UPSC)",
        url = "https://upsc.gov.in",
        description = "Civil Services, Engineering Services, NDA, CDS and Central recruitment examinations",
        hindiDescription = "सिविल सेवा, एनडीए, सीडीएस एवं केंद्रीय भर्ती परीक्षाओं का आधिकारिक आयोग"
    ),
    MandatoryOfficialSource(
        name = "Staff Selection Commission (SSC)",
        hindiName = "कर्मचारी चयन आयोग (SSC)",
        url = "https://ssc.gov.in",
        description = "CGL, CHSL, MTS, CPO, GD Constable and technical recruitment for Central Ministries",
        hindiDescription = "सीजीएल, सीएचएसएल, एमटीएस व केंद्रीय मंत्रालयों के रिक्त पदों हेतु आयोग"
    ),
    MandatoryOfficialSource(
        name = "National Career Service (NCS Portal)",
        hindiName = "राष्ट्रीय करियर सेवा (NCS पोर्टल)",
        url = "https://www.ncs.gov.in",
        description = "Ministry of Labour & Employment, Govt of India employment portal and job fairs",
        hindiDescription = "श्रम एवं रोजगार मंत्रालय, भारत सरकार का राष्ट्रव्यापी रोजगार पोर्टल"
    ),
    MandatoryOfficialSource(
        name = "Employment News / Rozgar Samachar",
        hindiName = "रोजगार समाचार (भारत सरकार)",
        url = "https://employmentnews.gov.in",
        description = "Official weekly government job gazette published by Ministry of Information and Broadcasting",
        hindiDescription = "सूचना एवं प्रसारण मंत्रालय द्वारा प्रकाशित आधिकारिक साप्ताहिक रोजगार पत्रिका"
    ),
    MandatoryOfficialSource(
        name = "Press Information Bureau (PIB)",
        hindiName = "पत्र सूचना कार्यालय (PIB)",
        url = "https://pib.gov.in",
        description = "Nodal agency of the Government of India to disseminate information to print and electronic media",
        hindiDescription = "भारत सरकार के मंत्रालयों व नीतियों के प्रामाणिक प्रेस नोट व तथ्य जांच पोर्टल"
    )
)

/**
 * "About & Disclaimer" Dialog / Page
 * Accessible from settings/menu, disclaimer banners, and header info button.
 * Strictly adheres to Google Play's Misleading Claims policy.
 */
@Composable
fun AboutDisclaimerDialog(
    isHindi: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("about_disclaimer_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SaffronContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isHindi) "ऐप परिचय व अस्वीकरण" else "About & Disclaimer",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = if (isHindi) "आधिकारिक स्रोत व गैर-सरकारी घोषणा" else "Official Sources & Non-Affiliation",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SlateTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_about_disclaimer_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = SlateBorder)
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // App Description Box
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isHindi) "📱 BharatOne के बारे में" else "📱 About BharatOne",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isHindi) {
                                        "BharatOne एक स्वतंत्र निजी कम्युनिटी प्लेटफॉर्म है जो नागरिकों को कमरा/पीजी किराये, स्थानीय मार्केटप्लेस, और सार्वजनिक सूचनाओं के सारांश एक स्थान पर प्रदान करता है।"
                                    } else {
                                        "BharatOne is an independent private community platform designed to provide citizens with room/PG rentals, verified local marketplace, and public informational summaries in one place."
                                    },
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = SlateTextSecondary
                                )
                            }
                        }
                    }

                    // Mandatory Disclaimer Section (Exact English & Hindi User Prompt Strings)
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = AmberContainer.copy(alpha = 0.35f)),
                            border = BorderStroke(1.2.dp, AmberNotice)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = AmberNotice,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (isHindi) "अनिवार्य अस्वीकरण (Mandatory Disclaimer)" else "Mandatory Disclaimer (Non-Government Entity)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AmberNotice
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Hindi Disclaimer
                                Text(
                                    text = "अस्वीकरण: BharatOne एक स्वतंत्र निजी ऐप है और किसी भी सरकारी संस्था से जुड़ा नहीं है। जानकारी केवल सूचना के लिए आधिकारिक सार्वजनिक स्रोतों से ली गई है। आवेदन से पहले आधिकारिक वेबसाइट पर जानकारी अवश्य जांचें।",
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = AmberNotice.copy(alpha = 0.4f))
                                Spacer(modifier = Modifier.height(8.dp))

                                // English Disclaimer
                                Text(
                                    text = "Disclaimer: BharatOne is an independent private app and is NOT affiliated with or representing any government entity. Information is taken from official public sources for informational purposes only. Always verify on the official website before applying.",
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Section: Official Government Sources
                    item {
                        Text(
                            text = if (isHindi) "🔗 आधिकारिक सरकारी स्रोत (.gov.in):" else "🔗 Official Government Sources (.gov.in):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) {
                                "नीचे दिए गए सभी लिंक भारत सरकार के आधिकारिक पोर्टल हैं। किसी भी भर्ती, परीक्षा या योजना के लिए केवल इन्हीं आधिकारिक वेबसाइट्स पर जाकर आवेदन करें:"
                            } else {
                                "All links below are official Govt of India portals. Please verify details and submit applications exclusively on these official government websites:"
                            },
                            fontSize = 11.5.sp,
                            color = SlateTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // 7 Mandatory Official Sources as Clickable Cards
                    items(MANDATORY_GOVT_SOURCES) { source ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, SlateBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    openUrl(context, source.url)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isHindi) source.hindiName else source.name,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = source.url,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BharatBlue
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isHindi) source.hindiDescription else source.description,
                                        fontSize = 10.5.sp,
                                        lineHeight = 14.sp,
                                        color = SlateTextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BharatBlue.copy(alpha = 0.1f),
                                    border = BorderStroke(1.dp, BharatBlue.copy(alpha = 0.4f)),
                                    modifier = Modifier.clickable {
                                        openUrl(context, source.url)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.OpenInNew,
                                            contentDescription = "Open ${source.url}",
                                            tint = BharatBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isHindi) "खोलें" else "Open",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BharatBlue
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Additional District / State Official Portals
                    item {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                            border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (isHindi) "📍 राज्य व जिला आधिकारिक पोर्टल (.gov.in / .nic.in):" else "📍 State & District Official Portals:",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• https://mppsc.mp.gov.in (MPPSC)\n• https://esb.mp.gov.in (MP ESB / PEB)\n• https://mponline.gov.in (MP Online)\n• https://balaghat.nic.in (Balaghat District NIC)\n• https://nhmmp.gov.in (NHM MP)",
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = SlateTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = SlateBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Close button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isHindi) "समझ गया (बंद करें)" else "Understood (Close)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}
