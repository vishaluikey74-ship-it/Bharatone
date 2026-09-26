package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.IndiaLocationsData
import com.example.ui.theme.SaffronContainer
import com.example.ui.theme.SaffronPrimary

@Composable
fun LocationSelectorDialog(
    currentState: String,
    currentDistrict: String,
    currentArea: String,
    language: String,
    onDismiss: () -> Unit,
    onLocationSelected: (state: String, district: String, area: String) -> Unit
) {
    var selectedState by remember { mutableStateOf(currentState) }
    var selectedDistrict by remember { mutableStateOf(currentDistrict) }
    var selectedArea by remember { mutableStateOf(currentArea) }

    val states = IndiaLocationsData.getStates()
    val districts = IndiaLocationsData.getDistricts(selectedState)
    val areas = listOf("All Areas") + IndiaLocationsData.getAreas(selectedState, selectedDistrict)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == "hi") "📍 स्थान चुनें (Select Location)" else "📍 Select Location",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // GPS Auto-detect Button
                OutlinedButton(
                    onClick = {
                        // Simulates auto-detecting user's current district (e.g. Balaghat, MP)
                        selectedState = "Madhya Pradesh"
                        selectedDistrict = "Balaghat"
                        selectedArea = "Paraswada"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auto_detect_gps_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "hi") "वर्तमान लोकेशन का उपयोग करें (GPS)" else "Use Current GPS Location",
                        color = SaffronPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // State Selector
                Text(
                    text = if (language == "hi") "1. राज्य (State):" else "1. State:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    items(states) { st ->
                        val isSelected = st == selectedState
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) SaffronContainer else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedState = st
                                    val newDistricts = IndiaLocationsData.getDistricts(st)
                                    selectedDistrict = newDistricts.firstOrNull() ?: ""
                                    selectedArea = "All Areas"
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = st,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) SaffronPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // District Selector
                Text(
                    text = if (language == "hi") "2. जिला (District):" else "2. District:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    items(districts) { dist ->
                        val isSelected = dist == selectedDistrict
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) SaffronContainer else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedDistrict = dist
                                    selectedArea = "All Areas"
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = dist,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) SaffronPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Area / Tehsil Selector
                Text(
                    text = if (language == "hi") "3. क्षेत्र / कस्बा (Area / Tehsil):" else "3. Area / Tehsil:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                ) {
                    items(areas) { ar ->
                        val isSelected = ar == selectedArea
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) SaffronContainer else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedArea = ar }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = ar,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) SaffronPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Apply Button
                Button(
                    onClick = {
                        onLocationSelected(selectedState, selectedDistrict, selectedArea)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_location_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text(
                        text = if (language == "hi") "स्थान लागू करें (Apply Location)" else "Apply Location",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
