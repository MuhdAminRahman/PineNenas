package com.example.pinenenas.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.pinenenas.MainActivity
import com.example.pinenenas.data.local.SessionManager
import com.example.pinenenas.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    // Use view binding to safely access views
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    // Get a reference to the ViewModel
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSaveProfile.setOnClickListener {
            saveProfileData()
        }
    }

    private fun saveProfileData() {
        // Get text from input fields
        val fullName = binding.editTextFullName.text.toString().trim()
        val age = binding.editTextAge.text.toString().trim()
        val contact = binding.editTextContactNumber.text.toString().trim()
        val shopName = binding.editTextShopName.text.toString().trim()
        val shopDesc = binding.editTextShopDescription.text.toString().trim()

        // Simple validation
        if (fullName.isEmpty() || age.isEmpty() || contact.isEmpty() || shopName.isEmpty()) {
            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Get an instance of the SessionManager.
        val sessionManager = SessionManager(requireContext())

        // 2. Retrieve the ID of the currently logged-in user.
        val currentUserId = sessionManager.getLoggedInUserId()

        // 3. Check if the ID is valid.
        if (currentUserId == -1L) {
            // This is a safety check. If no user is logged in, we shouldn't proceed.
            Toast.makeText(context, "Error: No user is logged in.", Toast.LENGTH_LONG).show()
            // Optional: Navigate back to LoginActivity
            return
        }

        // Call the ViewModel to save the data
        viewModel.saveUserDetails(currentUserId, fullName, age, contact, shopName, shopDesc)

        Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()

        // Relaunch the MainActivity. It will now detect that onboarding is complete.
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
