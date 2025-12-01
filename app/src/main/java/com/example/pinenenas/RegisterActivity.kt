package com.example.pinenenas

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pinenenas.databinding.ActivityRegisterBinding
import com.example.pinenenas.ui.login.LoggedInUserView
import com.example.pinenenas.ui.register.RegisterResult
import com.example.pinenenas.ui.register.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    // Use 'by viewModels()' to get the ViewModel instance
    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up all the UI listeners and observers
        setupFormValidation()
        setupRegisterButton()
        setupLoginNavigation()
        observeRegisterResult()
    }

    private fun setupFormValidation() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                registerViewModel.registerDataChanged(
                    binding.username.text.toString(),
                    binding.email.text.toString(),
                    binding.password.text.toString(),
                    binding.confirmPassword.text.toString(),
                    binding.displayName.text.toString()
                )
            }
        }

        binding.username.addTextChangedListener(textWatcher)
        binding.email.addTextChangedListener(textWatcher)
        binding.password.addTextChangedListener(textWatcher)
        binding.confirmPassword.addTextChangedListener(textWatcher)
        binding.displayName.addTextChangedListener(textWatcher)

        registerViewModel.registerFormState.observe(this) { formState ->
            binding.register.isEnabled = formState.isDataValid
            formState.usernameError?.let { binding.username.error = getString(it) }
            formState.emailError?.let { binding.email.error = getString(it) }
            formState.passwordError?.let { binding.password.error = getString(it) }
            formState.confirmPasswordError?.let { binding.confirmPassword.error = getString(it) }
            formState.displayNameError?.let { binding.displayName.error = getString(it) }
        }
    }

    private fun setupRegisterButton() {
        binding.register.setOnClickListener {
            val username = binding.username.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            val displayName = binding.displayName.text.toString()

            binding.loading.visibility = View.VISIBLE
            registerViewModel.register(username, email, password, confirmPassword, displayName)
        }
    }

    private fun setupLoginNavigation() {
        // When the login link is clicked, finish this activity to go back to the previous one (LoginActivity)
        binding.loginLink.setOnClickListener {
            finish()
        }
    }

    private fun observeRegisterResult() {
        registerViewModel.registerResult.observe(this) { result ->
            binding.loading.visibility = View.GONE
            when {
                result.loading -> binding.loading.visibility = View.VISIBLE
                result.success != null -> {
                    updateUiWithUser(result.success)
                    // After successful registration, navigate back to LoginActivity
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                }
                result.error != null -> showRegisterFailed(result.error)
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = "Welcome ${model.displayName}! Please log in."
        Toast.makeText(this, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showRegisterFailed(error: String) {
        Toast.makeText(this, "Registration failed: $error", Toast.LENGTH_LONG).show()
    }
}
