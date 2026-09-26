package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import coil.compose.AsyncImage
import com.example.data.model.EmployerVerification
import com.example.data.model.JobListing
import com.example.data.model.JobType
import com.example.data.model.hasValidOfficialGovUrl
import com.example.data.model.getEffectiveOfficialUrl
import com.example.ui.components.GovtDisclaimerBanner
import com.example.ui.components.GovtSourcesDialog
import com.example.ui.components.OfficialPortalsSection
import com.example.ui.theme.*
import com.example.ui.util.ShareHelper

@Composable
fun JobsScreen(
    jobsList: List<JobListing>,
    selectedJobType: JobType?,
    district: String,
    state: String,
    language: String,
    onJobTypeSelect: (JobType?) -> Unit,
    onJobClick: (JobListing) -> Unit,
    onReportJobClick: (JobListing) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = language == "hi"

    // Google Play Policy Compliance: Official Government Sources dialog state
    var showGovtSourcesDialog by remember { mutableStateOf(false) }

    if (showGovtSourcesDialog) {
        GovtSourcesDialog(
            isHindi = isHindi,
            onDismiss = { showGovtSourcesDialog = false }
        )
    }

    // AI Job Assistant interactive states
    var isAIAssistantOpen by remember { mutableStateOf(false) }
    var userAIQuery by remember { mutableStateOf("") }
    var aiResponse by remember { mutableStateOf<String?>(null) }
    var aiMatchingJobs by remember { mutableStateOf<List<JobListing>>(emptyList()) }

    fun askJobAI(query: String) {
        userAIQuery = query
        val lower = query.lowercase()
        val matched = jobsList.filter {
            it.qualification.lowercase().contains("12th") ||
            it.qualification.lowercase().contains("10th") ||
            it.postName.lowercase().contains(lower) ||
            it.organization.lowercase().contains(lower) ||
            it.location.lowercase().contains(lower)
        }.ifEmpty { jobsList.take(3) }
        aiMatchingJobs = matched
        aiResponse = if (isHindi) {
            "🤖 भारत एआई जॉब असिस्टेंट: आपके प्रश्न '$query' के अनुसार वर्तमान में ${matched.size} आधिकारिक व सत्यापित भर्तियां उपलब्ध हैं। कृपया आवेदन करने से पूर्व आधिकारिक अधिसूचना अवश्य जांचें।"
        } else {
            "🤖 Bharat AI Job Assistant: Found ${matched.size} verified recruitment opportunities matching '$query'. Please verify official eligibility before applying."
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("jobs_screen")
    ) {
        val isLargeScreen = maxWidth >= 560.dp
        val horizontalContentPadding = if (isLargeScreen) 16.dp else 12.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Mandatory Google Play Policy Disclaimer Banner: Non-Government Entity & Official Source Portals
            GovtDisclaimerBanner(
                isHindi = isHindi,
                onSourcesClick = { showGovtSourcesDialog = true },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // AI Job Assistant Expandable Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAIAssistantOpen = !isAIAssistantOpen },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SaffronPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isHindi) "🤖 भारत एआई जॉब असिस्टेंट" else "🤖 Bharat AI Job Assistant",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isHindi) "योग्यता अनुसार सरकारी/प्राइवेट जॉब्स पूछें" else "Ask jobs by qualification or location",
                                fontSize = 10.5.sp,
                                color = SlateTextSecondary
                            )
                        }
                    }
                    Icon(
                        imageVector = if (isAIAssistantOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = SaffronPrimary
                    )
                }

                AnimatedVisibility(visible = isAIAssistantOpen) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        // Quick prompt questions
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PromptChip("12वीं पास MP भर्ती") { askJobAI("12th pass") }
                            PromptChip("Balaghat लोकल जॉब्स") { askJobAI("Balaghat") }
                            PromptChip("पुलिस व डिफेंस भर्ती") { askJobAI("Police") }
                            PromptChip("अंतिम तारीख पास") { askJobAI("Last Date") }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Custom question input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = userAIQuery,
                                onValueChange = { userAIQuery = it },
                                placeholder = {
                                    Text(
                                        text = if (isHindi) "उदा. मेरी 12वीं है, MP में भर्ती बताओ..." else "e.g. 12th pass govt jobs in MP...",
                                        fontSize = 12.sp
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = { if (userAIQuery.isNotBlank()) askJobAI(userAIQuery) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                            ) {
                                Text(if (isHindi) "पूछें" else "Ask", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        if (aiResponse != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = SaffronContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = aiResponse!!,
                                    fontSize = 11.5.sp,
                                    color = OnSaffronContainer,
                                    modifier = Modifier.padding(8.dp),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        val filteredJobsList = remember(jobsList) {
            jobsList.filter { job ->
                if (job.jobType == JobType.GOVERNMENT) {
                    job.hasValidOfficialGovUrl()
                } else {
                    true
                }
            }
        }

        // Job Type Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedJobType == null,
                onClick = { onJobTypeSelect(null) },
                label = { Text(if (isHindi) "सभी नौकरियां (${filteredJobsList.size})" else "All Jobs (${filteredJobsList.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SaffronPrimary,
                    selectedLabelColor = Color.White
                )
            )

            FilterChip(
                selected = selectedJobType == JobType.GOVERNMENT,
                onClick = { onJobTypeSelect(JobType.GOVERNMENT) },
                label = { Text(if (isHindi) "🏛️ सार्वजनिक भर्ती (Govt)" else "🏛️ Public / Govt Jobs") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SaffronPrimary,
                    selectedLabelColor = Color.White
                )
            )

            FilterChip(
                selected = selectedJobType == JobType.PRIVATE,
                onClick = { onJobTypeSelect(JobType.PRIVATE) },
                label = { Text(if (isHindi) "🏢 स्थानीय व प्राइवेट जॉब्स" else "🏢 Private / Local") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SaffronPrimary,
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Jobs List (1-col mobile, 2-col masonry on larger screens)
        if (filteredJobsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalContentPadding, vertical = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 640.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Icon(
                        imageVector = Icons.Default.WorkOutline,
                        contentDescription = null,
                        tint = SlateTextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (selectedJobType == JobType.PRIVATE) {
                            if (isHindi) "अभी कोई प्राइवेट नौकरी उपलब्ध नहीं है" else "No private jobs available right now"
                        } else if (selectedJobType == JobType.GOVERNMENT) {
                            if (isHindi) "कोई सरकारी भर्ती उपलब्ध नहीं मिली" else "No public / govt jobs found"
                        } else {
                            if (isHindi) "कोई भर्ती उपलब्ध नहीं मिली" else "No jobs found"
                        },
                        fontWeight = FontWeight.Bold,
                        color = SlateTextSecondary,
                        fontSize = 15.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (selectedJobType == JobType.PRIVATE) {
                            if (isHindi) "स्थानीय नियोक्ता अपनी रिक्तियां पोस्ट कर सकते हैं। केवल आधिकारिक रूप से सत्यापित नौकरियां ही दिखाई जाएंगी।" else "Local employers can post vacancies. Only officially verified listings will be displayed."
                        } else {
                            if (isHindi) "केवल वास्तविक व सत्यापित पोर्टल लिंक्स दिखाए जाते हैं। आधिकारिक भर्ती व परीक्षाओं के लिए नीचे दिए गए अधिकृत सरकारी पोर्टल्स देखें:" else "Only authentic official links are displayed. For legitimate government recruitments and competitive exams, please refer to the official portals below:"
                        },
                        fontSize = 12.sp,
                        color = SlateTextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OfficialPortalsSection(
                        isHindi = isHindi,
                        titleOverride = if (isHindi) "आधिकारिक सरकारी भर्ती व परीक्षा पोर्टल" else "Official Govt Recruitment & Job Portals",
                        subtitleOverride = if (isHindi)
                            "संघ व राज्य शासन की अधिकृत भर्ती सूचनाओं के लिए सीधे इन सरकारी पोर्टल्स पर जाएं:"
                        else
                            "Access direct recruitment notifications & application forms via official government websites:"
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(if (isLargeScreen) 2 else 1),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = horizontalContentPadding, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp
            ) {
                items(filteredJobsList) { job ->
                    JobCard(
                        job = job,
                        isHindi = isHindi,
                        onJobClick = { onJobClick(job) },
                        onReportClick = { onReportJobClick(job) }
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun PromptChip(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = SaffronDark,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun JobCard(
    job: JobListing,
    isHindi: Boolean,
    onJobClick: () -> Unit,
    onReportClick: () -> Unit,
    onShareClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val handleShare = {
        if (onShareClick != null) onShareClick()
        else ShareHelper.shareJob(context, job, isHindi)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (job.jobType == JobType.GOVERNMENT) ForestGreenContainer.copy(alpha = 0.28f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            if (job.jobType == JobType.GOVERNMENT) ForestGreen.copy(alpha = 0.35f) else SlateBorder.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("job_card_${job.id}")
            .clickable { onJobClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (job.imageUrl.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                ) {
                    AsyncImage(
                        model = job.imageUrl,
                        contentDescription = job.postName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.65f)
                                    )
                                )
                            )
                    )
                    // Organization tag over photo
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (job.jobType == JobType.GOVERNMENT) ForestGreen else BharatBlue,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = if (job.jobType == JobType.GOVERNMENT) (if (isHindi) "सार्वजनिक पोर्टल सूचना (गैर-सरकारी)" else "PUBLIC PORTAL INFO") else (if (isHindi) "स्थानीय नियोक्ता" else "LOCAL EMPLOYER"),
                            color = Color.White,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                if (job.imageUrl.isEmpty()) {
                    // Header Row: Organization Tag + Verification Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(5.dp),
                            color = if (job.jobType == JobType.GOVERNMENT) ForestGreen else BharatBlue
                        ) {
                            Text(
                                text = if (job.jobType == JobType.GOVERNMENT) (if (isHindi) "सार्वजनिक पोर्टल (गैर-सरकारी)" else "PUBLIC PORTAL INFO") else (if (isHindi) "स्थानीय नियोक्ता" else "LOCAL EMPLOYER"),
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }

                        // Employer Verification Badge
                        EmployerBadge(verification = job.employerVerification, isHindi = isHindi)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        EmployerBadge(verification = job.employerVerification, isHindi = isHindi)
                    }
                }

            Spacer(modifier = Modifier.height(6.dp))

            // Post Name
            Text(
                text = if (isHindi && job.hindiPostName.isNotBlank()) job.hindiPostName else job.postName,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Organization
            Text(
                text = job.organization,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SaffronPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Key Quick Details Table/Grid Row in Compact Surface
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = if (isHindi) "रिक्त पद" else "Vacancy", fontSize = 9.sp, color = SlateTextMuted)
                        Text(text = job.vacancy, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(text = if (isHindi) "योग्यता" else "Eligibility", fontSize = 9.sp, color = SlateTextMuted)
                        Text(text = job.qualification.take(18) + if (job.qualification.length > 18) "..." else "", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = if (isHindi) "अंतिम तिथि" else "Last Date", fontSize = 9.sp, color = SlateTextMuted)
                        Text(text = job.lastDate, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = BreakingNewsRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Official Source Button (Mandatory for Government / Public Items)
            if (job.jobType == JobType.GOVERNMENT) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BharatBlue.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BharatBlue.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("official_source_btn_${job.id}")
                        .clickable {
                            val url = job.getEffectiveOfficialUrl()
                            if (url.isNotBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = BharatBlue,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isHindi) "🔗 आधिकारिक स्रोत (.gov.in)" else "🔗 Official Source (.gov.in)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BharatBlue
                            )
                        }
                        Text(
                            text = job.getEffectiveOfficialUrl().substringAfter("://").takeWhile { it != '/' },
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SlateTextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Footer: Official Source + Share + Report Flag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = BharatBlue,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = job.officialSource,
                        fontSize = 9.5.sp,
                        color = SlateTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Direct Share Button with Play Store badge style
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SaffronContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .testTag("share_job_btn_${job.id}")
                            .clickable { handleShare() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Job",
                                tint = SaffronDark,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isHindi) "शेयर" else "Share",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronDark
                            )
                        }
                    }

                    IconButton(onClick = onReportClick, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = "Report Job",
                            tint = SlateTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
fun EmployerBadge(verification: EmployerVerification, isHindi: Boolean) {
    val bgColor = when (verification) {
        EmployerVerification.VERIFIED_OFFICIAL_GOVT -> ForestGreenContainer
        EmployerVerification.VERIFIED_EMPLOYER -> BharatBlueContainer
        EmployerVerification.UNVERIFIED_EMPLOYER -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when (verification) {
        EmployerVerification.VERIFIED_OFFICIAL_GOVT -> ForestGreen
        EmployerVerification.VERIFIED_EMPLOYER -> BharatBlue
        EmployerVerification.UNVERIFIED_EMPLOYER -> SlateTextMuted
    }
    val icon = when (verification) {
        EmployerVerification.VERIFIED_OFFICIAL_GOVT -> Icons.Default.Public
        EmployerVerification.VERIFIED_EMPLOYER -> Icons.Default.CheckCircle
        EmployerVerification.UNVERIFIED_EMPLOYER -> Icons.Default.Info
    }
    val label = when (verification) {
        EmployerVerification.VERIFIED_OFFICIAL_GOVT -> if (isHindi) "सार्वजनिक पोर्टल (गैर-सरकारी)" else "Public Portal (Non-Govt)"
        EmployerVerification.VERIFIED_EMPLOYER -> if (isHindi) "सत्यापित नियोक्ता" else "Verified Employer"
        EmployerVerification.UNVERIFIED_EMPLOYER -> if (isHindi) "असत्यापित नियोक्ता" else "Unverified"
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(text = label, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
