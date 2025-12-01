package com.example.pinenenas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider // Import ViewModelProvider
import com.example.pinenenas.databinding.ActivityLoginBinding
import com.example.pinenenas.ui.login.LoginResult
import com.example.pinenenas.ui.login.LoginViewModel
import com.example.pinenenas.ui.login.LoginViewModelFactory // Import your factory

class LoginActivity : AppCompatActivity() {

    // Correctly initialize the ViewModel using the factory
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = LoginViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        observeLoginResult()
        setupLoginButton()
        setupRegisterNavigation()
    }

    private fun observeLoginResult() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    val targetActivity = if (result.isOnboardingComplete) {
                        MainActivity::class.java
                    } else {
                        OnboardingActivity::class.java
                    }
                    val intent = Intent(this, targetActivity)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupLoginButton() {
        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(username, password)
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRegisterNavigation(){
        binding.textViewGoToRegister.setOnClickListener {
            // Start the RegisterActivity when the text is clicked
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
