package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ParkingSpace
import com.example.data.util.LocationUtils
import com.example.data.util.UserLocation
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.MapDarkBg
import com.example.ui.theme.MapDarkPark
import com.example.ui.theme.MapDarkRoad
import com.example.ui.theme.MapDarkWater
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate900
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class ParkingMapMode(val label: String) {
    GOOGLE_NORMAL("Google Map"),
    GOOGLE_HYBRID("Satellite"),
    GOOGLE_TERRAIN("Terrain"),
    VECTOR_STYLE("Stylized View")
}

@Composable
fun InteractiveMapView(
    spaces: List<ParkingSpace>,
    selectedSpaceId: Long?,
    onSelectSpace: (Long) -> Unit,
    onViewDetails: (Long) -> Unit,
    onBookNow: (Long) -> Unit,
    modifier: Modifier = Modifier,
    userLocation: UserLocation? = null,
    nearbyRadiusKm: Double? = null,
    onCenterToLocation: () -> Unit = {}
) {
    var mapMode by remember { mutableStateOf(ParkingMapMode.GOOGLE_NORMAL) }
    var showLayersMenu by remember { mutableStateOf(false) }

    val selectedSpace = spaces.find { it.id == selectedSpaceId }
    val isDark = MaterialTheme.colorScheme.background == CharcoalBackground
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Default center calculation
    val defaultCenter = remember(userLocation, spaces) {
        if (userLocation != null) {
            LatLng(userLocation.latitude, userLocation.longitude)
        } else if (spaces.isNotEmpty()) {
            LatLng(spaces.first().latitude, spaces.first().longitude)
        } else {
            LatLng(12.9716, 77.5946) // Bengaluru default
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultCenter, 13.8f)
    }

    // Camera animation when selected space changes
    LaunchedEffect(selectedSpaceId) {
        val space = spaces.find { it.id == selectedSpaceId }
        if (space != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLng(LatLng(space.latitude, space.longitude)),
                500
            )
        }
    }

    // Camera animation when user location updates initially or on request
    LaunchedEffect(userLocation?.latitude, userLocation?.longitude) {
        if (userLocation != null && selectedSpaceId == null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(userLocation.latitude, userLocation.longitude),
                    14f
                ),
                500
            )
        }
    }

    Box(modifier = modifier.fillMaxSize().background(if (isDark) MapDarkBg else Color(0xFFE2E8F0))) {
        if (mapMode != ParkingMapMode.VECTOR_STYLE) {
            // Google Maps Compose View
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("google_map_view"),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    compassEnabled = true,
                    mapToolbarEnabled = false
                ),
                properties = MapProperties(
                    mapType = when (mapMode) {
                        ParkingMapMode.GOOGLE_HYBRID -> MapType.HYBRID
                        ParkingMapMode.GOOGLE_TERRAIN -> MapType.TERRAIN
                        else -> MapType.NORMAL
                    },
                    isMyLocationEnabled = false
                ),
                onMapClick = {
                    // Optional click on empty map area
                }
            ) {
                // User Location Radius Perimeter if filter active
                if (userLocation != null && nearbyRadiusKm != null) {
                    Circle(
                        center = LatLng(userLocation.latitude, userLocation.longitude),
                        radius = nearbyRadiusKm * 1000.0,
                        fillColor = Color(0x182563EB),
                        strokeColor = PrimaryBlue.copy(alpha = 0.5f),
                        strokeWidth = 3f
                    )
                }

                // User Location Marker & Accuracy indicator
                if (userLocation != null) {
                    Circle(
                        center = LatLng(userLocation.latitude, userLocation.longitude),
                        radius = 80.0,
                        fillColor = Color(0x282563EB),
                        strokeColor = PrimaryBlue,
                        strokeWidth = 2f
                    )

                    MarkerComposable(
                        state = rememberMarkerState(
                            key = "user_gps_marker",
                            position = LatLng(userLocation.latitude, userLocation.longitude)
                        ),
                        title = "Your Location",
                        snippet = userLocation.name
                    ) {
                        UserLocationPin()
                    }
                }

                // Walking route polyline from user to selected parking spot
                if (selectedSpace != null && userLocation != null) {
                    Polyline(
                        points = listOf(
                            LatLng(userLocation.latitude, userLocation.longitude),
                            LatLng(selectedSpace.latitude, selectedSpace.longitude)
                        ),
                        color = Color(0xFF059669),
                        width = 8f
                    )
                }

                // Parking Space Pins
                spaces.forEach { space ->
                    val isSelected = space.id == selectedSpaceId
                    val markerState = rememberMarkerState(
                        key = "parking_spot_${space.id}",
                        position = LatLng(space.latitude, space.longitude)
                    )

                    LaunchedEffect(space.latitude, space.longitude) {
                        markerState.position = LatLng(space.latitude, space.longitude)
                    }

                    MarkerComposable(
                        state = markerState,
                        title = space.title,
                        snippet = "₹${space.hourlyPrice.toInt()}/hr • ${space.vehicleCapacity} spots available",
                        onClick = {
                            onSelectSpace(space.id)
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLng(LatLng(space.latitude, space.longitude)),
                                    400
                                )
                            }
                            true
                        }
                    ) {
                        ParkingMapPinBadge(
                            space = space,
                            isSelected = isSelected,
                            isDark = isDark
                        )
                    }
