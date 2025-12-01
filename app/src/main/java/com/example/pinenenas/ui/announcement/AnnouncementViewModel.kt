package com.example.pinenenas.ui.announcement

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pinenenas.data.local.AppDatabase
import com.example.pinenenas.data.local.SessionManager
import com.example.pinenenas.data.model.Announcement
import kotlinx.coroutines.launch

class AnnouncementViewModel(application: Application) : AndroidViewModel(application) {

    private val announcementDao = AppDatabase.getInstance(application).announcementDao()
    private val sessionManager = SessionManager(application)
    private val userDao = AppDatabase.getInstance(application).userDao()

    // Expose a LiveData stream of all announcements from the database
    val allAnnouncements: LiveData<List<Announcement>> = announcementDao.getAllAnnouncements().asLiveData()

    fun postAnnouncement(content: String, price: Double?) {
        viewModelScope.launch {
            val userId = sessionManager.getLoggedInUserId()
            if (userId == -1L) return@launch // Not logged in, do nothing

            // Get the current user's display name
            val user = userDao.getUserById(userId)
            if (user == null) return@launch

            val newAnnouncement = Announcement(
                authorId = userId,
                authorDisplayName = user.displayName,
                content = content,
                price = price
            )
            announcementDao.insert(newAnnouncement)
        }
    }

    fun getCurrentUserId(): Long {
        return sessionManager.getLoggedInUserId()
    }
}
