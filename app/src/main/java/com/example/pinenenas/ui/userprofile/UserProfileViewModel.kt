package com.example.pinenenas.ui.userprofile

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.pinenenas.data.local.AppDatabase
import com.example.pinenenas.data.model.UserDetail
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class UserProfileViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    private val userDetailDao = AppDatabase.getInstance(application).userDetail()
    private val userId: Long? = savedStateHandle.get<Long>("userId")

    // Use flatMapLatest to react to changes in the userId argument
    val userDetail: StateFlow<UserDetail?> = if (userId != null && userId != -1L) {
        // If we have a valid userId, start a flow to get the profile from the database.
        userDetailDao.getProfile(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    } else {
        // If userId is null or invalid, create a flow that just emits null.
        // This ensures the UI will show "Profile not available".
        Toast.makeText(application, "Invalid user ID", Toast.LENGTH_SHORT).show()
        flowOf(null).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }
}
