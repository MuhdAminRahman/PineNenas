package com.example.pinenenas.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Manages user session data, such as login state and onboarding completion.
 */
class SessionManager(context: Context) {

    private var prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        const val ONBOARDING_COMPLETE = "onboarding_complete"
        // Key to store the ID of the logged-in user.
        const val LOGGED_IN_USER_ID = "logged_in_user_id"
    }

    /**
     * Saves the completion status of the onboarding process.
     */
    fun saveOnboardingStatus(isComplete: Boolean) {
        prefs.edit {
            putBoolean(ONBOARDING_COMPLETE, isComplete)
        }
    }

    /**
     * Checks if the user has completed the onboarding process.
     */
    fun hasCompletedOnboarding(): Boolean {
        return prefs.getBoolean(ONBOARDING_COMPLETE, false)
    }

    // --- NEW FUNCTIONS TO MANAGE LOGIN ---

    /**
     * Saves the user's ID to establish a login session.
     * @param userId The unique ID of the user who just logged in.
     */
    fun saveLoginSession(userId: Long) {
        prefs.edit {
            putLong(LOGGED_IN_USER_ID, userId)
        }
    }

    /**
     * Retrieves the ID of the currently logged-in user.
     * @return The user ID if logged in, otherwise -1.
     */
    fun getLoggedInUserId(): Long {
        // We use -1 to indicate that no user is logged in.
        return prefs.getLong(LOGGED_IN_USER_ID, -1L)
    }

    /**
     * Checks if a user is currently logged in.
     * @return True if a user ID is saved, false otherwise.
     */
    fun isLoggedIn(): Boolean {
        return getLoggedInUserId() != -1L
    }

    /**
     * Clears all session data, effectively logging the user out.
     */
    fun logout() {
        prefs.edit {
            remove(LOGGED_IN_USER_ID)
            remove(ONBOARDING_COMPLETE)
            // Use commit() for logout to ensure it happens synchronously if needed,
            // or apply() for asynchronous operation.
        }
    }
}
