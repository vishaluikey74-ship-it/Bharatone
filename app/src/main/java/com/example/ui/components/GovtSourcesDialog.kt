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

data class GovtSourceItem(
    val name: String,
    val hindiName: String,
    val domain: String,
    val url: String,
    val purpose: String,
    val hindiPurpose: String
)

val OFFICIAL_GOVT_SOURCES = listOf(
    GovtSourceItem(
        name = "National Portal of India",
        hindiName = "भारत सरकार का आधिकारिक पोर्टल",
        domain = "india.gov.in",
        url = "https://www.india.gov.in",
        purpose = "Single-window official access to information and public services of Govt of India",
        hindiPurpose = "भारत सरकार की सभी योजनाओं व सेवाओं का एकल आधिकारिक पोर्टल"
    ),
    GovtSourceItem(
        name = "myScheme (Govt Schemes Portal)",
        hindiName = "माई-स्कीम राष्ट्रीय सरकारी योजना पोर्टल",
        domain = "myscheme.gov.in",
        url = "https://www.myscheme.gov.in",
        purpose = "Official Government platform for citizen discovery of Central & State government schemes",
        hindiPurpose = "केंद्र व राज्य सरकार की जनकल्याणकारी योजनाओं की खोज हेतु आधिकारिक पोर्टल"
    ),
    GovtSourceItem(
        name = "Union Public Service Commission (UPSC)",
        hindiName = "संघ लोक सेवा आयोग (UPSC)",
        domain = "upsc.gov.in",
        url = "https://upsc.gov.in",
        purpose = "Central Civil Services, NDA, CDS and Indian Engineering Services",
        hindiPurpose = "सिविल सेवा, एनडीए, सीडीएस एवं केंद्रीय भर्ती परीक्षा"
    ),
    GovtSourceItem(
        name = "Staff Selection Commission (SSC)",
        hindiName = "कर्मचारी चयन आयोग (SSC)",
        domain = "ssc.gov.in",
        url = "https://ssc.gov.in",
        purpose = "CGL, CHSL, MTS, CPO and GD Constable Recruitment",
        hindiPurpose = "सीजीएल, सीएचएसएल, एमटीएस, जीडी कांस्टेबल भर्ती"
    ),
    GovtSourceItem(
        name = "National Career Service (NCS Portal)",
        hindiName = "राष्ट्रीय करियर सेवा (NCS पोर्टल)",
        domain = "ncs.gov.in",
        url = "https://www.ncs.gov.in",
        purpose = "Ministry of Labour & Employment, Government of India employment exchange",
        hindiPurpose = "श्रम एवं रोजगार मंत्रालय, भारत सरकार का रोजगार पोर्टल"
    ),
    GovtSourceItem(
        name = "Employment News / Rozgar Samachar",
        hindiName = "रोजगार समाचार (भारत सरकार)",
        domain = "employmentnews.gov.in",
        url = "https://employmentnews.gov.in",
        purpose = "Official Government weekly job gazette published by Ministry of I&B",
        hindiPurpose = "सूचना एवं प्रसारण मंत्रालय द्वारा प्रकाशित आधिकारिक साप्ताहिक रोजगार पत्रिका"
    ),
    GovtSourceItem(
        name = "Press Information Bureau (PIB)",
        hindiName = "पत्र सूचना कार्यालय (PIB)",
        domain = "pib.gov.in",
        url = "https://pib.gov.in",
        purpose = "Official releases and fact-checking agency of the Government of India",
        hindiPurpose = "भारत सरकार के प्रामाणिक प्रेस विज्ञप्ति व तथ्य जांच का मुख्य स्रोत"
    ),
    GovtSourceItem(
        name = "Madhya Pradesh Public Service Commission (MPPSC)",
        hindiName = "मध्य प्रदेश लोक सेवा आयोग (MPPSC)",
        domain = "mppsc.mp.gov.in",
        url = "https://mppsc.mp.gov.in",
        purpose = "State Civil Services, Engineering & State Administration recruitment",
        hindiPurpose = "राज्य सेवा परीक्षा, राज्य वन सेवा व राज्य प्रशासनिक भर्ती"
    ),
    GovtSourceItem(
        name = "MP Online Citizen & Recruitment Portal",
        hindiName = "एमपी ऑनलाइन सरकारी पोर्टल",
        domain = "mponline.gov.in",
        url = "https://mponline.gov.in",
        purpose = "Authorized online portal for Madhya Pradesh government applications & examinations",
        hindiPurpose = "मध्य प्रदेश शासन की ऑनलाइन भर्ती एवं नागरिक सेवाओं का पोर्टल"
    ),
    GovtSourceItem(
        name = "MP Employees Selection Board (ESB / PEB)",
        hindiName = "मध्य प्रदेश कर्मचारी चयन मंडल (ESB)",
        domain = "esb.mp.gov.in",
        url = "https://esb.mp.gov.in",
        purpose = "MP Police Constable, Sub-Inspector, Teachers, Patwari and Group exams",
        hindiPurpose = "पुलिस कांस्टेबल, सब-इंस्पेक्टर, शिक्षक, पटवारी भर्ती परीक्षाएं"
    ),
    GovtSourceItem(
        name = "National Health Mission, MP (NHM)",
        hindiName = "राष्ट्रीय स्वास्थ्य मिशन, मध्य प्रदेश (NHM)",
        domain = "nhmmp.gov.in",
        url = "https://nhmmp.gov.in",
        purpose = "Community Health Officer (CHO), Staff Nurse, ANM medical recruitments",
        hindiPurpose = "सीएचओ, स्टाफ नर्स, एएनएम स्वास्थ्य विभाग भर्तियां"
    ),
    GovtSourceItem(
        name = "District Portal Balaghat (NIC)",
        hindiName = "जिला प्रशासन बालाघाट पोर्टल (NIC)",
        domain = "balaghat.nic.in",
        url = "https://balaghat.nic.in",
        purpose = "Official district administration circulars, collectorate orders & local tenders",
        hindiPurpose = "कलेक्ट्रेट बालाघाट, स्थानीय भर्ती एवं जिला स्तरीय आधिकारिक सूचनाएं"
    ),
    GovtSourceItem(
        name = "MOIL Limited (Govt of India Enterprise)",
        hindiName = "मॉइल लिमिटेड (भारत सरकार उपक्रम)",
        domain = "moil.nic.in",
        url = "https://moil.nic.in",
        purpose = "Miniratna Public Sector Undertaking under Ministry of Steel recruitment",
        hindiPurpose = "इस्पात मंत्रालय के अधीन मिनीरत्न सार्वजनिक उपक्रम भर्ती"
    )
)

