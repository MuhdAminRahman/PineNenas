package com.example.pinenenas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pinenenas.data.model.User
import com.example.pinenenas.data.model.UserDetail
import com.example.pinenenas.data.model.Announcement
import com.example.pinenenas.data.model.Product

@Database(entities = [User::class, UserDetail::class, Announcement::class, Product::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userDetail(): UserDetailDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                "app_database"
                            ).fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}