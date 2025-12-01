package com.example.pinenenas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pinenenas.data.local.SessionManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)
        sessionManager.logout()
        // Determine which activity to navigate to
        val targetActivity = when {
            // Case 1: User is NOT logged in -> go to LoginActivity
            // You will need to create LoginActivity if it doesn't exist yet.
            !sessionManager.isLoggedIn() -> LoginActivity::class.java

            // Case 2: User IS logged in but has NOT completed onboarding -> go to OnboardingActivity
            !sessionManager.hasCompletedOnboarding() -> OnboardingActivity::class.java

            // Case 3: User IS logged in AND has completed onboarding -> go to MainActivity
            else -> MainActivity::class.java
        }

        // Start the determined activity and immediately finish this SplashActivity
        // so the user cannot navigate back to it.
        startActivity(Intent(this, targetActivity))
        finish()
    }
}
