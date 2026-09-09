package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.model.Booking
import com.example.data.model.ParkingSpace
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

/**
 * Real-Time Firebase Cloud Firestore Synchronization Manager.
 * Syncs parking space listings and booking passes across multiple phones in real-time.
 */
class FirebaseSyncManager private constructor(
    private val db: AppDatabase,
    private val coroutineScope: CoroutineScope
) {
    private var firestore: FirebaseFirestore? = null
    private var isListening = false

    init {
        try {
            firestore = FirebaseFirestore.getInstance()
            Log.d(TAG, "Firebase Firestore initialized successfully 🚀")
        } catch (e: Exception) {
            Log.w(TAG, "Firebase not yet configured via google-services.json. Offline local mode active. ${e.localizedMessage}")
        }
    }

    /**
     * Start listening to live cloud updates on "parking_spaces" and "bookings"
     */
    fun startRealtimeSync() {
        val fs = firestore ?: run {
            Log.d(TAG, "Firestore null, skipping snapshot listener")
            return
        }
        if (isListening) return
        isListening = true

        try {
            // Listen to real-time additions/modifications to parking spaces
            fs.collection(COLLECTION_SPACES)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error listening to parking_spaces Firestore collection: ${error.localizedMessage}", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        coroutineScope.launch(Dispatchers.IO) {
                            val remoteSpaces = mutableListOf<ParkingSpace>()
                            for (doc in snapshot.documents) {
                                try {
                                    val space = docToParkingSpace(doc.id, doc.data ?: emptyMap())
                                    if (space != null) {
                                        remoteSpaces.add(space)
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "Failed parsing document ${doc.id}", e)
                                }
                            }
                            if (remoteSpaces.isNotEmpty()) {
                                db.parkingSpaceDao().insertAll(remoteSpaces)
                                Log.d(TAG, "Realtime Sync: Inserted/Updated ${remoteSpaces.size} spaces into local DB")
                            }
                        }
                    }
                }

            // Listen to real-time additions/modifications to bookings
            fs.collection(COLLECTION_BOOKINGS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error listening to bookings Firestore collection: ${error.localizedMessage}", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        coroutineScope.launch(Dispatchers.IO) {
                            val remoteBookings = mutableListOf<Booking>()
                            for (doc in snapshot.documents) {
                                try {
                                    val booking = docToBooking(doc.id, doc.data ?: emptyMap())
                                    if (booking != null) {
                                        remoteBookings.add(booking)
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "Failed parsing booking document ${doc.id}", e)
                                }
                            }
                            if (remoteBookings.isNotEmpty()) {
                                db.bookingDao().insertAll(remoteBookings)
                                Log.d(TAG, "Realtime Sync: Inserted/Updated ${remoteBookings.size} bookings into local DB")
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Exception starting realtime sync", e)
        }
    }

    /**
     * Publish a new or updated parking space to Cloud Firestore
     */
    suspend fun publishSpaceToCloud(space: ParkingSpace) = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext
        try {
            val spaceMap = spaceToMap(space)
            val docId = if (space.id > 0) space.id.toString() else System.currentTimeMillis().toString()
            Tasks.await(
                fs.collection(COLLECTION_SPACES).document(docId).set(spaceMap)
            )
            Log.d(TAG, "Published space #${space.id} '${space.title}' to Firestore successfully! docId=$docId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to publish space to Firestore: ${e.localizedMessage}", e)
        }
    }

    /**
     * Publish a booking pass to Cloud Firestore
     */
    suspend fun publishBookingToCloud(booking: Booking) = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext
        try {
            val bookingMap = bookingToMap(booking)
            val docId = if (booking.id > 0) booking.id.toString() else booking.bookingCode
            Tasks.await(
                fs.collection(COLLECTION_BOOKINGS).document(docId).set(bookingMap)
            )
            Log.d(TAG, "Published booking ${booking.bookingCode} to Firestore! docId=$docId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to publish booking to Firestore: ${e.localizedMessage}", e)
        }
    }

    private fun spaceToMap(s: ParkingSpace): Map<String, Any> {
        return mapOf(
            "id" to s.id,
            "ownerId" to s.ownerId,
            "ownerName" to s.ownerName,
            "ownerPhone" to s.ownerPhone,
            "title" to s.title,
            "description" to s.description,
            "address" to s.address,
            "area" to s.area,
            "city" to s.city,
            "pincode" to s.pincode,
            "latitude" to s.latitude,
            "longitude" to s.longitude,
            "parkingType" to s.parkingType,
            "vehicleCapacity" to s.vehicleCapacity,
            "supportedVehicles" to s.supportedVehicles,
            "hourlyPrice" to s.hourlyPrice,
            "dailyPrice" to s.dailyPrice,
            "monthlyPrice" to s.monthlyPrice,
            "status" to s.status,
            "verificationStatus" to s.verificationStatus,
            "isCovered" to s.isCovered,
            "hasCctv" to s.hasCctv,
            "hasSecurityGuard" to s.hasSecurityGuard,
            "hasEvCharging" to s.hasEvCharging,
            "hasLighting" to s.hasLighting,
            "has24x7Access" to s.has24x7Access,
            "rating" to s.rating.toDouble(),
            "reviewsCount" to s.reviewsCount,
            "parkingPhoto" to s.parkingPhoto,
            "updatedAt" to System.currentTimeMillis()
        )
    }

    private fun docToParkingSpace(docId: String, data: Map<String, Any>): ParkingSpace? {
        if (data.isEmpty()) return null
        val id = (data["id"] as? Number)?.toLong()
            ?: (data["id"] as? String)?.toLongOrNull()
            ?: docId.toLongOrNull()
            ?: abs(docId.hashCode().toLong())

        val title = data["title"] as? String ?: data["name"] as? String ?: return null

        return ParkingSpace(
            id = id,
            ownerId = (data["ownerId"] as? Number)?.toLong() ?: 1L,
            ownerName = data["ownerName"] as? String ?: "Arjun Patel",
            ownerPhone = data["ownerPhone"] as? String ?: "+91 98234 56789",
            title = title,
            description = data["description"] as? String ?: "",
            address = data["address"] as? String ?: "",
            area = data["area"] as? String ?: "",
            city = data["city"] as? String ?: "Bengaluru",
            pincode = data["pincode"] as? String ?: "560038",
            latitude = (data["latitude"] as? Number)?.toDouble() ?: 12.9716,
            longitude = (data["longitude"] as? Number)?.toDouble() ?: 77.5946,
            parkingType = data["parkingType"] as? String ?: "Residential",
            vehicleCapacity = (data["vehicleCapacity"] as? Number)?.toInt() ?: 2,
            supportedVehicles = data["supportedVehicles"] as? String ?: "Car, Bike",
            hourlyPrice = (data["hourlyPrice"] as? Number)?.toDouble() ?: 40.0,
            dailyPrice = (data["dailyPrice"] as? Number)?.toDouble() ?: 300.0,
            monthlyPrice = (data["monthlyPrice"] as? Number)?.toDouble() ?: 4000.0,
            status = data["status"] as? String ?: "Active",
            verificationStatus = data["verificationStatus"] as? String ?: "Verified",
            isCovered = data["isCovered"] as? Boolean ?: true,
            hasCctv = data["hasCctv"] as? Boolean ?: true,
            hasSecurityGuard = data["hasSecurityGuard"] as? Boolean ?: true,
            hasEvCharging = data["hasEvCharging"] as? Boolean ?: false,
            hasLighting = data["hasLighting"] as? Boolean ?: true,
            has24x7Access = data["has24x7Access"] as? Boolean ?: true,
            rating = (data["rating"] as? Number)?.toFloat() ?: 4.8f,
            reviewsCount = (data["reviewsCount"] as? Number)?.toInt() ?: 12,
            parkingPhoto = data["parkingPhoto"] as? String ?: ""
        )
    }

    private fun bookingToMap(b: Booking): Map<String, Any> {
        return mapOf(
            "id" to b.id,
            "bookingCode" to b.bookingCode,
            "userId" to b.userId,
            "userName" to b.userName,
            "parkingSpaceId" to b.parkingSpaceId,
            "parkingTitle" to b.parkingTitle,
            "parkingAddress" to b.parkingAddress,
            "parkingCity" to b.parkingCity,
            "vehicleType" to b.vehicleType,
            "vehicleRegNumber" to b.vehicleRegNumber,
            "bookingDate" to b.bookingDate,
            "startTime" to b.startTime,
            "endTime" to b.endTime,
            "durationHours" to b.durationHours,
            "totalAmount" to b.totalAmount,
            "status" to b.status,
            "paymentMethod" to b.paymentMethod,
            "createdAt" to System.currentTimeMillis()
        )
    }

    private fun docToBooking(docId: String, data: Map<String, Any>): Booking? {
        if (data.isEmpty()) return null
        val id = (data["id"] as? Number)?.toLong()
            ?: (data["id"] as? String)?.toLongOrNull()
            ?: docId.toLongOrNull()
            ?: abs(docId.hashCode().toLong())

        val bookingCode = data["bookingCode"] as? String ?: docId

        return Booking(
            id = id,
            bookingCode = bookingCode,
            userId = (data["userId"] as? Number)?.toLong() ?: 1L,
            userName = data["userName"] as? String ?: "Commuter",
            parkingSpaceId = (data["parkingSpaceId"] as? Number)?.toLong() ?: 1L,
            parkingTitle = data["parkingTitle"] as? String ?: "Parking Slot",
            parkingAddress = data["parkingAddress"] as? String ?: "",
            parkingCity = data["parkingCity"] as? String ?: "Bengaluru",
            vehicleType = data["vehicleType"] as? String ?: "Car",
            vehicleRegNumber = data["vehicleRegNumber"] as? String ?: "KA-01-AB-1234",
            bookingDate = data["bookingDate"] as? String ?: "Today",
            startTime = data["startTime"] as? String ?: "10:00 AM",
            endTime = data["endTime"] as? String ?: "12:00 PM",
            durationHours = (data["durationHours"] as? Number)?.toInt() ?: 2,
            subtotal = (data["subtotal"] as? Number)?.toDouble() ?: 80.0,
            platformFee = (data["platformFee"] as? Number)?.toDouble() ?: 10.0,
            totalAmount = (data["totalAmount"] as? Number)?.toDouble() ?: 90.0,
            status = data["status"] as? String ?: "Confirmed",
            paymentMethod = data["paymentMethod"] as? String ?: "UPI",
            paymentStatus = data["paymentStatus"] as? String ?: "Paid",
            qrData = data["qrData"] as? String ?: "PARKSPACE:$bookingCode",
            providerEarnings = (data["providerEarnings"] as? Number)?.toDouble() ?: 80.0,
            isReviewed = data["isReviewed"] as? Boolean ?: false
        )
    }

    companion object {
        private const val TAG = "FirebaseSyncManager"
        private const val COLLECTION_SPACES = "parking_spaces"
        private const val COLLECTION_BOOKINGS = "bookings"

        @Volatile
        private var instance: FirebaseSyncManager? = null

        fun getInstance(db: AppDatabase, coroutineScope: CoroutineScope): FirebaseSyncManager {
            return instance ?: synchronized(this) {
                instance ?: FirebaseSyncManager(db, coroutineScope).also { instance = it }
            }
        }
    }
}
