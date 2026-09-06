package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ParkingSpace
import com.example.data.util.LocationUtils
import com.example.data.util.UserLocation
import com.example.ui.theme.AccentEmerald
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.MapBuilding
import com.example.ui.theme.MapDarkBg
import com.example.ui.theme.MapDarkBuilding
import com.example.ui.theme.MapDarkPark
import com.example.ui.theme.MapDarkRoad
import com.example.ui.theme.MapDarkWater
import com.example.ui.theme.MapPark
import com.example.ui.theme.MapRoad
import com.example.ui.theme.MapWater
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import kotlin.math.roundToInt

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
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    val selectedSpace = spaces.find { it.id == selectedSpaceId }
    val isDark = MaterialTheme.colorScheme.background == CharcoalBackground

    val mapBgColor = if (isDark) MapDarkBg else Color(0xFFE2E8F0)
    val mapCanvasBg = if (isDark) MapDarkBg else Color(0xFFF1F5F9)
    val parkColor = if (isDark) MapDarkPark else Color(0xFFDCFCE7)
    val waterColor = if (isDark) MapDarkWater else Color(0xFFE0F2FE)
    val roadColor = if (isDark) MapDarkRoad else Color(0xFFCBD5E1)
    val centerDashColor = if (isDark) Color(0xFF3B4861) else Color.White

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(mapBgColor)) {
        val containerWidth = maxWidth.value
        val containerHeight = maxHeight.value

        // Relative Marker offsets based on space coordinates
        val markerPositions = remember(spaces) {
            spaces.mapIndexed { index, space ->
                // Pseudo-normalized layout offsets around city center
                val angle = (index * (360f / maxOf(1, spaces.size))) * (Math.PI / 180f)
                val distance = 140f + (index % 3) * 65f
                val rx = (kotlin.math.cos(angle) * distance).toFloat()
                val ry = (kotlin.math.sin(angle) * (distance * 0.75f)).toFloat()
                space.id to Pair(rx, ry)
            }.toMap()
        }

        // Custom Vector Street Map Canvas
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

            // Background City Grid & Blocks
            drawRect(color = mapCanvasBg)

            // Green Park Polygon
            val parkPath = Path().apply {
                moveTo(centerX - 320 * zoomScale, centerY - 220 * zoomScale)
                lineTo(centerX - 160 * zoomScale, centerY - 260 * zoomScale)
                lineTo(centerX - 140 * zoomScale, centerY - 120 * zoomScale)
                lineTo(centerX - 300 * zoomScale, centerY - 90 * zoomScale)
                close()
            }
            drawPath(parkPath, color = parkColor)

            // Water Lake Polygon
            val lakePath = Path().apply {
                moveTo(centerX + 180 * zoomScale, centerY + 100 * zoomScale)
                lineTo(centerX + 340 * zoomScale, centerY + 70 * zoomScale)
                lineTo(centerX + 360 * zoomScale, centerY + 240 * zoomScale)
                lineTo(centerX + 220 * zoomScale, centerY + 260 * zoomScale)
                close()
            }
            drawPath(lakePath, color = waterColor)

            // Major Roads Grid (Horizontal & Vertical & Diagonal)
            val roadWidth = 24f * zoomScale
            val majorRoadWidth = 36f * zoomScale

            // Arterial road 1 (Ring Road)
            drawLine(
                color = roadColor,
                start = Offset(0f, centerY - 80 * zoomScale),
                end = Offset(size.width, centerY - 80 * zoomScale),
                strokeWidth = majorRoadWidth
            )
            // Arterial road 2
            drawLine(
                color = roadColor,
                start = Offset(0f, centerY + 140 * zoomScale),
                end = Offset(size.width, centerY + 140 * zoomScale),
                strokeWidth = roadWidth
            )
            // Cross road 1 (Metro Corridor)
            drawLine(
                color = roadColor,
                start = Offset(centerX - 60 * zoomScale, 0f),
                end = Offset(centerX - 60 * zoomScale, size.height),
                strokeWidth = majorRoadWidth
            )
            // Cross road 2
            drawLine(
                color = roadColor,
                start = Offset(centerX + 150 * zoomScale, 0f),
                end = Offset(centerX + 150 * zoomScale, size.height),
                strokeWidth = roadWidth
            )
            // Diagonal Boulevard
            drawLine(
                color = roadColor,
                start = Offset(0f, centerY + 240 * zoomScale),
                end = Offset(size.width, centerY - 200 * zoomScale),
                strokeWidth = roadWidth
            )

            // Center Road Dashes
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

            // Current User Location & Nearby Radius Indicator
            val userX = centerX
            val userY = centerY

            // Draw Nearby Radius Perimeter if active
            if (nearbyRadiusKm != null) {
                val radiusPx = ((nearbyRadiusKm.toFloat()) * 180f * zoomScale).coerceIn(80f, 650f)
                drawCircle(
                    color = Color(0x182563EB),
                    radius = radiusPx,
                    center = Offset(userX, userY)
                )
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

            // Draw walking route line to selected space
            if (selectedSpace != null) {
                val selPos = markerPositions[selectedSpace.id]
                if (selPos != null) {
                    val targetX = centerX + selPos.first * zoomScale
                    val targetY = centerY + selPos.second * zoomScale
                    drawLine(
                        color = Color(0xFF059669),
                        start = Offset(userX, userY),
                        end = Offset(targetX, targetY),
                        strokeWidth = 3f * zoomScale,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 8f), 0f)
                    )
                }
            }

            // User Location Radar Pulse & Dot
            drawCircle(
                color = Color(0x332563EB),
                radius = 28f * zoomScale,
                center = Offset(userX, userY)
            )
            drawCircle(
                color = if (isDark) CharcoalSurface else Color.White,
                radius = 13f * zoomScale,
                center = Offset(userX, userY)
            )
            drawCircle(
                color = PrimaryBlue,
                radius = 8f * zoomScale,
                center = Offset(userX, userY)
            )
        }

        // Overlay Interactive Floating Marker Pins (Price Badges)
        val density = androidx.compose.ui.platform.LocalDensity.current
        spaces.forEach { space ->
            val relPos = markerPositions[space.id] ?: Pair(0f, 0f)
            val markerPxX = (containerWidth / 2f) + panOffsetX / density.density + (relPos.first * zoomScale)
            val markerPxY = (containerHeight / 2f) + panOffsetY / density.density + (relPos.second * zoomScale)
            val isSelected = space.id == selectedSpaceId

            val markerBgColor = if (isSelected) {
                PrimaryBlue
            } else if (isDark) {
                CharcoalElevated
            } else {
                Color.White
            }
            val markerTextColor = if (isSelected) {
                Color.White
            } else if (isDark) {
                MaterialTheme.colorScheme.onSurface
            } else {
                Slate900
            }
            val markerBorderColor = if (isSelected) {
                PrimaryBlue
            } else if (isDark) {
                CharcoalBorder
            } else {
                Slate200
            }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (markerPxX * density.density).roundToInt() - 36,
                            y = (markerPxY * density.density).roundToInt() - 20
                        )
                    }
                    .clickable { onSelectSpace(space.id) }
                    .testTag("map_marker_${space.id}")
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = markerBgColor,
                    shadowElevation = if (isSelected) 6.dp else 2.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = markerBorderColor
                    )
                ) {
                    Text(
                        text = "₹${space.hourlyPrice.toInt()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = markerTextColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Map Control Floating Buttons (Top-Right)
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column {
                    IconButton(
                        onClick = { zoomScale = (zoomScale * 1.2f).coerceAtMost(2.5f) },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Box(modifier = Modifier.width(34.dp).height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
                    IconButton(
                        onClick = { zoomScale = (zoomScale / 1.2f).coerceAtLeast(0.7f) },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            FloatingActionButton(
                onClick = {
                    panOffsetX = 0f
                    panOffsetY = 0f
                    zoomScale = 1f
                    onCenterToLocation()
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                elevation = FloatingActionButtonDefaults.elevation(2.dp),
                modifier = Modifier.size(42.dp).testTag("map_my_location_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "My Location",
                    modifier = Modifier.size(18.dp)
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
                CompactParkingCard(
                    space = selectedSpace,
                    onViewDetails = { onViewDetails(selectedSpace.id) },
                    onBook = { onBookNow(selectedSpace.id) },
                    modifier = Modifier.testTag("map_space_preview_card")
                )
            }
        }
    }
}
