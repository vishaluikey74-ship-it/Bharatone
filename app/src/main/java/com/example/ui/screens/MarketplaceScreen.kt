package com.example.ui.screens

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
import com.example.data.model.MarketCategory
import com.example.ui.components.CompactListingGridCard
import com.example.ui.components.ListingCard
import com.example.ui.theme.*

@Composable
fun MarketplaceScreen(
    items: List<Listing>,
    savedIds: Set<String>,
    selectedCategory: MarketCategory?,
    language: String,
    onCategorySelect: (MarketCategory?) -> Unit,
    onListingClick: (Listing) -> Unit,
    onSaveClick: (String) -> Unit,
    onCallClick: (Listing) -> Unit,
    onChatClick: (Listing) -> Unit,
    onOpenReport: (String, String, String) -> Unit,
    onSellItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = language == "hi"
    var isGridView by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("marketplace_screen")
    ) {
        val isLargeScreen = maxWidth >= 560.dp
        val showGrid = isGridView || isLargeScreen
        val horizontalContentPadding = if (isLargeScreen) 16.dp else 12.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Market Category Filter Chips with View Mode Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    label = { Text(if (isHindi) "सभी सामान" else "All Items", fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BharatBlue,
                        selectedLabelColor = Color.White
                    )
                )

                MarketCategory.values().forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelect(if (isSelected) null else cat) },
                        label = { Text(if (isHindi) cat.hindiName else cat.displayName, fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BharatBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            IconButton(
                onClick = { isGridView = !isGridView },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isGridView) Icons.Default.ViewAgenda else Icons.Default.GridView,
                    contentDescription = "Toggle Grid",
                    tint = BharatBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Product Items List or Grid
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = SlateTextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isHindi) "इस श्रेणी में कोई सामान नहीं मिला" else "No marketplace items found",
                        fontWeight = FontWeight.Bold,
                        color = SlateTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onSellItemClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BharatBlue)
                    ) {
                        Text(if (isHindi) "+ सामान बेचने के लिए लिस्ट करें" else "+ Sell Your Product", fontSize = 12.sp)
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
                items(items) { item ->
                    CompactListingGridCard(
                        listing = item,
                        isSaved = savedIds.contains(item.id),
                        isHindi = isHindi,
                        onCardClick = { onListingClick(item) },
                        onSaveClick = { onSaveClick(item.id) }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = horizontalContentPadding, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    ListingCard(
                        listing = item,
                        isSaved = savedIds.contains(item.id),
                        isHindi = isHindi,
                        onCardClick = { onListingClick(item) },
                        onSaveClick = { onSaveClick(item.id) },
                        onCallClick = { onCallClick(item) },
                        onChatClick = { onChatClick(item) },
                        onReportClick = { onOpenReport("LISTING", item.id, item.title) }
                    )
                }
            }
        }
        }
    }
}
