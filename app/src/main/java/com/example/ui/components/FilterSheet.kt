package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.FilterState
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSheet(
    filterState: FilterState,
    onVehicleSelect: (String) -> Unit,
    onParkingTypeSelect: (String) -> Unit,
    onPriceTierSelect: (String) -> Unit,
    onSortSelect: (String) -> Unit,
    onToggleFeature: (String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("filter_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Filter Parking",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sort By
                Text("Sort By", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Recommended", "Nearest", "Cheapest", "Highest Rated").forEach { sort ->
                        FilterChip(
                            selected = filterState.sortOption == sort,
                            onClick = { onSortSelect(sort) },
                            label = { Text(sort, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = PrimaryBlue
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Vehicle Type
                Text("Vehicle Type", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("All", "Car", "Bike", "SUV", "Van").forEach { v ->
                        FilterChip(
                            selected = filterState.vehicleType == v,
                            onClick = { onVehicleSelect(v) },
                            label = { Text(v, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = PrimaryBlue
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Parking Type
                Text("Parking Type", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("All", "Residential", "Commercial", "Basement", "Open Plot", "Private Garage").forEach { pt ->
                        FilterChip(
                            selected = filterState.parkingType == pt,
                            onClick = { onParkingTypeSelect(pt) },
                            label = { Text(pt, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = PrimaryBlue
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hourly Price Tier
                Text("Price (Hourly)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("All", "Under ₹20", "₹20 - ₹50", "₹50 - ₹100", "₹100+").forEach { tier ->
                        FilterChip(
                            selected = filterState.priceTier == tier,
                            onClick = { onPriceTierSelect(tier) },
                            label = { Text(tier, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = PrimaryBlue
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Features Checkboxes
                Text("Features & Amenities", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                Spacer(modifier = Modifier.height(6.dp))

                listOf(
                    Pair("Covered", filterState.isCoveredOnly),
                    Pair("CCTV", filterState.hasCctvOnly),
                    Pair("Guard", filterState.hasGuardOnly),
                    Pair("EV Charging", filterState.hasEvChargingOnly),
                    Pair("24/7 Access", filterState.has24x7Only)
                ).forEach { (feat, checked) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleFeature(feat) }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { onToggleFeature(feat) }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(feat, fontSize = 13.sp, color = Slate900)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Buttons: Reset and Apply
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onReset,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("filter_reset_button")
                    ) {
                        Text("Reset All")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.weight(1.2f).testTag("filter_apply_button")
                    ) {
                        Text("Apply Filters", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
