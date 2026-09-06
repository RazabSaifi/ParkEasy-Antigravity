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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@Composable
fun ProviderDashboardScreen(
    user: User?,
    spaces: List<ParkingSpace>,
    bookings: List<Booking>,
    onAddNewSpace: () -> Unit,
    onManageSpaces: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenEarnings: () -> Unit,
    onToggleSpaceStatus: (ParkingSpace) -> Unit,
    onWithdrawEarnings: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalEarnings = user?.totalEarnings ?: 24500.0
    val availableBalance = user?.availableBalance ?: 18200.0
    val totalBookingsCount = bookings.size + 85
    val activeListingsCount = spaces.count { it.status == "Active" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("provider_dashboard_screen")
    ) {
        // Top Header
        item {
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Provider Portal", fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                            Text("Welcome, ${user?.name ?: "Host"}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = Slate900)
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Verified Host", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                            }
                        }
                    }
                }
            }
        }

        // 4 Key Metrics Cards
        item {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Metric 1: Total Earnings
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Total Revenue", fontSize = 11.sp, color = Slate500, fontWeight = FontWeight.Medium)
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("₹${totalEarnings.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Slate900)
                            Text("+18% this month", fontSize = 10.sp, color = AccentEmerald, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Metric 2: Available Payout Balance
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Withdrawable", fontSize = 11.sp, color = Slate500, fontWeight = FontWeight.Medium)
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("₹${availableBalance.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Slate900)
                            Text("Instant UPI transfer", fontSize = 10.sp, color = AccentEmerald, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Metric 3: Total Bookings
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate100),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Total Bookings", fontSize = 11.sp, color = Slate500)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$totalBookingsCount", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Slate900)
                            Text("100% completion rate", fontSize = 10.sp, color = AccentEmerald)
                        }
                    }

                    // Metric 4: Occupancy Rate
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate100),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Slot Occupancy", fontSize = 11.sp, color = Slate500)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("72%", fontWeight = FontWeight.Black, fontSize = 18.sp, color = PrimaryBlue)
                            Text("Peak hours: 9AM - 7PM", fontSize = 10.sp, color = Slate500)
                        }
                    }
                }
            }
        }

        // Quick Action Buttons
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text("Quick Actions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAddNewSpace,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Slot", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onOpenCalendar,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Slate700, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Calendar", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onOpenEarnings,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(Icons.Default.Paid, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Payouts", fontSize = 12.sp)
                    }
                }
            }
        }

        // Active Listings Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("My Listed Spaces ($activeListingsCount active)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                TextButton(onClick = onManageSpaces) {
                    Text("Manage All", fontSize = 12.sp, color = PrimaryBlue)
                }
            }
        }

        items(spaces) { space ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(space.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (space.status == "Active") Color(0xFFDCFCE7) else Slate100
                            ) {
                                Text(
                                    text = space.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (space.status == "Active") Color(0xFF15803D) else Slate500,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text("${space.area}, ${space.city} • Cap: ${space.vehicleCapacity}", fontSize = 11.sp, color = Slate500)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("₹${space.hourlyPrice.toInt()}/hr • ₹${space.dailyPrice.toInt()}/day", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                    }

                    // Toggle status (Active / Paused)
                    Switch(
                        checked = space.status == "Active",
                        onCheckedChange = { onToggleSpaceStatus(space) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AccentEmerald
                        )
                    )
                }
            }
        }

        // Recent Bookings Ledger on My Spaces
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Recent Space Bookings",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Slate900,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(bookings.take(5)) { b ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFEFF6FF))
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("${b.userName} • ${b.vehicleRegNumber}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                            Text("${b.bookingDate} (${b.startTime} - ${b.endTime})", fontSize = 11.sp, color = Slate500)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("+₹${b.providerEarnings.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AccentEmerald)
                        Text("Earned", fontSize = 10.sp, color = Slate500)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
