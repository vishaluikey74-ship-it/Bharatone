package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

@Composable
fun PrivacyPolicyDialog(
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
                .fillMaxHeight(0.90f)
                .testTag("privacy_policy_dialog")
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
                        Icon(
                            imageVector = Icons.Default.Policy,
                            contentDescription = null,
                            tint = SaffronPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "गोपनीयता नीति (Privacy Policy)" else "Privacy Policy & Safety",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            fontSize = 16.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Non-Govt Entity Disclaimer
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isHindi) "गैर-सरकारी संस्था घोषणा (Non-Government Entity)" else "Non-Government Entity Disclaimer",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = if (isHindi)
                                            "BharatOne एक निजी स्वतंत्र प्लेटफॉर्म है। यह भारत सरकार, किसी भी राज्य सरकार अथवा सरकारी विभाग से संबद्ध, अधिकृत या समर्थित नहीं है। सभी सरकारी सूचनाएं व भर्ती अपडेट्स केवल जनहित में आधिकारिक सरकारी पोर्टलों (.gov.in / .nic.in) से संकलित की जाती हैं।"
                                        else
                                            "BharatOne is an independent, privately developed mobile application. It DOES NOT represent, affiliate with, or operate on behalf of any government agency or entity. All government examination and public notice updates are aggregated for public convenience from official portals (.gov.in / .nic.in).",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    // Section 1: Overview
                    item {
                        PolicySectionCard(
                            title = if (isHindi) "1. परिचय एवं उद्देश्य" else "1. Overview & Scope",
                            content = if (isHindi)
                                "BharatOne का उद्देश्य भारत के नागरिकों को स्थानीय किराये के मकान (Rooms/Flats), प्रयुक्त वस्तु खरीद-बिक्री (Marketplace), रोज़गार व भर्ती सूचनाएं और स्थानीय समाचार एक ही सुरक्षित मंच पर प्रदान करना है।"
                            else
                                "BharatOne is designed to help citizens across India discover room rentals, post marketplace listings, view job opportunities, and access regional community news within a unified, safe mobile experience."
                        )
                    }

                    // Section 2: Data Collection
                    item {
                        PolicySectionCard(
                            title = if (isHindi) "2. डेटा संग्रह एवं उपयोग (Data We Collect)" else "2. Information We Collect",
                            content = if (isHindi)
                                "• खाता जानकारी: नाम, मोबाइल नंबर (लिस्टिंग संपर्क हेतु) और ईमेल।\n• उपयोगकर्ता सामग्री: आपके द्वारा पोस्ट की गई लिस्टिंग्स, फोटो एवं पोस्ट्स।\n• हम कभी भी आपकी बैंकिंग जानकारी, आधार कार्ड, या संवेदनशील व्यक्तिगत पहचान डेटा एकत्र नहीं करते हैं।"
                            else
                                "• Account Details: Name, mobile contact number (for buyers/tenants to connect), and optional email.\n• User Content: Listings, photos, and public posts you choose to publish.\n• We DO NOT collect banking details, Aadhaar credentials, credit card info, or sensitive biometric data."
                        )
                    }

                    // Section 3: Third-Party Sharing
                    item {
                        PolicySectionCard(
                            title = if (isHindi) "3. तृतीय-पक्ष डेटा साझाकरण (Third-Party Sharing)" else "3. Data Sharing & Security",
                            content = if (isHindi)
                                "हम आपके व्यक्तिगत डेटा को किसी भी विज्ञापन नेटवर्क या बाहरी डेटा ब्रोकर के साथ नहीं बेचते हैं। डेटा केवल ऐप की मुख्य कार्यप्रणाली (संपर्क और प्रमाणीकरण) के लिए उपयोग किया जाता है।"
                            else
                                "We DO NOT sell or rent your personal information to third-party advertisers or data brokers. Data is processed strictly to facilitate listing inquiries and communication between users."
                        )
                    }

                    // Section 4: Data Deletion
                    item {
                        PolicySectionCard(
                            title = if (isHindi) "4. डेटा व खाता हटाने का अधिकार (Right to Delete)" else "4. User Control & Data Deletion",
                            content = if (isHindi)
                                "उपयोगकर्ता ऐप की प्रोफाइल सेटिंग्स में जाकर किसी भी समय अपनी लिस्टिंग्स हटा सकते हैं या 'खाता हटाएं' (Delete Account) विकल्प का उपयोग करके अपना संपूर्ण डेटा स्थायी रूप से मिटा सकते हैं।"
                            else
                                "Users have full control over their data. You can edit or delete any listing at any time, or permanently delete your account and associated data through Profile > Delete Account."
                        )
                    }

                    // Section 5: Official Sources Link
                    item {
                        PolicySectionCard(
                            title = if (isHindi) "5. सरकारी सूचना स्रोत (Official Sources)" else "5. Government Information Sources",
                            content = if (isHindi)
                                "सभी सार्वजनिक सूचनाएं निम्नलिखित आधिकारिक पोर्टलों से संकलित हैं:\n• upsc.gov.in\n• ssc.gov.in\n• ncs.gov.in\n• india.gov.in\n• mppsc.mp.gov.in\n• mponline.gov.in\n• esb.mp.gov.in\n• nhmmp.gov.in\n• balaghat.nic.in\n• moil.nic.in"
                            else
                                "Government recruitment and public notices shown for citizen convenience are sourced directly from verified public portals:\n• UPSC: https://upsc.gov.in\n• SSC: https://ssc.gov.in\n• National Career Service: https://www.ncs.gov.in\n• National Portal of India: https://www.india.gov.in\n• MPPSC: https://mppsc.mp.gov.in\n• MP Online: https://mponline.gov.in\n• MP ESB: https://esb.mp.gov.in\n• NHM MP: https://nhmmp.gov.in\n• Balaghat District: https://balaghat.nic.in\n• MOIL Ltd: https://moil.nic.in"
                        )
                    }

                    // Contact Info
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isHindi) "📬 संपर्क एवं शिकायत निवारण (Contact Us)" else "📬 Contact & Grievance Redressal",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Developer Contact: vishaluikey74@gmail.com\nBharatOne Support Team, Balaghat, Madhya Pradesh, India",
                                    fontSize = 11.sp,
                                    color = SlateTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isHindi) "स्वीकार करें / बंद करें" else "I Understand / Close")
                }
            }
        }
    }
}

@Composable
private fun PolicySectionCard(
    title: String,
    content: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
            )
        }
    }
}
