package com.example.pinenenas.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pinenenas.R
import com.example.pinenenas.data.LoginRepository
import com.example.pinenenas.data.local.AppDatabase
import com.example.pinenenas.data.local.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class LoginViewModel(application: Application, loginRepository: LoginRepository) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getInstance(application).userDao()
    private val userDetailDao = AppDatabase.getInstance(application).userDetail()
    private val sessionManager = SessionManager(application)

    // LiveData to notify the Activity about the login result
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm


    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) { // Use IO dispatcher for database calls
            val user = userDao.loginUser(username, password)

            if (user == null) {
                // If user is null, post an error and stop.
                _loginResult.postValue(LoginResult.Error("Invalid username or password"))
                return@launch
            }

            // --- If we reach here, the login was successful ---

            // 1. Save the session
            sessionManager.saveLoginSession(user.id)

            // 2. Check if onboarding is complete
            val userDetail = userDetailDao.getProfile(user.id).firstOrNull() // Check database
            val isOnboardingComplete = (userDetail != null)
            sessionManager.saveOnboardingStatus(isOnboardingComplete)

            // 3. Post the final, correct successful result
            _loginResult.postValue(LoginResult.Success(isOnboardingComplete))
        }
    }
    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }
    private fun isUserNameValid(username: String): Boolean {
        // You can add your own validation logic here
        return username.isNotBlank()
    }

    private fun isPasswordValid(password: String): Boolean {
        // You can add your own validation logic here
        return password.length > 5
    }
}