<<<<<<< HEAD
                    .clickable { onSelectSpace(space.id) }
                    .testTag("map_marker_${space.id}")
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = markerBgColor,
                    shadowElevation = if (isSelected) 6.dp else 3.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = markerBorderColor
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        if (space.hasEvCharging) {
                            Icon(
                                imageVector = Icons.Default.ElectricCar,
                                contentDescription = "EV Charging",
                                tint = if (isSelected) Color.White else AccentEmerald,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = "₹${space.hourlyPrice.toInt()}/hr",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = markerTextColor
                        )
                    }
=======
>>>>>>> 17344eb6b3a3406d243c2ae5e44b64350c4f1642
                }
            }
        } else {
            // Offline / Stylized Vector Map Fallback View
            StylizedVectorMapView(
                spaces = spaces,
                selectedSpaceId = selectedSpaceId,
                onSelectSpace = onSelectSpace,
                userLocation = userLocation,
                nearbyRadiusKm = nearbyRadiusKm,
                isDark = isDark
            )
        }

        // Top Status Badge: Available spots count
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            shadowElevation = 3.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 16.dp)
                .testTag("map_spots_count_badge")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AccentEmerald)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${spaces.size} parking spots available",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Map Control Floating Buttons (Top-Right)
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            // Layer Switcher Button
            Box {
                FloatingActionButton(
                    onClick = { showLayersMenu = true },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    elevation = FloatingActionButtonDefaults.elevation(2.dp),
                    modifier = Modifier.size(42.dp).testTag("map_layers_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Map Layers",
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showLayersMenu,
                    onDismissRequest = { showLayersMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    ParkingMapMode.entries.forEach { mode ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = mode.label,
                                    fontWeight = if (mapMode == mode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (mapMode == mode) PrimaryBlue else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                mapMode = mode
                                showLayersMenu = false
                            },
                            modifier = Modifier.testTag("map_mode_${mode.name}")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Zoom In / Out Controls
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                cameraPositionState.animate(CameraUpdateFactory.zoomIn(), 300)
                            }
                        },
                        modifier = Modifier.size(38.dp).testTag("map_zoom_in_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Box(modifier = Modifier.width(34.dp).height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                cameraPositionState.animate(CameraUpdateFactory.zoomOut(), 300)
                            }
                        },
                        modifier = Modifier.size(38.dp).testTag("map_zoom_out_btn")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // My Location FAB
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        if (userLocation != null) {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(userLocation.latitude, userLocation.longitude),
                                    14.5f
                                ),
                                500
                            )
                        }
                    }
                    onCenterToLocation()
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryBlue,
                elevation = FloatingActionButtonDefaults.elevation(2.dp),
                modifier = Modifier.size(42.dp).testTag("map_my_location_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "My Location",
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Open in Google Maps Turn-by-Turn Navigation
            FloatingActionButton(
                onClick = {
                    if (selectedSpace != null) {
                        LocationUtils.openGoogleMaps(
                            context = context,
                            lat = selectedSpace.latitude,
                            lng = selectedSpace.longitude,
                            label = selectedSpace.title
                        )
                    } else if (userLocation != null) {
                        LocationUtils.openGoogleMaps(
                            context = context,
                            lat = userLocation.latitude,
                            lng = userLocation.longitude,
                            label = "Parking near " + userLocation.name
                        )
                    } else {
                        LocationUtils.openGoogleMaps(
                            context = context,
                            lat = 12.9780,
                            lng = 77.6400,
                            label = "Bengaluru Parking"
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = AccentEmerald,
                elevation = FloatingActionButtonDefaults.elevation(2.dp),
                modifier = Modifier.size(42.dp).testTag("map_open_google_maps_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Directions,
                    contentDescription = "Turn-by-turn Navigation",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Selected Space Preview Card (Slide up at bottom of map)
        AnimatedVisibility(
            visible = selectedSpace != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            if (selectedSpace != null) {
                val distText = if (userLocation != null) {
                    val km = LocationUtils.calculateDistanceKm(
                        userLocation.latitude,
                        userLocation.longitude,
                        selectedSpace.latitude,
                        selectedSpace.longitude
                    )
                    LocationUtils.formatDistance(km) + " · " + LocationUtils.formatWalkingTime(km)
                } else null

                CompactParkingCard(
                    space = selectedSpace,
                    onViewDetails = { onViewDetails(selectedSpace.id) },
                    onBook = { onBookNow(selectedSpace.id) },
                    distanceText = distText,
                    modifier = Modifier.testTag("map_space_preview_card")
                )
            }
        }
    }
}

/**
 * Custom Map Pin Badge showing Price, Availability, and EV status
 */
@Composable
fun ParkingMapPinBadge(
    space: ParkingSpace,
    isSelected: Boolean,
    isDark: Boolean
) {
    val bg = when {
        isSelected -> PrimaryBlue
        isDark -> CharcoalElevated
        else -> Color.White
    }
    val textColor = when {
        isSelected -> Color.White
        isDark -> MaterialTheme.colorScheme.onSurface
        else -> Slate900
    }
    val borderColor = when {
        isSelected -> Color.White
        isDark -> CharcoalBorder
        else -> Slate200
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 2.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = bg,
            shadowElevation = if (isSelected) 8.dp else 3.dp,
            border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
            modifier = Modifier.testTag("map_marker_${space.id}")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
            ) {
                if (space.hasEvCharging) {
                    Icon(
                        imageVector = Icons.Default.ElectricCar,
                        contentDescription = "EV",
                        tint = if (isSelected) Color(0xFFFDE047) else AccentEmerald,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White.copy(alpha = 0.25f) else PrimaryBlue.copy(alpha = 0.15f))
                    ) {
                        Text(
                            text = "P",
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = if (isSelected) Color.White else PrimaryBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Text(
                    text = "₹${space.hourlyPrice.toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = textColor
                )

                Spacer(modifier = Modifier.width(5.dp))
                val dotColor = if (space.vehicleCapacity <= 2) Color(0xFFF59E0B) else AccentEmerald
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else dotColor)
                )
            }
        }

        // Pointer triangle needle pointing down to spot coordinate
        Canvas(modifier = Modifier.size(width = 10.dp, height = 6.dp)) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
                close()
            }
            drawPath(path, color = bg)
        }
    }
}

