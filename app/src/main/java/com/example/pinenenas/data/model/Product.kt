package com.example.pinenenas.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Long, // To link the product to a user
    val name: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null // Optional image for the product
)
