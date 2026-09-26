package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.SaffronPrimary
import java.util.UUID

@Composable
fun CreateListingDialog(
    domain: ListingDomain,
    state: String,
    district: String,
    area: String,
    language: String,
    currentUser: User,
    onDismiss: () -> Unit,
    onSubmitListing: (Listing) -> Unit
) {
    val isHindi = language == "hi"

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var depositStr by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Room specific
    var selectedPropCat by remember { mutableStateOf(PropertyCategory.STUDENT_ROOM) }
    var selectedFurnished by remember { mutableStateOf("Semi-Furnished") }
    var selectedTenantPref by remember { mutableStateOf("Students / Anyone") }

    // Marketplace specific
    var selectedMarketCat by remember { mutableStateOf(MarketCategory.MOBILES) }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("Used - Good") }

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
                    Text(
                        text = if (domain == ListingDomain.ROOM_RENTAL)
                            (if (isHindi) "🏠 कमरा / PG लिस्ट करें" else "🏠 List Room / PG / Flat")
                        else
                            (if (isHindi) "🛒 सामान बेचें (Post Product)" else "🛒 Sell Product (OLX Style)"),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
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
                    // Category Selection
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
                            if (domain == ListingDomain.ROOM_RENTAL) {
                                PropertyCategory.values().forEach { cat ->
                                    val isSelected = selectedPropCat == cat
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedPropCat = cat },
                                        label = { Text(if (isHindi) cat.hindiName else cat.displayName) }
                                    )
                                }
                            } else {
                                MarketCategory.values().forEach { cat ->
                                    val isSelected = selectedMarketCat == cat
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedMarketCat = cat },
                                        label = { Text(if (isHindi) cat.hindiName else cat.displayName) }
                                    )
                                }
                            }
                        }
                    }

                    // Title
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text(if (isHindi) "शीर्षक (Title) *" else "Title *") },
                            placeholder = {
                                Text(
                                    if (domain == ListingDomain.ROOM_RENTAL)
                                        (if (isHindi) "उदा: बालाघाट कॉलेज के पास 1 BHK कमरा" else "e.g. Spacious 1 BHK Room near College")
                                    else
                                        (if (isHindi) "उदा: Redmi Note 12 Pro 5G (8GB/128GB)" else "e.g. Maruti Suzuki Swift VXI 2021")
                                )
                            },
                            modifier = Modifier.fillMaxWidth().testTag("listing_form_title"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Price & Deposit Row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = priceStr,
                                onValueChange = { priceStr = it },
                                label = { Text(if (domain == ListingDomain.ROOM_RENTAL) (if (isHindi) "मासिक किराया (₹) *" else "Rent / Month (₹) *") else (if (isHindi) "कीमत (₹) *" else "Price (₹) *")) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).testTag("listing_form_price"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (domain == ListingDomain.ROOM_RENTAL) {
                                OutlinedTextField(
                                    value = depositStr,
                                    onValueChange = { depositStr = it },
                                    label = { Text(if (isHindi) "सुरक्षा राशि (₹)" else "Deposit (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    // Domain specific fields
                    if (domain == ListingDomain.ROOM_RENTAL) {
                        item {
                            OutlinedTextField(
                                value = selectedFurnished,
                                onValueChange = { selectedFurnished = it },
                                label = { Text(if (isHindi) "फर्निशिंग स्थिति" else "Furnishing Status") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = selectedTenantPref,
                                onValueChange = { selectedTenantPref = it },
                                label = { Text(if (isHindi) "किरायेदार पसंद (छात्र / परिवार / सभी)" else "Tenant Preference") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    } else {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = brand,
                                    onValueChange = { brand = it },
                                    label = { Text(if (isHindi) "ब्रांड (Brand)" else "Brand") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = year,
                                    onValueChange = { year = it },
                                    label = { Text(if (isHindi) "वर्ष (Year)" else "Year") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = condition,
                                onValueChange = { condition = it },
                                label = { Text(if (isHindi) "कंडीशन (Condition)" else "Condition") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // Address / Landmark
                    item {
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text(if (isHindi) "पता व लैंडमार्क (Address / Landmark)" else "Address & Landmark") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Description
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(if (isHindi) "पूरा विवरण (Description)" else "Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
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
                            val priceVal = priceStr.toDoubleOrNull() ?: 0.0
                            val depositVal = depositStr.toDoubleOrNull() ?: 0.0
                            val listing = Listing(
                                id = "user_list_${UUID.randomUUID().toString().take(8)}",
                                domain = domain,
                                title = title,
                                description = description,
                                price = priceVal,
                                securityDeposit = depositVal,
                                state = state,
                                district = district,
                                area = if (area == "All Areas") "Main Market" else area,
                                address = address,
                                sellerId = currentUser.id,
                                sellerName = currentUser.fullName,
                                sellerPhone = currentUser.phone,
                                sellerBadge = currentUser.verificationBadges.firstOrNull(),
                                propertyCategory = if (domain == ListingDomain.ROOM_RENTAL) selectedPropCat else null,
                                isFurnished = selectedFurnished,
                                tenantPreference = selectedTenantPref,
                                marketCategory = if (domain == ListingDomain.MARKETPLACE) selectedMarketCat else null,
                                brand = brand,
                                model = model,
                                year = year,
                                condition = condition
                            )
                            onSubmitListing(listing)
                        },
                        enabled = title.isNotBlank() && priceStr.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .testTag("submit_listing_confirm_btn")
                    ) {
                        Text(
                            text = if (isHindi) "✓ अभी प्रकाशित करें (Publish Now)" else "✓ Publish Listing Now",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
