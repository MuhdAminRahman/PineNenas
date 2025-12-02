package com.example.pinenenas.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinenenas.data.local.AppDatabase
import com.example.pinenenas.data.local.SessionManager
import com.example.pinenenas.data.model.UserDetail
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    // Get a reference to the DAO from the database
    private val userDetailDao = AppDatabase.getInstance(application).userDetail()
    private val sessionManager = SessionManager(application)

    /**
     * Saves the user's details to the database.
     * This function should be called from the Fragment.
     *
     * @param userId The ID of the currently logged-in user.
     * @param fullName The user's full name.
     * @param age The user's age.
     * @param contact The user's contact number.
     * @param shopName The user's shop name.
     * @param shopDesc The description of the user's shop.
     */
    fun saveUserDetails(
        userId: Long, // You will need to pass the logged-in user's ID
        fullName: String,
        age: String, // Take as String for validation, then convert
        contact: String,
        shopName: String,
        shopDesc: String,
        shopLatitude: Double?,
        shopLongitude: Double?,
        farmLatitude: Double?,
        farmLongitude: Double?,
        instagram: String?,
        facebook: String?,
        tiktok: String?
    ) {
        // Basic validation
        if (fullName.isBlank() || age.isBlank() || contact.isBlank() || shopName.isBlank()) {
            // you would expose an error state via LiveData here
            return
        }

        // Use a coroutine to perform the database operation off the main thread
        viewModelScope.launch {
            val userDetail = UserDetail(
                userId = userId,
                fullName = fullName,
                age = age.toIntOrNull() ?: 0,
                contactNumber = contact,
                shopName = shopName,
                shopDescription = shopDesc,
                shopLatitude = shopLatitude,
                shopLongitude = shopLongitude,
                farmLatitude = farmLatitude,
                farmLongitude = farmLongitude,
                instagramHandle = instagram?.takeIf { it.isNotBlank() },
                facebookUrl = facebook?.takeIf { it.isNotBlank() },
                tiktokHandle = tiktok?.takeIf { it.isNotBlank() }
            )
            userDetailDao.saveProfile(userDetail)
            sessionManager.saveOnboardingStatus(true)
        }
    }
}
