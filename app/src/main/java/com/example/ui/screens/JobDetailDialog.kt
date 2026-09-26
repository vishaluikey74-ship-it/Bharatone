package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.model.EmployerVerification
import com.example.data.model.JobListing
import com.example.data.model.JobType
import com.example.data.model.getEffectiveOfficialUrl
import com.example.data.model.hasValidOfficialGovUrl
import com.example.ui.components.GovtDisclaimerBanner
import com.example.ui.theme.*
import com.example.ui.util.ShareHelper

@Composable
fun JobDetailDialog(
    job: JobListing,
    language: String,
    onDismiss: () -> Unit,
    onReportClick: () -> Unit
) {
    val isHindi = language == "hi"
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmployerBadge(verification = job.employerVerification, isHindi = isHindi)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                ShareHelper.shareJob(
                                    context = context,
                                    jobTitle = if (isHindi) job.hindiPostName else job.postName,
                                    company = job.companyName ?: job.organization,
                                    location = "${job.location}, ${job.district}",
                                    salary = job.salary,
                                    isHindi = isHindi
                                )
                            }
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
                    // Job Banner Image (if available)
                    if (job.imageUrl.isNotBlank()) {
                        item {
                            AsyncImage(
                                model = job.imageUrl,
                                contentDescription = job.postName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                        }
                    }

                    // Post Name & Organization
                    item {
                        Column {
                            Text(
                                text = if (isHindi && job.hindiPostName.isNotBlank()) job.hindiPostName else job.postName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 26.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = job.organization,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = SaffronPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            if (job.location.isNotBlank()) {
                                Text(
                                    text = "📍 ${job.location}",
                                    fontSize = 12.sp,
                                    color = SlateTextSecondary
                                )
                            }
                        }
                    }

                    // Strict Policy Fraud Protection Warning
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = BreakingNewsRedContainer.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = BreakingNewsRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "⚠️ धोखाधड़ी से सावधान: किसी भी जॉब के लिए कभी अग्रिम शुल्क न दें। हमेशा आधिकारिक पोर्टल से ही आवेदन करें।" else "⚠️ Anti-Fraud Warning: Never pay advance money or registration fees for any job. Verify on official portal only.",
                                    fontSize = 11.sp,
                                    color = BreakingNewsRed,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // Mandatory Google Play Policy: Non-Government Entity Disclaimer
                    if (job.jobType == JobType.GOVERNMENT) {
                        item {
                            GovtDisclaimerBanner(
                                isHindi = isHindi,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Structured Parameters Table (MANDATORY POLICY FIELDS)
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "📋 महत्वपूर्ण विवरण व पात्रता" else "📋 Key Details & Eligibility",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                JobDetailRow(label = if (isHindi) "कुल पद (Vacancies)" else "Total Vacancies", value = job.vacancy)
                                JobDetailRow(label = if (isHindi) "शैक्षणिक योग्यता (Eligibility)" else "Qualification", value = job.qualification)
                                JobDetailRow(label = if (isHindi) "आयु सीमा (Age Limit)" else "Age Limit", value = job.ageLimit)
                                JobDetailRow(label = if (isHindi) "वेतनमान (Salary/Pay Scale)" else "Salary", value = job.salary)
                                JobDetailRow(label = if (isHindi) "आवेदन प्रारंभ तिथि" else "Application Start", value = job.applicationStartDate)
                                JobDetailRow(label = if (isHindi) "आवेदन की अंतिम तिथि" else "Last Date", value = job.lastDate, isHighlight = true)
                                if (job.examDate.isNotBlank()) {
                                    JobDetailRow(label = if (isHindi) "परीक्षा / चयन तिथि" else "Exam Date", value = job.examDate)
                                }
                                JobDetailRow(label = if (isHindi) "आवेदन शुल्क (Fee)" else "Application Fee", value = job.applicationFee)
                                JobDetailRow(label = if (isHindi) "चयन प्रक्रिया" else "Selection Process", value = job.selectionProcess)
                            }
                        }
                    }

                    // Unconfirmed Info / Disclaimer (MANDATORY POLICY)
                    if (job.isUnconfirmedMissingInfo) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = "ℹ️ इस जानकारी की आधिकारिक पुष्टि उपलब्ध नहीं है। कृपया आधिकारिक नोटिफिकेशन देखें।",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }

                    // Official Source & Notification Links
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = BharatBlueContainer.copy(alpha = 0.35f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BharatBlue.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = null,
                                        tint = BharatBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "आधिकारिक स्रोत पोर्टल: ${job.officialSource}" else "Official Source Portal: ${job.officialSource}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = BharatBlue
                                    )
                                }

                                if (job.officialNotificationUrl.isNotBlank()) {
                                    Text(
                                        text = "🌐 आधिकारिक URL: ${job.officialNotificationUrl}",
                                        fontSize = 11.sp,
                                        color = SlateTextSecondary
                                    )

                                    Button(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.officialNotificationUrl))
                                                context.startActivity(intent)
                                            } catch (_: Exception) {}
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BharatBlue),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("btn_open_official_source_url")
                                    ) {
                                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isHindi) "🔗 आधिकारिक पोर्टल पर खोलें (.gov / .nic)" else "🔗 Open Official Portal (.gov / .nic)",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (job.jobType == JobType.PRIVATE && !job.contactPhone.isNullOrBlank()) {
                                    Text(
                                        text = "📞 संपर्क नंबर: ${job.contactPhone}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Footer Actions: "View Official Notification" + "Apply Now" + "Report"
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Prominent Share Option with Play Store link
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SaffronContainer,
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, SaffronPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    ShareHelper.shareJob(
                                        context = context,
                                        jobTitle = if (isHindi) job.hindiPostName else job.postName,
                                        company = job.companyName ?: job.organization,
                                        location = "${job.location}, ${job.district}",
                                        salary = job.salary,
                                        isHindi = isHindi
                                    )
                                }
                                .testTag("share_job_dialog_full_btn")
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
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "📲 भर्ती शेयर करें (Play Store डाउनलोड लिंक)" else "📲 Share Job (Play Store Download Link)",
                                    color = SaffronDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Official Source Primary Action (Mandatory for Government / Public Items)
                        if (job.jobType == JobType.GOVERNMENT) {
                            Button(
                                onClick = {
                                    val url = job.getEffectiveOfficialUrl()
                                    if (url.isNotBlank()) {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BharatBlue),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("detail_official_source_btn")
                            ) {
                                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "🔗 आधिकारिक स्रोत खोलें (.gov.in / .nic.in)" else "🔗 Open Official Source (.gov.in / .nic.in)",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.officialNotificationUrl))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isHindi) "नोटिफिकेशन" else "Notice PDF", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.officialApplyLink))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isHindi) "आधिकारिक पोर्टल" else "Apply Official", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(onClick = onReportClick) {
                                Icon(imageVector = Icons.Outlined.Flag, contentDescription = null, tint = SlateTextMuted, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isHindi) "फर्जी नौकरी / स्कैम की शिकायत करें" else "Report Fake Job / Scam", fontSize = 11.sp, color = SlateTextMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JobDetailRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = SlateTextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) BreakingNewsRed else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.2f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
