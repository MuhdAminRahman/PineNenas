package com.example.pinenenas.ui.register

import com.example.pinenenas.ui.login.LoggedInUserView

data class RegisterResult(
    val success: LoggedInUserView? = null,
    val error: String? = null,
    val loading: Boolean = false
)