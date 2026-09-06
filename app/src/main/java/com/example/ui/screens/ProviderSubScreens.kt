package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.ParkingSpace
import com.example.data.model.User
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@Composable
fun MySpacesScreen(
    spaces: List<ParkingSpace>,
    onAddNew: () -> Unit,
    onToggleStatus: (ParkingSpace) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("My Listed Spaces", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                }
                Button(
                    onClick = onAddNew,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Space", fontSize = 12.sp)
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(spaces) { space ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(space.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                            Switch(
                                checked = space.status == "Active",
                                onCheckedChange = { onToggleStatus(space) }
                            )
                        }
                        Text("${space.address}, ${space.city}", fontSize = 12.sp, color = Slate500)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Capacity: ${space.vehicleCapacity} vehicles", fontSize = 11.sp, color = Slate700)
                            Text("₹${space.hourlyPrice.toInt()}/hr • ₹${space.dailyPrice.toInt()}/day", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryBlue)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AvailabilityScreen(
    spaces: List<ParkingSpace>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var blockedDates = remember { mutableStateListOf("2026-10-18 (Festival)", "2026-10-25 (Maintenance)") }
    var newBlockInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text("Calendar & Availability", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                    Text("Block dates for personal use or private family events", fontSize = 11.sp, color = Slate500)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Block Dates for Personal Use", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newBlockInput,
                    onValueChange = { newBlockInput = it },
                    placeholder = { Text("e.g. 2026-11-02 (Family visit)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (newBlockInput.isNotBlank()) {
                            blockedDates.add(newBlockInput)
                            newBlockInput = ""
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Block")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Currently Blocked Dates (${blockedDates.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
            Spacer(modifier = Modifier.height(8.dp))

            blockedDates.forEach { date ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(date, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color(0xFF991B1B))
                        }
                        IconButton(onClick = { blockedDates.remove(date) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Unblock", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EarningsScreen(
    user: User?,
    bookings: List<Booking>,
    onWithdraw: (Double) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var withdrawAmountText by remember { mutableStateOf("5000") }

    val totalEarned = user?.totalEarnings ?: 24500.0
    val availableBalance = user?.availableBalance ?: 18200.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Earnings & Payouts", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Balance Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Available for Withdrawal", fontSize = 12.sp, color = Slate200)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("₹${availableBalance.toInt()}", fontWeight = FontWeight.Black, fontSize = 28.sp, color = Color.White)
                    Text("Linked UPI: rohan@oksbi (Verified ✓)", fontSize = 11.sp, color = AccentEmerald)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showWithdrawDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Withdraw to Bank / UPI", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Commission Model Explanation
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Transparent Payout Model", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("You receive 90% of every booking directly into your wallet. Platform retains 10% to cover insurance, payment gateway fees, and 24/7 host protection.", fontSize = 11.sp, color = Slate600, lineHeight = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Recent Earnings Ledger", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
            Spacer(modifier = Modifier.height(10.dp))

            bookings.forEach { b ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(b.parkingTitle, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Slate900)
                            Text("Slot: ${b.bookingDate} (${b.durationHours} hrs)", fontSize = 11.sp, color = Slate500)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("+₹${b.providerEarnings.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentEmerald)
                            Text("Platform fee: ₹${b.platformFee.toInt()}", fontSize = 10.sp, color = Slate500)
                        }
                    }
                }
            }
        }
    }

    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("Withdraw Earnings") },
            text = {
                Column {
                    Text("Available Balance: ₹${availableBalance.toInt()}", fontSize = 13.sp, color = Slate700)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it },
                        label = { Text("Amount (₹)") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Transfer destination: UPI (rohan@oksbi)", fontSize = 11.sp, color = PrimaryBlue)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = withdrawAmountText.toDoubleOrNull() ?: 0.0
                        onWithdraw(amt)
                        showWithdrawDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Confirm Payout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
