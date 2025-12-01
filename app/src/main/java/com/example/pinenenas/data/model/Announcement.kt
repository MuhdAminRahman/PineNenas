package com.example.pinenenas.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "announcements",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Announcement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val authorId: Long,
    val authorDisplayName: String, // Denormalized for easy display
    val content: String,
    val price: Double? = null, // Optional field for price
    val timestamp: Long = System.currentTimeMillis()
)