/**
 * User location radar pin
 */
@Composable
fun UserLocationPin() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.White)
                .shadow(2.dp, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(13.dp)
                .clip(CircleShape)
                .background(PrimaryBlue)
        )
    }
}

/**
 * Stylized Vector Map View for offline / vector-styled rendering
 */
@Composable
private fun StylizedVectorMapView(
    spaces: List<ParkingSpace>,
    selectedSpaceId: Long?,
    onSelectSpace: (Long) -> Unit,
    userLocation: UserLocation?,
    nearbyRadiusKm: Double?,
    isDark: Boolean
) {
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    val mapCanvasBg = if (isDark) MapDarkBg else Color(0xFFF1F5F9)
    val parkColor = if (isDark) MapDarkPark else Color(0xFFDCFCE7)
    val waterColor = if (isDark) MapDarkWater else Color(0xFFE0F2FE)
    val roadColor = if (isDark) MapDarkRoad else Color(0xFFCBD5E1)
    val centerDashColor = if (isDark) Color(0xFF3B4861) else Color.White

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val containerWidth = maxWidth.value
        val containerHeight = maxHeight.value

        val markerPositions = remember(spaces) {
            spaces.mapIndexed { index, space ->
                val angle = (index * (360f / maxOf(1, spaces.size))) * (Math.PI / 180f)
                val distance = 140f + (index % 3) * 65f
                val rx = (kotlin.math.cos(angle) * distance).toFloat()
                val ry = (kotlin.math.sin(angle) * (distance * 0.75f)).toFloat()
                space.id to Pair(rx, ry)
            }.toMap()
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(0.7f, 2.5f)
                        panOffsetX += pan.x
                        panOffsetY += pan.y
                    }
                }
        ) {
            val centerX = size.width / 2f + panOffsetX
            val centerY = size.height / 2f + panOffsetY

            drawRect(color = mapCanvasBg)

            val parkPath = Path().apply {
                moveTo(centerX - 320 * zoomScale, centerY - 220 * zoomScale)
                lineTo(centerX - 160 * zoomScale, centerY - 260 * zoomScale)
                lineTo(centerX - 140 * zoomScale, centerY - 120 * zoomScale)
                lineTo(centerX - 300 * zoomScale, centerY - 90 * zoomScale)
                close()
            }
            drawPath(parkPath, color = parkColor)

            val lakePath = Path().apply {
                moveTo(centerX + 180 * zoomScale, centerY + 100 * zoomScale)
                lineTo(centerX + 340 * zoomScale, centerY + 70 * zoomScale)
                lineTo(centerX + 360 * zoomScale, centerY + 240 * zoomScale)
                lineTo(centerX + 220 * zoomScale, centerY + 260 * zoomScale)
                close()
            }
            drawPath(lakePath, color = waterColor)

            val roadWidth = 24f * zoomScale
            val majorRoadWidth = 36f * zoomScale

            drawLine(
                color = roadColor,
                start = Offset(0f, centerY - 80 * zoomScale),
                end = Offset(size.width, centerY - 80 * zoomScale),
                strokeWidth = majorRoadWidth
            )
            drawLine(
                color = roadColor,
                start = Offset(0f, centerY + 140 * zoomScale),
                end = Offset(size.width, centerY + 140 * zoomScale),
                strokeWidth = roadWidth
            )
            drawLine(
                color = roadColor,
                start = Offset(centerX - 60 * zoomScale, 0f),
                end = Offset(centerX - 60 * zoomScale, size.height),
                strokeWidth = majorRoadWidth
            )
            drawLine(
                color = roadColor,
                start = Offset(centerX + 150 * zoomScale, 0f),
                end = Offset(centerX + 150 * zoomScale, size.height),
                strokeWidth = roadWidth
            )

            drawLine(
                color = centerDashColor,
                start = Offset(0f, centerY - 80 * zoomScale),
                end = Offset(size.width, centerY - 80 * zoomScale),
                strokeWidth = 3f * zoomScale
            )
            drawLine(
                color = centerDashColor,
                start = Offset(centerX - 60 * zoomScale, 0f),
                end = Offset(centerX - 60 * zoomScale, size.height),
                strokeWidth = 3f * zoomScale
            )

            val userX = centerX
            val userY = centerY

            if (nearbyRadiusKm != null) {
                val radiusPx = ((nearbyRadiusKm.toFloat()) * 180f * zoomScale).coerceIn(80f, 650f)
                drawCircle(color = Color(0x182563EB), radius = radiusPx, center = Offset(userX, userY))
                drawCircle(
                    color = PrimaryBlue.copy(alpha = 0.5f),
                    radius = radiusPx,
                    center = Offset(userX, userY),
                    style = Stroke(
                        width = 2f * zoomScale,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f)
                    )
                )
            }

            drawCircle(color = Color(0x332563EB), radius = 28f * zoomScale, center = Offset(userX, userY))
            drawCircle(color = if (isDark) CharcoalSurface else Color.White, radius = 13f * zoomScale, center = Offset(userX, userY))
            drawCircle(color = PrimaryBlue, radius = 8f * zoomScale, center = Offset(userX, userY))
        }

        val density = LocalDensity.current
        spaces.forEach { space ->
            val relPos = markerPositions[space.id] ?: Pair(0f, 0f)
            val markerPxX = (containerWidth / 2f) + panOffsetX / density.density + (relPos.first * zoomScale)
            val markerPxY = (containerHeight / 2f) + panOffsetY / density.density + (relPos.second * zoomScale)
            val isSelected = space.id == selectedSpaceId

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (markerPxX * density.density).roundToInt() - 36,
                            y = (markerPxY * density.density).roundToInt() - 20
                        )
                    }
                    .clickable { onSelectSpace(space.id) }
            ) {
                ParkingMapPinBadge(
                    space = space,
                    isSelected = isSelected,
                    isDark = isDark
                )
            }
        }
    }
}
