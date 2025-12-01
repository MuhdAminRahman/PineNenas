package com.example.pinenenas.ui.login

/**
 * Represents the result of a login attempt. It is a sealed class,
 * meaning it can only be one of the defined nested types (Success or Error).
 */
sealed class LoginResult {
    /**
     * Represents a successful login.
     * @param isOnboardingComplete A flag to determine if the user needs to go through onboarding.
     */
    data class Success(val isOnboardingComplete: Boolean) : LoginResult()

    /**
     * Represents a failed login attempt.
     * @param message The error message to be displayed to the user.
     */
    data class Error(val message: String) : LoginResult()
}
