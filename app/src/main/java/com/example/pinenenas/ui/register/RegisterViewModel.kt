package com.example.pinenenas.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel // <-- CHANGE: Inherit from AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import android.util.Patterns
import com.example.pinenenas.data.Result
import com.example.pinenenas.R
import com.example.pinenenas.data.LoginRepository
import com.example.pinenenas.ui.login.LoggedInUserView
import kotlinx.coroutines.launch

// CHANGE: Inherit from AndroidViewModel and pass application to it.
class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    // CHANGE: Initialize LoginRepository here, using the application context.
    private val loginRepository: LoginRepository = LoginRepository(application)

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun register(username: String, email: String, password: String, confirmPassword: String, displayName: String) {
        viewModelScope.launch {
            _registerResult.value = RegisterResult(loading = true)

            // The rest of the function remains the same.
            val result = loginRepository.register(username, email, password, displayName)

            if (result is Result.Success) {
                _registerResult.value = RegisterResult(success = LoggedInUserView(displayName = result.data.displayName))
            } else {
                _registerResult.value = RegisterResult(error = result.toString())
            }
        }
    }

    // All other functions (registerDataChanged, isUserNameValid, etc.) remain unchanged.
    fun registerDataChanged(username: String, email: String, password: String, confirmPassword: String, displayName: String) {
        if (!isUserNameValid(username)) {
            _registerForm.value = RegisterFormState(usernameError = R.string.invalid_username)
        } else if (!isEmailValid(email)) {
            _registerForm.value = RegisterFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else if (!isConfirmPasswordValid(password, confirmPassword)) {
            _registerForm.value = RegisterFormState(confirmPasswordError = R.string.password_mismatch)
        } else if (!isDisplayNameValid(displayName)) {
            _registerForm.value = RegisterFormState(displayNameError = R.string.invalid_display_name)
        } else {
            _registerForm.value = RegisterFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank() && username.length >= 3
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    private fun isConfirmPasswordValid(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && confirmPassword.isNotBlank()
    }

    private fun isDisplayNameValid(displayName: String): Boolean {
        return displayName.isNotBlank() && displayName.length >= 2
    }
}
