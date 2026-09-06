package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BlockedSlot
import com.example.data.model.Booking
import com.example.data.model.NotificationItem
import com.example.data.model.ParkingSpace
import com.example.data.model.PlatformSettings
import com.example.data.model.Review
import com.example.data.model.User
import com.example.data.model.Vehicle
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUser(userId: Long): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: User)

    @Query("UPDATE users SET activeMode = :mode WHERE id = :userId")
    suspend fun setActiveMode(userId: Long, mode: String)

    @Query("UPDATE users SET totalEarnings = totalEarnings + :amount, availableBalance = availableBalance + :amount WHERE id = :userId")
    suspend fun addEarnings(userId: Long, amount: Double)

    @Query("UPDATE users SET availableBalance = availableBalance - :amount, withdrawnAmount = withdrawnAmount + :amount WHERE id = :userId")
    suspend fun withdrawBalance(userId: Long, amount: Double)

    @Query("UPDATE users SET isIdVerified = :idVerified, isPropertyVerified = :propVerified WHERE id = :userId")
    suspend fun updateKyc(userId: Long, idVerified: Boolean, propVerified: Boolean)

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<User>>
}

@Dao
interface ParkingSpaceDao {
    @Query("SELECT * FROM parking_spaces ORDER BY rating DESC, id DESC")
    fun getAllSpaces(): Flow<List<ParkingSpace>>

    @Query("SELECT * FROM parking_spaces WHERE ownerId = :ownerId ORDER BY id DESC")
    fun getSpacesByOwner(ownerId: Long): Flow<List<ParkingSpace>>

    @Query("SELECT * FROM parking_spaces WHERE id = :id LIMIT 1")
    fun getSpaceById(id: Long): Flow<ParkingSpace?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpace(space: ParkingSpace): Long

    @Update
    suspend fun updateSpace(space: ParkingSpace)

    @Query("UPDATE parking_spaces SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("UPDATE parking_spaces SET verificationStatus = :status WHERE id = :id")
    suspend fun updateVerificationStatus(id: Long, status: String)

    @Delete
    suspend fun deleteSpace(space: ParkingSpace)

    @Query("SELECT COUNT(*) FROM parking_spaces")
    suspend fun countSpaces(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(spaces: List<ParkingSpace>)
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE userId = :userId ORDER BY id DESC")
    fun getBookingsByUser(userId: Long): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE parkingSpaceId IN (SELECT id FROM parking_spaces WHERE ownerId = :ownerId) ORDER BY id DESC")
    fun getBookingsForProvider(ownerId: Long): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    fun getBookingById(id: Long): Flow<Booking?>

    @Query("SELECT * FROM bookings WHERE bookingCode = :code LIMIT 1")
    suspend fun getBookingByCode(code: String): Booking?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking): Long

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Long, status: String)

    @Query("UPDATE bookings SET isReviewed = 1 WHERE id = :id")
    suspend fun markReviewed(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookings: List<Booking>)
}

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles WHERE userId = :userId ORDER BY isDefault DESC, id DESC")
    fun getVehiclesByUser(userId: Long): Flow<List<Vehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle): Long

    @Query("DELETE FROM vehicles WHERE id = :id")
    suspend fun deleteVehicle(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vehicles: List<Vehicle>)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE parkingSpaceId = :spaceId ORDER BY id DESC")
    fun getReviewsForSpace(spaceId: Long): Flow<List<Review>>

    @Query("SELECT * FROM reviews ORDER BY id DESC")
    fun getAllReviews(): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review): Long

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteReview(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<Review>)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY id DESC")
    fun getNotificationsByUser(userId: Long): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationItem>)
}

@Dao
interface BlockedSlotDao {
    @Query("SELECT * FROM blocked_slots WHERE parkingSpaceId = :spaceId ORDER BY id DESC")
    fun getBlockedSlots(spaceId: Long): Flow<List<BlockedSlot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedSlot(slot: BlockedSlot): Long

    @Query("DELETE FROM blocked_slots WHERE id = :id")
    suspend fun deleteBlockedSlot(id: Long)
}

@Dao
interface PlatformSettingsDao {
    @Query("SELECT * FROM platform_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<PlatformSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: PlatformSettings)
}
