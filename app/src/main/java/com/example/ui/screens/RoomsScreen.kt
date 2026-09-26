package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.sp
import com.example.data.model.Listing
import com.example.data.model.PropertyCategory
import com.example.ui.components.CompactListingGridCard
import com.example.ui.components.ListingCard
import com.example.ui.theme.*

@Composable
fun RoomsScreen(
    rooms: List<Listing>,
    savedIds: Set<String>,
    selectedCategory: PropertyCategory?,
    selectedTenantPref: String,
    language: String,
    onCategorySelect: (PropertyCategory?) -> Unit,
    onTenantPrefSelect: (String) -> Unit,
    onListingClick: (Listing) -> Unit,
    onSaveClick: (String) -> Unit,
    onCallClick: (Listing) -> Unit,
    onChatClick: (Listing) -> Unit,
    onOpenReport: (String, String, String) -> Unit,
    onPostRoomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = language == "hi"
    var isGridView by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("rooms_screen")
    ) {
        val isLargeScreen = maxWidth >= 560.dp
        val showGrid = isGridView || isLargeScreen
        val horizontalContentPadding = if (isLargeScreen) 16.dp else 12.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Filter Bar with View Mode Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Property Category Horizontal Scroll Filter
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // "All" Filter Chip
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelect(null) },
                    label = { Text(if (isHindi) "सभी" else "All", fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SaffronPrimary,
                        selectedLabelColor = Color.White
                    )
                )

                PropertyCategory.values().forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelect(if (isSelected) null else cat) },
                        label = { Text(if (isHindi) cat.hindiName else cat.displayName, fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Grid / List View Switcher
            IconButton(
                onClick = { isGridView = !isGridView },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isGridView) Icons.Default.ViewAgenda else Icons.Default.GridView,
                    contentDescription = "Toggle Grid",
                    tint = SaffronDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Secondary Tenant Filter (Boys / Girls / Family / Students / Any)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Students", "Boys", "Girls", "Family").forEach { pref ->
                val isSelected = selectedTenantPref == pref
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) SaffronContainer else SlateSurfaceVariant,
                    modifier = Modifier.clickable { onTenantPrefSelect(pref) }
                ) {
                    Text(
                        text = when (pref) {
                            "Students" -> if (isHindi) "🎓 छात्र" else "Students"
                            "Boys" -> if (isHindi) "👦 पुरुष/Boys" else "Boys Only"
                            "Girls" -> if (isHindi) "👧 महिला/Girls" else "Girls Only"
                            "Family" -> if (isHindi) "👨‍👩‍👧‍👦 परिवार" else "Family"
                            else -> if (isHindi) "सभी किरायेदार" else "All Tenants"
                        },
                        fontSize = 10.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) OnSaffronContainer else SlateTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Rooms List or Grid
        if (rooms.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MeetingRoom,
                        contentDescription = null,
                        tint = SlateTextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isHindi) "इस श्रेणी में कोई कमरा उपलब्ध नहीं है" else "No rooms found for this filter",
                        fontWeight = FontWeight.Bold,
                        color = SlateTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onPostRoomClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                    ) {
                        Text(if (isHindi) "+ अपना कमरा लिस्ट करें" else "+ List Your Room", fontSize = 12.sp)
                    }
                }
            }
        } else if (showGrid) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = horizontalContentPadding, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rooms) { room ->
                    CompactListingGridCard(
                        listing = room,
                        isSaved = savedIds.contains(room.id),
                        isHindi = isHindi,
                        onCardClick = { onListingClick(room) },
                        onSaveClick = { onSaveClick(room.id) }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = horizontalContentPadding, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rooms) { room ->
                    ListingCard(
                        listing = room,
                        isSaved = savedIds.contains(room.id),
                        isHindi = isHindi,
                        onCardClick = { onListingClick(room) },
                        onSaveClick = { onSaveClick(room.id) },
                        onCallClick = { onCallClick(room) },
                        onChatClick = { onChatClick(room) },
                        onReportClick = { onOpenReport("LISTING", room.id, room.title) }
                    )
                }
            }
        }
        }
    }
}
