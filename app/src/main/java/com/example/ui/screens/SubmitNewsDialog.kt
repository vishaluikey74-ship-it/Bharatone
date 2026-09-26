package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.NewsCategory
import com.example.ui.theme.SaffronPrimary

@Composable
fun SubmitNewsDialog(
    state: String,
    district: String,
    area: String,
    language: String,
    onDismiss: () -> Unit,
    onSubmitNews: (headline: String, summary: String, content: String, category: NewsCategory, source: String) -> Unit
) {
    val isHindi = language == "hi"

    var headline by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var fullContent by remember { mutableStateOf("") }
    var sourceName by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(NewsCategory.LOCAL) }

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
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isHindi) "📝 स्थानीय समाचार सबमिट करें" else "📝 Submit Citizen News",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "📍 $area, $district, $state",
                            fontSize = 11.sp,
                            color = SaffronPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider()

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Category
                    item {
                        Text(
                            text = if (isHindi) "श्रेणी चुनें (Select Category):" else "Select Category:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            NewsCategory.values().forEach { cat ->
                                val isSelected = selectedCat == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCat = cat },
                                    label = { Text("${cat.emoji} " + if (isHindi) cat.hindiName else cat.displayName) }
                                )
                            }
                        }
                    }

                    // Headline
                    item {
                        OutlinedTextField(
                            value = headline,
                            onValueChange = { headline = it },
                            label = { Text(if (isHindi) "मुख्य शीर्षक (Headline) *" else "Headline *") },
                            placeholder = { Text(if (isHindi) "उदा: बालाघाट में नई जल संरक्षण योजना शुरू..." else "Enter news headline...") },
                            modifier = Modifier.fillMaxWidth().testTag("news_form_headline"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Summary
                    item {
                        OutlinedTextField(
                            value = summary,
                            onValueChange = { summary = it },
                            label = { Text(if (isHindi) "संक्षिप्त विवरण (Summary) *" else "Summary *") },
                            placeholder = { Text(if (isHindi) "2-3 पंक्तियों में मुख्य बात लिखें..." else "Short summary in 2-3 lines...") },
                            modifier = Modifier.fillMaxWidth().testTag("news_form_summary"),
                            minLines = 2,
                            maxLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Source
                    item {
                        OutlinedTextField(
                            value = sourceName,
                            onValueChange = { sourceName = it },
                            label = { Text(if (isHindi) "स्रोत / संवाददाता का नाम (Source Name)" else "Source / Reporter Name") },
                            placeholder = { Text(if (isHindi) "उदा: स्थानीय नागरिक संवाददाता / जिला प्रेस नोट" else "e.g. Local Citizen Reporter") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Full Content
                    item {
                        OutlinedTextField(
                            value = fullContent,
                            onValueChange = { fullContent = it },
                            label = { Text(if (isHindi) "पूरी खबर (Full Content)" else "Full Content") },
                            placeholder = { Text(if (isHindi) "पूरी जानकारी यहाँ विस्तार से लिखें..." else "Full news story details...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4,
                            maxLines = 8,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Submit Button
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            val src = if (sourceName.isNotBlank()) sourceName else "Citizen Reporter ($district)"
                            onSubmitNews(headline, summary, fullContent, selectedCat, src)
                        },
                        enabled = headline.isNotBlank() && summary.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .testTag("submit_news_confirm_btn")
                    ) {
                        Text(
                            text = if (isHindi) "✓ संपादकीय समीक्षा के लिए भेजें" else "✓ Submit for Editor Review",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
