package com.example.pinenenas.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pinenenas.databinding.FragmentRegisterBinding
import com.example.pinenenas.ui.login.LoggedInUserView
import com.example.pinenenas.ui.login.LoginViewModelFactory
import com.example.pinenenas.ui.register.RegisterViewModel
import com.example.pinenenas.R
class RegisterFragment : Fragment() {

    private lateinit var registerViewModel: RegisterViewModel
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerViewModel = ViewModelProvider(this, LoginViewModelFactory(requireContext()))
            .get(RegisterViewModel::class.java)

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

        registerViewModel.registerFormState.observe(viewLifecycleOwner) { formState ->
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
        binding.loginLink.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeRegisterResult() {
        registerViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            binding.loading.visibility = View.GONE

            when {
                result.loading -> binding.loading.visibility = View.VISIBLE
                result.success != null -> {
                    updateUiWithUser(result.success)
                    findNavController().navigate(R.id.nav_home)
                }
                result.error != null -> showRegisterFailed(result.error)
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = "Welcome ${model.displayName}!"
        Toast.makeText(requireContext(), welcome, Toast.LENGTH_LONG).show()
    }

    private fun showRegisterFailed(error: String) {
        Toast.makeText(requireContext(), "Registration failed: $error", Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}