package com.example.pinenenas.ui.register

data class RegisterFormState(
    val usernameError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val displayNameError: Int? = null,
    val isDataValid: Boolean = false
)