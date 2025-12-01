package com.example.pinenenas.data

import com.example.pinenenas.data.model.LoggedInUser

import android.content.Context
import com.example.pinenenas.data.local.AppDatabase
import com.example.pinenenas.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginRepository(private val context: Context) {

    private val database = AppDatabase.getInstance(context)

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            val user = withContext(Dispatchers.IO) {
                database.userDao().loginUser(username, password)
            }
            if (user != null) {
                Result.Success(LoggedInUser(user.id.toString(), user.displayName))
            } else {
                Result.Error(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun register(username: String, email: String, password: String, displayName: String): Result<LoggedInUser> {
        return try {
            // Check if user already exists
            val existingUser = withContext(Dispatchers.IO) {
                database.userDao().checkUserExists(username, email)
            }

            if (existingUser > 0) {
                return Result.Error(Exception("User already exists"))
            }

            val user = User(
                username = username,
                email = email,
                password = password,
                displayName = displayName
            )

            val userId = withContext(Dispatchers.IO) {
                database.userDao().insert(user)
            }

            if (userId > 0) {
                Result.Success(LoggedInUser(userId.toString(), displayName))
            } else {
                Result.Error(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}