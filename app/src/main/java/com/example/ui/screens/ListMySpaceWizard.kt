package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Videocam
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.data.util.IndianLocations
import com.example.data.util.UserLocation
import com.example.ui.components.LocationSelectorTriggerButton
import com.example.ui.components.SearchableLocationSelectorModal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ParkingSpace
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.CharcoalSubtle
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ListMySpaceWizard(
    onCancel: () -> Unit,
    onPublish: (ParkingSpace) -> Unit,
    modifier: Modifier = Modifier,
    onRequestGpsLocation: () -> Unit = {},
    userLocation: UserLocation? = null
) {
    var step by remember { mutableIntStateOf(1) }
    val totalSteps = 10

    // Form fields & Location Hierarchy
    var state by remember { mutableStateOf("Karnataka") }
    var city by remember { mutableStateOf("Bengaluru") }
    var area by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("560038") }
    var latitude by remember { androidx.compose.runtime.mutableDoubleStateOf(userLocation?.latitude ?: 12.9716) }
    var longitude by remember { androidx.compose.runtime.mutableDoubleStateOf(userLocation?.longitude ?: 77.5946) }
    var isGpsPinned by remember { mutableStateOf(userLocation != null) }

    var showStatePicker by remember { mutableStateOf(false) }
    var showCityPicker by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(userLocation) {
        if (userLocation != null) {
            latitude = userLocation.latitude
            longitude = userLocation.longitude
            if (address.isBlank()) address = userLocation.name
            if (area.isBlank()) area = userLocation.locality
            val resolvedCity = userLocation.city.ifBlank { "Bengaluru" }
            val resolvedState = IndianLocations.findStateForCity(resolvedCity) ?: "Karnataka"
            state = resolvedState
            city = resolvedCity
            isGpsPinned = true
        }
    }
    var parkingType by remember { mutableStateOf("Residential") }
    var supportedVehicles by remember { mutableStateOf(setOf("Car", "Bike", "SUV")) }
    var capacity by remember { mutableIntStateOf(2) }

    // Features
    var hasCctv by remember { mutableStateOf(true) }
    var hasGuard by remember { mutableStateOf(false) }
    var isCovered by remember { mutableStateOf(true) }
    var hasEv by remember { mutableStateOf(false) }
    var hasLighting by remember { mutableStateOf(true) }
    var has24x7 by remember { mutableStateOf(true) }
    var easyEntry by remember { mutableStateOf(true) }

    // Photos
    var selectedPhotoUri by remember { mutableStateOf<String?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedPhotoUri = uri.toString()
        }
    }
    var photoEntranceSelected by remember { mutableStateOf(true) }
    var photoSpaceSelected by remember { mutableStateOf(true) }
    var photoSurroundingsSelected by remember { mutableStateOf(true) }

    // Availability
    var daysText by remember { mutableStateOf("Monday to Sunday") }
    var timingsText by remember { mutableStateOf("24 Hours") }

    // Pricing
    var hourlyPrice by remember { mutableStateOf("40") }
    var dailyPrice by remember { mutableStateOf("250") }
    var monthlyPrice by remember { mutableStateOf("3000") }

    // Rules & Description
    var rulesText by remember { mutableStateOf("Park within marked lines. No commercial repairs. Clean after yourself.") }
    var descriptionText by remember { mutableStateOf("Safe, well-lit covered parking slot with CCTV security. Easy drive-in access.") }

    val isDark = MaterialTheme.colorScheme.background == CharcoalBackground

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = PrimaryBlue
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("list_space_wizard")
    ) {
        // Clean Header with Thin ParkEasy Blue Progress Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (step > 1) step-- else onCancel() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "List your parking space",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) CharcoalElevated else Color(0xFFEFF6FF)
                    ) {
                        Text(
                            text = "Step $step of $totalSteps",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = PrimaryBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Thin, Clean ParkEasy Blue Progress Bar
                LinearProgressIndicator(
                    progress = { step.toFloat() / totalSteps.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = PrimaryBlue,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)

        // Step Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (step) {
                1 -> {
                    // Step 1: Location
                    StepHeader(
                        title = "Where is your parking space located?",
                        subtitle = "Enter exact street address and landmark so seekers can navigate accurately."
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // GPS Pinning Card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRequestGpsLocation() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "Pin GPS",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isGpsPinned) "📍 Live GPS Coordinates Pinned!" else "📍 Pin Current GPS Coordinates",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = PrimaryBlue
                                )
                                Text(
                                    text = if (isGpsPinned) "Pin: ${String.format("%.4f", latitude)}, ${String.format("%.4f", longitude)} · Drivers open Google Maps turn-by-turn navigation directly to this pin!" else "Tap to auto-fill address and pin exact GPS coordinates for Google Maps navigation.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Listing Name / Title") },
                        placeholder = { Text("e.g. Covered Driveway Slot - Indiranagar") },
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Street Address / Building / Plot No.") },
                        placeholder = { Text("e.g. 14, 12th Main Road, HAL 2nd Stage") },
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Location Hierarchy: State -> Dependent City
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // State Trigger
                        LocationSelectorTriggerButton(
                            label = "State",
                            selectedValue = state,
                            placeholder = "Select State",
                            onClick = { showStatePicker = true },
                            modifier = Modifier.weight(1f),
                            testTag = "wizard_state_trigger"
                        )

                        // Dependent City Trigger
                        LocationSelectorTriggerButton(
                            label = "City",
                            selectedValue = city,
                            placeholder = "Select City",
                            onClick = { showCityPicker = true },
                            enabled = state.isNotBlank(),
                            modifier = Modifier.weight(1f),
                            testTag = "wizard_city_trigger"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = area,
                            onValueChange = { area = it },
                            label = { Text("Locality / Area") },
                            placeholder = { Text("e.g. Indiranagar") },
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors,
                            modifier = Modifier.weight(1.2f)
                        )
                        OutlinedTextField(
                            value = pincode,
                            onValueChange = { pincode = it },
                            label = { Text("Pincode") },
                            placeholder = { Text("560038") },
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Modals for State & City Selection
                    if (showStatePicker) {
                        SearchableLocationSelectorModal(
                            title = "Select State",
                            subtitle = "Country: India (🇮🇳)",
                            items = IndianLocations.getAllStates(),
                            selectedItem = state,
                            onItemSelected = { selectedState ->
                                state = selectedState
                                val validCities = IndianLocations.getCitiesForState(selectedState)
                                if (!IndianLocations.isValidCityForState(selectedState, city)) {
                                    city = validCities.firstOrNull() ?: ""
                                }
                            },
                            onDismiss = { showStatePicker = false },
                            placeholderSearch = "Search state (e.g. Uttar Pradesh, Karnataka...)"
                        )
                    }

                    if (showCityPicker) {
                        val citiesInSelectedState = remember(state) {
                            IndianLocations.getCitiesForState(state)
                        }
                        SearchableLocationSelectorModal(
                            title = "Select City in $state",
                            subtitle = "Showing cities belonging to $state",
                            items = citiesInSelectedState,
                            selectedItem = city,
                            onItemSelected = { selectedCity ->
                                city = selectedCity
                            },
                            onDismiss = { showCityPicker = false },
                            placeholderSearch = "Search city in $state..."
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Your exact house number is kept private until a seeker confirms their reservation.",
                                fontSize = 12.sp,
                                color = Slate600,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                2 -> {
                    // Step 2: Parking Type
                    StepHeader(
                        title = "What type of parking space is it?",
                        subtitle = "Select the category that best describes your property."
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val parkingTypes = listOf(
                        Triple("Residential", "Driveway / Villa / Apartment complex", Icons.Default.Roofing),
                        Triple("Commercial", "Commercial complex / Shop front / Tech park", Icons.Default.DirectionsCar),
                        Triple("Basement", "Dedicated underground basement parking", Icons.Default.LocalParking),
                        Triple("Open Plot", "Gated open surface land / Private compound", Icons.Default.LocationOn),
                        Triple("Private Garage", "Lockable shutter or enclosed garage", Icons.Default.Lock),
                        Triple("Other", "Specialized open-air or shaded slot", Icons.Default.LocalParking)
                    )

                    parkingTypes.forEach { (typeKey, subtitle, icon) ->
                        val selected = parkingType == typeKey
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) Color(0xFFEFF6FF) else Color.White,
                            border = BorderStroke(
                                if (selected) 1.5.dp else 1.dp,
                                if (selected) PrimaryBlue else Slate200
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clickable { parkingType = typeKey }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) Color.White else Slate100)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (selected) PrimaryBlue else Slate600,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = typeKey,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (selected) PrimaryBlue else Slate900
                                    )
                                    Text(
                                        text = subtitle,
                                        fontSize = 12.sp,
                                        color = Slate500
                                    )
                                }

                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Step 3: Vehicle Compatibility
                    StepHeader(
                        title = "Which vehicles can park here?",
                        subtitle = "Select all supported vehicle categories for this slot."
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val vehicleOptions = listOf(
                        Triple("Car", "Sedans, Hatchbacks, Small EVs", Icons.Default.DirectionsCar),
                        Triple("Bike", "Two-wheelers, Motorcycles, Scooters", Icons.Default.TwoWheeler),
                        Triple("SUV", "Full-size SUVs, MPVs, Compact Crossovers", Icons.Default.DirectionsCar),
                        Triple("Van", "Passenger Vans & Light Commercial", Icons.Default.DirectionsCar)
                    )

                    vehicleOptions.forEach { (vKey, vSub, icon) ->
                        val isChecked = supportedVehicles.contains(vKey)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isChecked) Color(0xFFEFF6FF) else Color.White,
                            border = BorderStroke(
                                if (isChecked) 1.5.dp else 1.dp,
                                if (isChecked) PrimaryBlue else Slate200
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clickable {
                                    supportedVehicles = if (isChecked) supportedVehicles - vKey else supportedVehicles + vKey
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        supportedVehicles = if (isChecked) supportedVehicles - vKey else supportedVehicles + vKey
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = PrimaryBlue,
                                        uncheckedColor = Slate400
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = vKey,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = if (isChecked) PrimaryBlue else Slate900
                                    )
                                    Text(
                                        text = vSub,
                                        fontSize = 12.sp,
                                        color = Slate500
                                    )
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // Step 4: Capacity
                    StepHeader(
                        title = "How many vehicles can park simultaneously?",
                        subtitle = "Total independent slots available at this location."
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 2, 3, 4, 6, 10).forEach { cap ->
                            val selected = capacity == cap
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) PrimaryBlue else Color.White,
                                border = BorderStroke(1.dp, if (selected) PrimaryBlue else Slate200),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { capacity = cap }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        text = "$cap",
                                        color = if (selected) Color.White else Slate900,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = if (cap == 1) "slot" else "slots",
                                        color = if (selected) Color.White.copy(alpha = 0.8f) else Slate500,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Currently configured for $capacity vehicle${if (capacity > 1) "s" else ""} at the same time.",
                                fontSize = 13.sp,
                                color = Slate700
                            )
                        }
                    }
                }

                5 -> {
                    // Step 5: Facilities / Amenities
                    StepHeader(
                        title = "Facilities & Safety Amenities",
                        subtitle = "Spaces with verified amenities receive 2.5x more reservations."
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val amenities = listOf(
                        AmenityItem("CCTV Camera Surveillance", "24/7 continuous video monitoring", Icons.Default.Videocam, hasCctv) { hasCctv = !hasCctv },
                        AmenityItem("Security Guard On Duty", "Physical guard gate access", Icons.Default.Security, hasGuard) { hasGuard = !hasGuard },
                        AmenityItem("Covered Roof Protection", "Shielded from harsh sun and rain", Icons.Default.Roofing, isCovered) { isCovered = !isCovered },
                        AmenityItem("EV Charging Available", "Standard or fast electric charging port", Icons.Default.ElectricCar, hasEv) { hasEv = !hasEv },
                        AmenityItem("Well Lit at Night", "LED floodlights for safe late-night parking", Icons.Default.LightMode, hasLighting) { hasLighting = !hasLighting },
                        AmenityItem("24/7 Unrestricted Access", "Enter and exit anytime without host assistance", Icons.Default.Schedule, has24x7) { has24x7 = !has24x7 },
                        AmenityItem("Wide Easy Entry / Exit", "Ample driveway clearance for effortless turning", Icons.Default.DirectionsCar, easyEntry) { easyEntry = !easyEntry }
                    )

                    amenities.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (item.checked) Color(0xFFEFF6FF) else Color.White,
                            border = BorderStroke(
                                if (item.checked) 1.5.dp else 1.dp,
                                if (item.checked) PrimaryBlue else Slate200
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { item.onToggle() }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.checked,
                                    onCheckedChange = { item.onToggle() },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = PrimaryBlue,
                                        uncheckedColor = Slate400
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = if (item.checked) PrimaryBlue else Slate900
                                    )
                                    Text(
                                        text = item.subtitle,
                                        fontSize = 11.sp,
                                        color = Slate500
                                    )
                                }
                            }
                        }
                    }
                }

                6 -> {
                    // Step 6: Photos
                    StepHeader(
                        title = "Upload Space Photos",
                        subtitle = "Clear photos showing your slot, entrance, and street help seekers park with confidence."
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Interactive Custom Photo Upload & Camera Action Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isDark) CharcoalSurface else Color.White,
                        border = BorderStroke(1.5.dp, if (selectedPhotoUri != null) AccentEmerald else PrimaryBlue.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        if (selectedPhotoUri != null) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black)
                                ) {
                                    AsyncImage(
                                        model = selectedPhotoUri,
                                        contentDescription = "Selected Spot Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = AccentEmerald,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "✓ Photo Attached",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Primary spot photo ready",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) Color.White else Slate900
                                    )
                                    Text(
                                        text = "Tap to Change",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PrimaryBlue.copy(alpha = 0.1f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Upload Photo",
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Take Photo or Upload from Device",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isDark) Color.White else Slate900
                                    )
                                    Text(
                                        text = "Tap to capture with camera or choose from gallery",
                                        fontSize = 11.sp,
                                        color = Slate500
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryBlue
                                ) {
                                    Text(
                                        text = "Select",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val photoItems = listOf(
                        Triple("1. Parking Slot Photo", "Clear angle showing the empty slot and surface", photoSpaceSelected),
                        Triple("2. Entrance / Gate Photo", "Driveway approach and street entrance", photoEntranceSelected),
                        Triple("3. Surroundings / Street Photo", "Street landmark for easy recognition", photoSurroundingsSelected)
                    )

                    photoItems.forEach { (name, hint, isAttached) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Slate200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFEFF6FF))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Slate900
                                        )
                                        Text(
                                            text = hint,
                                            fontSize = 11.sp,
                                            color = Slate500
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "✓ Verified",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                7 -> {
                    // Step 7: Availability & Timings
                    StepHeader(
                        title = "When is your space available?",
                        subtitle = "Set operating days and hours for seamless bookings."
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Available Days", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Slate700)
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Monday to Sunday", "Monday to Friday", "Weekends Only (Sat-Sun)").forEach { preset ->
                            val sel = daysText == preset
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (sel) Color(0xFFEFF6FF) else Color.White,
                                border = BorderStroke(1.dp, if (sel) PrimaryBlue else Slate200),
                                modifier = Modifier.clickable { daysText = preset }
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 12.sp,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (sel) PrimaryBlue else Slate700,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = daysText,
                        onValueChange = { daysText = it },
                        label = { Text("Custom Days") },
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Operating Hours", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Slate700)
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("24 Hours", "6:00 AM – 10:00 PM", "9:00 AM – 7:00 PM").forEach { preset ->
                            val sel = timingsText == preset
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (sel) Color(0xFFEFF6FF) else Color.White,
                                border = BorderStroke(1.dp, if (sel) PrimaryBlue else Slate200),
                                modifier = Modifier.clickable { timingsText = preset }
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 12.sp,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (sel) PrimaryBlue else Slate700,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = timingsText,
                        onValueChange = { timingsText = it },
                        label = { Text("Custom Hours") },
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                8 -> {
                    // Step 8: Pricing
                    StepHeader(
                        title = "Set Your Parking Rates",
                        subtitle = "Competitive prices attract daily office commuters and steady monthly earnings."
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = hourlyPrice,
                        onValueChange = { hourlyPrice = it },
                        label = { Text("Hourly Rate (₹/hr)") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 16.sp) },
                        placeholder = { Text("40") },
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = dailyPrice,
                        onValueChange = { dailyPrice = it },
                        label = { Text("Daily Rate (₹/day)") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 16.sp) },
                        placeholder = { Text("250") },
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = monthlyPrice,
                        onValueChange = { monthlyPrice = it },
                        label = { Text("Monthly Pass (₹/month)") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 16.sp) },
                        placeholder = { Text("3000") },
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Suggested Rates for $area", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryBlue)
                                Text("Average ₹40/hr in $city. You keep 90% after platform fees.", fontSize = 11.sp, color = Slate600)
                            }
                        }
                    }
                }

                9 -> {
                    // Step 9: Rules & Description
                    StepHeader(
                        title = "Rules & Space Description",
                        subtitle = "Communicate your house rules and entry instructions clearly."
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        label = { Text("Space Description") },
                        placeholder = { Text("Safe, well-lit covered parking slot with CCTV security. Easy drive-in access.") },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = rulesText,
                        onValueChange = { rulesText = it },
                        label = { Text("Parking Rules for Guests") },
                        placeholder = { Text("Park within the marked lines. No commercial repairs. Gate access details...") },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                10 -> {
                    // Step 10: Preview & Publish
                    StepHeader(
                        title = "Preview & Publish Listing",
                        subtitle = "Review your listing details before publishing for live seeker reservations."
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Clean White Summary Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title.ifBlank { "Covered Driveway Slot - $area" },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Slate900
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${address.ifBlank { "12th Main Road" }}, $area, $city",
                                        fontSize = 12.sp,
                                        color = Slate500
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEFF6FF)
                                ) {
                                    Text(
                                        text = parkingType,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = Slate100, modifier = Modifier.padding(vertical = 12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Capacity", fontSize = 11.sp, color = Slate400)
                                    Text("$capacity Vehicles", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                                }
                                Column {
                                    Text("Vehicles", fontSize = 11.sp, color = Slate400)
                                    Text(supportedVehicles.joinToString(", "), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Hourly Rate", fontSize = 11.sp, color = Slate400)
                                    Text("₹$hourlyPrice/hr", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Daily: ₹$dailyPrice", fontSize = 12.sp, color = Slate600)
                                Text("Monthly: ₹$monthlyPrice", fontSize = 12.sp, color = Slate600)
                                Text("Timings: $timingsText", fontSize = 12.sp, color = Slate600)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Verified Reassuring Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Ready to Go Live Instantly",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF15803D)
                                )
                                Text(
                                    text = "Your space will be discoverable on the map immediately for nearby commuters.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF166534)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Consolidate the bottom action area into a clean surface
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Secondary Button (Back / Cancel)
                    OutlinedButton(
                        onClick = { if (step > 1) step-- else onCancel() },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (step == 1) "Cancel" else "Back",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    // Primary Button (Continue / Publish Space)
                    Button(
                        onClick = {
                            if (step < totalSteps) {
                                step++
                            } else {
                                val newSpace = ParkingSpace(
                                    ownerId = 1L,
                                    ownerName = "Rohan Sharma",
                                    ownerPhone = "+91 98765 43210",
                                    title = title.ifBlank { "Covered Driveway Slot - $area" },
                                    description = descriptionText,
                                    address = address.ifBlank { "12, Central Avenue" },
                                    area = area.ifBlank { "Indiranagar" },
                                    city = city.ifBlank { "Bengaluru" },
                                    state = state,
                                    pincode = pincode.ifBlank { "560038" },
                                    latitude = latitude,
                                    longitude = longitude,
                                    parkingType = parkingType,
                                    vehicleCapacity = capacity,
                                    supportedVehicles = supportedVehicles.joinToString(", "),
                                    hourlyPrice = hourlyPrice.toDoubleOrNull() ?: 40.0,
                                    dailyPrice = dailyPrice.toDoubleOrNull() ?: 250.0,
                                    monthlyPrice = monthlyPrice.toDoubleOrNull() ?: 3000.0,
                                    hasCctv = hasCctv,
                                    hasSecurityGuard = hasGuard,
                                    isCovered = isCovered,
                                    hasEvCharging = hasEv,
                                    hasLighting = hasLighting,
                                    has24x7Access = has24x7,
                                    easyEntryExit = easyEntry,
                                    rules = rulesText,
                                    availableDays = daysText,
                                    availableTimings = timingsText,
                                    status = "Active",
                                    verificationStatus = "Verified",
                                    parkingPhoto = selectedPhotoUri ?: "https://images.unsplash.com/photo-1590674899484-d5640e854abe?w=800&q=80"
                                )
                                onPublish(newSpace)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier
                            .weight(1.4f)
                            .height(48.dp)
                            .testTag("wizard_next_button")
                    ) {
                        Text(
                            text = if (step == totalSteps) "Publish Space" else "Continue →",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepHeader(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}

private data class AmenityItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val checked: Boolean,
    val onToggle: () -> Unit
)
