package com.example.data.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class UserLocation(
    val id: String,
    val name: String,
    val locality: String,
    val city: String,
    val latitude: Double,
    val longitude: Double
)

object LocationUtils {

    val popularLocations = listOf(
        UserLocation(
            id = "blr_indiranagar",
            name = "Indiranagar 100ft Rd",
            locality = "Indiranagar",
            city = "Bengaluru",
            latitude = 12.9780,
            longitude = 77.6400
        ),
        UserLocation(
            id = "blr_mgroad",
            name = "MG Road / Brigade",
            locality = "Ashok Nagar",
            city = "Bengaluru",
            latitude = 12.9740,
            longitude = 77.6080
        ),
        UserLocation(
            id = "blr_koramangala",
            name = "Koramangala 5th Block",
            locality = "Koramangala",
            city = "Bengaluru",
            latitude = 12.9352,
            longitude = 77.6245
        ),
        UserLocation(
            id = "del_cp",
            name = "Connaught Place",
            locality = "Inner Circle",
            city = "New Delhi",
            latitude = 28.6315,
            longitude = 77.2167
        ),
        UserLocation(
            id = "mum_bkc",
            name = "Bandra Kurla Complex",
            locality = "BKC G Block",
            city = "Mumbai",
            latitude = 19.0657,
            longitude = 72.8687
        ),
        UserLocation(
            id = "hyd_hitec",
            name = "Hitec City / Cyber Towers",
            locality = "Madhapur",
            city = "Hyderabad",
            latitude = 17.4504,
            longitude = 78.3808
        )
    )

    fun calculateDistanceKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }

    fun formatDistance(distanceKm: Double): String {
        return if (distanceKm < 1.0) {
            val meters = (distanceKm * 1000).roundToInt()
            val roundedMeters = maxOf(50, ((meters + 25) / 50) * 50)
            "$roundedMeters m"
        } else {
            String.format("%.1f km", distanceKm)
        }
    }

    fun estimateWalkingMinutes(distanceKm: Double): Int {
        // Average walking speed ~ 4.5 km/h -> ~13.3 mins per km
        val mins = (distanceKm * 13.3).roundToInt()
        return maxOf(1, mins)
    }

    fun estimateDrivingMinutes(distanceKm: Double): Int {
        // Average city driving speed ~ 20 km/h -> ~3 mins per km
        val mins = (distanceKm * 3.0).roundToInt()
        return maxOf(1, mins)
    }

    fun formatWalkingTime(distanceKm: Double): String {
        val mins = estimateWalkingMinutes(distanceKm)
        return "$mins min walk"
    }

    fun formatDrivingTime(distanceKm: Double): String {
        val mins = estimateDrivingMinutes(distanceKm)
        return "$mins min drive"
    }

    /**
     * Opens Google Maps for turnkey turn-by-turn navigation or searching the specific parking location.
     */
    fun openGoogleMaps(context: android.content.Context, lat: Double, lng: Double, label: String = "Parking Space") {
        val uri = android.net.Uri.parse("google.navigation:q=$lat,$lng&mode=d")
        val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }
        try {
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            // Fallback to standard geo intent or web Google Maps
            val fallbackUri = android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
            try {
                context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, fallbackUri))
            } catch (ignored: Exception) {}
        }
    }
}
