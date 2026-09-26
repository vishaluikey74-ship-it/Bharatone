package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PipelineExecution
import com.example.data.model.PipelineStatus
import com.example.data.model.PipelineStepLog
import com.example.data.model.PublicationLevel
import com.example.ui.theme.*

@Composable
fun PipelineSimulatorDialog(
    executions: List<PipelineExecution>,
    language: String,
    onRunPipeline: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val isHindi = language == "hi"
    var inputUrl by remember { mutableStateOf("https://mppsc.mp.gov.in/recruitment_2026.pdf") }
    var sourceName by remember { mutableStateOf("Madhya Pradesh Public Service Commission (mppsc.mp.gov.in)") }
    var selectedExecution by remember { mutableStateOf(executions.firstOrNull()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(6.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isHindi) "🤖 AI 11-चरणीय सत्यापन पाइपलाइन" else "🤖 AI 11-Step Verification Pipeline",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isHindi) "कंटेंट नीति व कॉपीराइट अनुपालन इंजन" else "Content Policy & Compliance Engine",
                                fontSize = 10.sp,
                                color = SlateTextSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(color = SlateBorder)

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pipeline Trigger Card
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = if (isHindi) "🚀 नए स्रोत/दस्तावेज की जांच चलाएं" else "🚀 Ingest & Verify URL/Document",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )

                                OutlinedTextField(
                                    value = inputUrl,
                                    onValueChange = { inputUrl = it },
                                    label = { Text(if (isHindi) "स्रोत URL / RSS फीड / अधिसूचना लिंक" else "Source URL / Feed / PDF Link") },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = sourceName,
                                    onValueChange = { sourceName = it },
                                    label = { Text(if (isHindi) "पहचान योग्य स्रोत / विभाग नाम" else "Source / Department Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                    singleLine = true
                                )

                                Button(
                                    onClick = {
                                        onRunPipeline(inputUrl, sourceName, "Govt Recruitment / News")
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isHindi) "11-चरणीय सत्यापन चक्र प्रारंभ करें" else "Run 11-Step Compliance Check")
                                }
                            }
                        }
                    }

                    // Steps Render
                    item {
                        Text(
                            text = if (isHindi) "📊 11 सत्यापन चरण (Verification Stages)" else "📊 11 Verification Stages",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    val currentRun = executions.firstOrNull() ?: selectedExecution
                    if (currentRun != null) {
                        items(currentRun.steps) { step ->
                            StepLogCard(step = step, isHindi = isHindi)
                        }

                        // Summary Result Box
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = ForestGreenContainer.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "✅ ${currentRun.finalVerdict}",
                                            fontWeight = FontWeight.Bold,
                                            color = ForestGreen,
                                            fontSize = 12.sp
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = ForestGreen
                                        ) {
                                            Text(
                                                text = if (isHindi) currentRun.assignedPublicationLevel.hindiTitle else currentRun.assignedPublicationLevel.levelName,
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = currentRun.generatedSummaryHindi,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Footer
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                        ) {
                            Text(if (isHindi) "पूर्ण" else "Done")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepLogCard(step: PipelineStepLog, isHindi: Boolean) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Step Number Badge
            Surface(
                shape = CircleShape,
                color = when (step.status) {
                    PipelineStatus.PASSED -> ForestGreen
                    PipelineStatus.WARNING -> SaffronPrimary
                    PipelineStatus.FAILED -> BreakingNewsRed
                    PipelineStatus.PENDING -> BharatBlue
                },
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${step.stepIndex}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
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
                        text = if (isHindi) "${step.stepIndex}. ${step.hindiStepName} (${step.stepName})" else "${step.stepIndex}. ${step.stepName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = step.status.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (step.status) {
                            PipelineStatus.PASSED -> ForestGreen
                            PipelineStatus.WARNING -> SaffronPrimary
                            PipelineStatus.FAILED -> BreakingNewsRed
                            PipelineStatus.PENDING -> BharatBlue
                        }
                    )
                }
                Text(
                    text = step.details,
                    fontSize = 10.sp,
                    color = SlateTextSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
