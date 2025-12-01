package com.example.pinenenas.ui.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pinenenas.data.LoginRepository

/**
 * Factory for creating a LoginViewModel with a constructor that takes an
 * Application and LoginRepository.
 */
class LoginViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            // Create and return an instance of LoginViewModel
            return LoginViewModel(
                application = application,
                loginRepository = LoginRepository(application.applicationContext)
            ) as T
        }
        // If the ViewModel class is unknown, throw an exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
