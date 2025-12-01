package com.example.pinenenas.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pinenenas.data.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): User?

    // In UserDao.kt
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun loginUser(username: String, password: String): User?


    @Query("SELECT COUNT(*) FROM users WHERE username = :username OR email = :email")
    suspend fun checkUserExists(username: String, email: String): Int
}