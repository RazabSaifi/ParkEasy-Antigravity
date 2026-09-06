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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueDark
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@Composable
fun LandingScreen(
    onFindParking: () -> Unit,
    onListSpace: () -> Unit,
    onSearchDestination: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchLocation by remember { mutableStateOf("") }
    var selectedVehicle by remember { mutableStateOf("Car") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Section with Clean Light Aesthetics
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Trust Tag
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AccentEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "India's Community Parking Marketplace",
                            color = PrimaryBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Headline
                Text(
                    text = "Find parking before you reach.",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 36.sp
                    ),
                    color = Slate900
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subheadline
                Text(
                    text = "Discover verified parking spaces near your destination and book your spot in advance.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate600,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action CTAs
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onFindParking,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(50.dp)
                            .testTag("landing_find_parking_button")
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Find Parking", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(
                        onClick = onListSpace,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("landing_list_space_button")
                    ) {
                        Icon(Icons.Default.AddBusiness, contentDescription = null, tint = Slate700, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("List Space", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Slate900)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Embedded Search Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100),
                    modifier = Modifier.fillMaxWidth().testTag("landing_search_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Where do you want to park?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = searchLocation,
                            onValueChange = { searchLocation = it },
                            placeholder = { Text("Area, landmark, or city (e.g. Indiranagar, CP)") },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryBlue)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("landing_location_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Vehicle Type Selector
                        Text("Vehicle Type", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Car", "Bike", "SUV").forEach { v ->
                                val selected = selectedVehicle == v
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.White,
                                    border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue) else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedVehicle = v }
                                ) {
                                    Text(
                                        text = v,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) PrimaryBlue else Slate700,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { onSearchDestination(searchLocation, selectedVehicle) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("landing_search_submit_button")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Search Parking", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // How ParkEasy Works Section
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(
                text = "How ParkEasy Works",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Slate900
            )
            Text(
                text = "Seamless parking experience for commuters and space owners",
                fontSize = 13.sp,
                color = Slate500
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Step 1
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(38.dp).clip(CircleShape).background(Color(0xFFEFF6FF))
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("1. Search & Select", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                    Text("Search available spots near your destination, filter by covered/EV, and choose your preferred slot.", fontSize = 12.sp, color = Slate600)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step 2
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(38.dp).clip(CircleShape).background(Color(0xFFDCFCE7))
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("2. Book & Get Digital Pass", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                    Text("Pay securely via UPI, Card, or Wallet and receive an instant QR parking pass with guaranteed reservation.", fontSize = 12.sp, color = Slate600)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step 3
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(38.dp).clip(CircleShape).background(Color(0xFFFEF3C7))
                ) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("3. Navigate & Park with Ease", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                    Text("Drive straight to the verified location, show your digital QR pass at the gate, and park safely.", fontSize = 12.sp, color = Slate600)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Popular Cities
            Text(
                text = "Available in Top Cities",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Slate900
            )
            Spacer(modifier = Modifier.height(10.dp))

            val cities = listOf("Bengaluru", "New Delhi", "Mumbai", "Hyderabad", "Pune")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cities.take(3).forEach { city ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Slate100,
                        modifier = Modifier.weight(1f).clickable { onSearchDestination(city, "Car") }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.LocationCity, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(city, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                        }
                    }
                }
            }
        }
    }
}
