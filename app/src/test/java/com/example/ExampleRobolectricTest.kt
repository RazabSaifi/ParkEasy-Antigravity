package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.util.GpsLocationProvider
import com.example.data.util.LocationUtils
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ParkEasy", appName)
  }

  @Test
  fun `test location calculation and reverse geocoding fallback`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val provider = GpsLocationProvider(context)
    
    // Test near Indiranagar coordinates (12.9784, 77.6408)
    val userLoc = provider.reverseGeocode(12.9784, 77.6408)
    assertNotNull(userLoc)
    assertTrue(userLoc.name.isNotBlank())
    assertEquals(12.9784, userLoc.latitude, 0.0001)
    assertEquals(77.6408, userLoc.longitude, 0.0001)

    // Test distance calculation
    val dist = LocationUtils.calculateDistanceKm(12.9784, 77.6408, 12.9716, 77.5946)
    assertTrue(dist > 0.0)
    assertTrue(dist < 10.0)
  }
}
