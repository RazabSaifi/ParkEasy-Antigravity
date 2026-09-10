package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 1,
    val name: String = "Rohan Sharma",
    val email: String = "rohan.sharma@example.com",
    val phone: String = "+91 98765 43210",
    val role: String = "Both", // "Seeker", "Provider", "Both", "Admin"
    val activeMode: String = "Seeker", // "Seeker", "Provider", "Admin"
    val isIdVerified: Boolean = true,
    val isPropertyVerified: Boolean = true,
    val totalEarnings: Double = 24500.0,
    val availableBalance: Double = 18200.0,
    val pendingAmount: Double = 2100.0,
    val withdrawnAmount: Double = 4200.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "parking_spaces")
data class ParkingSpace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerId: Long = 1,
    val ownerName: String = "Arjun Patel",
    val ownerPhone: String = "+91 98234 56789",
    val title: String,
    val description: String,
    val address: String,
    val area: String,
    val city: String,
    val state: String = "",
    val pincode: String,
    val latitude: Double,
    val longitude: Double,
    val parkingType: String, // "Residential", "Commercial", "Open Plot", "Basement", "Private Garage", "Other"
    val vehicleCapacity: Int = 2,
    val supportedVehicles: String = "Car, Bike, SUV", // comma-separated
    val hourlyPrice: Double,
    val dailyPrice: Double,
    val monthlyPrice: Double,
    val status: String = "Active", // "Active", "Paused", "Draft", "Rejected"
    val verificationStatus: String = "Verified", // "Verified", "Pending Verification", "Rejected"
    val isCovered: Boolean = true,
    val hasCctv: Boolean = true,
    val hasSecurityGuard: Boolean = true,
    val hasEvCharging: Boolean = false,
    val hasLighting: Boolean = true,
    val has24x7Access: Boolean = true,
    val hasSecurityGate: Boolean = true,
    val easyEntryExit: Boolean = true,
    val rating: Float = 4.8f,
    val reviewsCount: Int = 42,
    val entrancePhoto: String = "",
    val parkingPhoto: String = "",
    val surroundingsPhoto: String = "",
    val availableDays: String = "Mon - Sun",
    val availableTimings: String = "24/7 Access",
    val rules: String = "• No overnight commercial vehicles\n• Keep within designated slot line\n• Strictly speed limit 10 km/h\n• Gate access card provided at guard post",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookingCode: String,
    val userId: Long,
    val userName: String,
    val parkingSpaceId: Long,
    val parkingTitle: String,
    val parkingAddress: String,
    val parkingCity: String,
    val vehicleType: String, // "Car", "Bike", "SUV", "Van"
    val vehicleRegNumber: String,
    val bookingDate: String,
    val startTime: String,
    val endTime: String,
    val durationHours: Int,
    val subtotal: Double,
    val platformFee: Double,
    val totalAmount: Double,
    val status: String = "Confirmed", // "Pending", "Confirmed", "Active", "Completed", "Cancelled", "No-show"
    val paymentMethod: String = "UPI", // "UPI", "Card", "Net Banking", "Wallet"
    val paymentStatus: String = "Paid", // "Paid", "Pending", "Refunded"
    val qrData: String,
    val providerEarnings: Double = 0.0,
    val isReviewed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 1,
    val type: String, // "Car", "Bike", "SUV", "Van"
    val registrationNumber: String,
    val model: String,
    val isDefault: Boolean = false
)

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookingId: Long,
    val userId: Long,
    val userName: String,
    val parkingSpaceId: Long,
    val rating: Float,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 1,
    val title: String,
    val message: String,
    val type: String = "Booking", // "Booking", "Payment", "Earnings", "Review", "Listing"
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "blocked_slots")
data class BlockedSlot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val parkingSpaceId: Long,
    val date: String,
    val startTime: String,
    val endTime: String,
    val reason: String = "Owner Reserved"
)

@Entity(tableName = "platform_settings")
data class PlatformSettings(
    @PrimaryKey val id: Int = 1,
    val commissionPercentage: Double = 10.0, // 10%
    val minimumPayoutAmount: Double = 500.0,
    val autoApproveBookings: Boolean = true,
    val platformName: String = "ParkEasy",
    val supportEmail: String = "support@parkeasy.in",
    val supportPhone: String = "1800-PARK-EASE"
)
