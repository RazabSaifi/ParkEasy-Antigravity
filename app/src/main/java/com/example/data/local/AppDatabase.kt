package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BlockedSlot
import com.example.data.model.Booking
import com.example.data.model.NotificationItem
import com.example.data.model.ParkingSpace
import com.example.data.model.PlatformSettings
import com.example.data.model.Review
import com.example.data.model.User
import com.example.data.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        ParkingSpace::class,
        Booking::class,
        Vehicle::class,
        Review::class,
        NotificationItem::class,
        BlockedSlot::class,
        PlatformSettings::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun parkingSpaceDao(): ParkingSpaceDao
    abstract fun bookingDao(): BookingDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun reviewDao(): ReviewDao
    abstract fun notificationDao(): NotificationDao
    abstract fun blockedSlotDao(): BlockedSlotDao
    abstract fun platformSettingsDao(): PlatformSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "parkspace_database"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                database.userDao().insertOrUpdate(DemoData.initialUser)
                database.platformSettingsDao().saveSettings(DemoData.initialSettings)
                database.vehicleDao().insertAll(DemoData.initialVehicles)
                database.parkingSpaceDao().insertAll(DemoData.initialSpaces)
                database.bookingDao().insertAll(DemoData.initialBookings)
                database.reviewDao().insertAll(DemoData.initialReviews)
                database.notificationDao().insertAll(DemoData.initialNotifications)
            }
        }
    }
}