@Composable
fun GovtSourcesDialog(
    isHindi: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("govt_sources_dialog")
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isHindi) "ऐप विवरण, अस्वीकरण व स्रोत" else "About, Disclaimer & Sources",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (isHindi) "आधिकारिक सरकारी पोर्टल (.gov.in)" else "Official Government Portals (.gov.in)",
                                fontSize = 11.sp,
                                color = SlateTextSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_govt_sources_dialog_btn")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mandatory Non-Affiliation Disclaimer Banner (Exact user wording)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberContainer.copy(alpha = 0.35f)),
                    border = BorderStroke(1.2.dp, AmberNotice),
                    modifier = Modifier.fillMaxWidth()
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
                                text = if (isHindi) "⚠️ गैर-सरकारी संस्था अस्वीकरण (Disclaimer)" else "⚠️ Non-Government Entity Disclaimer",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AmberNotice
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // User-mandated Hindi disclaimer
                        Text(
                            text = "अस्वीकरण: BharatOne एक स्वतंत्र निजी ऐप है और किसी भी सरकारी संस्था से जुड़ा नहीं है। जानकारी केवल सूचना के लिए आधिकारिक सार्वजनिक स्रोतों से ली गई है। आवेदन से पहले आधिकारिक वेबसाइट पर जानकारी अवश्य जांचें।",
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = AmberNotice.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(6.dp))

                        // User-mandated English disclaimer
                        Text(
                            text = "Disclaimer: BharatOne is an independent private app and is NOT affiliated with or representing any government entity. Information is taken from official public sources for informational purposes only. Always verify on the official website before applying.",
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isHindi) "🔗 आधिकारिक सरकारी स्रोत पोर्टल (.gov.in):" else "🔗 Official Government Source Portals (.gov.in):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isHindi) "क्लिक करके आधिकारिक सरकारी वेबसाइट पर सीधे पहुंचें:" else "Click below to visit the verified official government portal directly:",
                    fontSize = 11.sp,
                    color = SlateTextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(OFFICIAL_GOVT_SOURCES) { item ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, SlateBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isHindi) item.hindiName else item.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = item.url,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BharatBlue
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isHindi) item.hindiPurpose else item.purpose,
                                        fontSize = 10.5.sp,
                                        color = SlateTextSecondary,
                                        lineHeight = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BharatBlue.copy(alpha = 0.1f),
                                    border = BorderStroke(1.dp, BharatBlue.copy(alpha = 0.3f)),
                                    modifier = Modifier.clickable {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.OpenInNew,
                                            contentDescription = "Open ${item.domain}",
                                            tint = BharatBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
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
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = SlateBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isHindi) "समझ गया (बंद करें)" else "I Understand (Close)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
