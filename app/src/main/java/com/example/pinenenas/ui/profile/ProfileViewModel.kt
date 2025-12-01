package com.example.pinenenas.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinenenas.data.local.AppDatabase
import com.example.pinenenas.data.local.SessionManager
import com.example.pinenenas.data.model.UserDetail
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userDetailDao = AppDatabase.getInstance(application).userDetail()
    // --- Correctly get the SessionManager instance ---
    private val sessionManager = SessionManager(application)

    // --- Dynamically get the current user's ID from the session ---
    private val currentUserId = sessionManager.getLoggedInUserId()

    // Expose the user's profile as a StateFlow.
    val userDetail: StateFlow<UserDetail?> = if (currentUserId != -1L) {
        // If a user is logged in, fetch their profile from the database.
        userDetailDao.getProfile(currentUserId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    } else {
        // If no user is logged in, provide a flow with a null value.
        flowOf(null).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }
}
