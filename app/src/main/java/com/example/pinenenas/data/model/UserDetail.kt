package com.example.pinenenas.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_detail",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // If a User is deleted, delete their profile too
        )
    ]
)
data class UserDetail(
    @PrimaryKey
    val userId: Long, // This links directly to the User's ID
    val fullName: String,
    val age: Int,
    val contactNumber: String,
    val shopName: String,
    val shopDescription: String,
    val shopLatitude: Double?,
    val shopLongitude: Double?
)
    