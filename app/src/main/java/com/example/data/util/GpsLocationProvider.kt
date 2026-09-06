package com.example.data.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class GpsLocationProvider(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted || coarseLocationGranted
    }

    suspend fun getCurrentGpsLocation(): Location? {
        if (!hasLocationPermission()) return null

        return withContext(Dispatchers.IO) {
            try {
                val cts = CancellationTokenSource()
                val request = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setMaxUpdateAgeMillis(10_000)
                    .setDurationMillis(8_000)
                    .build()

                val currentLocation = suspendCancellableCoroutine<Location?> { cont ->
                    cts.token.let { token ->
                        fusedLocationClient.getCurrentLocation(request, token)
                            .addOnSuccessListener { loc ->
                                if (cont.isActive) cont.resume(loc)
                            }
                            .addOnFailureListener {
                                if (cont.isActive) cont.resume(null)
                            }
                            .addOnCanceledListener {
                                if (cont.isActive) cont.resume(null)
                            }
                    }

                    cont.invokeOnCancellation {
                        cts.cancel()
                    }
                }

                currentLocation ?: getLastKnownLocation()
            } catch (e: SecurityException) {
                null
            } catch (e: Exception) {
                getLastKnownLocation()
            }
        }
    }

    private suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { cont ->
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { loc ->
                    if (cont.isActive) cont.resume(loc)
                }
                .addOnFailureListener {
                    if (cont.isActive) cont.resume(null)
                }
                .addOnCanceledListener {
                    if (cont.isActive) cont.resume(null)
                }
        } catch (e: SecurityException) {
            if (cont.isActive) cont.resume(null)
        } catch (e: Exception) {
            if (cont.isActive) cont.resume(null)
        }
    }

    suspend fun reverseGeocode(latitude: Double, longitude: Double): UserLocation = withContext(Dispatchers.IO) {
        var resolvedName: String? = null
        var resolvedLocality: String? = null
        var resolvedCity: String? = null

        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                resolvedLocality = address.subLocality ?: address.locality ?: address.subAdminArea
                resolvedCity = address.locality ?: address.adminArea ?: address.subAdminArea
                val feature = address.featureName ?: address.thoroughfare
                resolvedName = when {
                    !feature.isNullOrBlank() && !resolvedLocality.isNullOrBlank() -> "$feature, $resolvedLocality"
                    !resolvedLocality.isNullOrBlank() -> resolvedLocality
                    else -> address.getAddressLine(0)
                }
            }
        } catch (_: Exception) {
            // Geocoder service may not be available on some emulators or offline
        }

        // Fallback or augment
        val nearestPopular = LocationUtils.popularLocations.minByOrNull {
            LocationUtils.calculateDistanceKm(latitude, longitude, it.latitude, it.longitude)
        }
        val distanceToNearest = nearestPopular?.let {
            LocationUtils.calculateDistanceKm(latitude, longitude, it.latitude, it.longitude)
        } ?: Double.MAX_VALUE

        val finalName = resolvedName?.takeIf { it.isNotBlank() }
            ?: if (distanceToNearest < 5.0 && nearestPopular != null) {
                "Near ${nearestPopular.name}"
            } else {
                String.format(Locale.US, "GPS (%.4f, %.4f)", latitude, longitude)
            }

        val finalLocality = resolvedLocality?.takeIf { it.isNotBlank() }
            ?: if (distanceToNearest < 15.0 && nearestPopular != null) {
                nearestPopular.locality
            } else {
                "Current Area"
            }

        val finalCity = resolvedCity?.takeIf { it.isNotBlank() }
            ?: if (distanceToNearest < 50.0 && nearestPopular != null) {
                nearestPopular.city
            } else {
                "Bengaluru"
            }

        UserLocation(
            id = "live_gps_${System.currentTimeMillis()}",
            name = finalName,
            locality = finalLocality,
            city = finalCity,
            latitude = latitude,
            longitude = longitude
        )
    }
}
