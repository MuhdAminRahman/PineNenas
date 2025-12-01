package com.example.pinenenas.ui.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.pinenenas.MainActivity
import com.example.pinenenas.MapsActivity
import com.example.pinenenas.data.local.SessionManager
import com.example.pinenenas.databinding.FragmentOnboardingBinding
import com.google.android.gms.maps.model.LatLng

class OnboardingFragment : Fragment() {

    // Use view binding to safely access views
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    // Get a reference to the ViewModel
    private val viewModel: OnboardingViewModel by viewModels()
    private var selectedLatLng: LatLng? = null

    private val mapResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val latitude = data?.getDoubleExtra("latitude", -1.0)
            val longitude = data?.getDoubleExtra("longitude", -1.0)

            if (latitude != -1.0 && longitude != -1.0 && latitude != null && longitude != null) {
                selectedLatLng = LatLng(latitude, longitude)
                binding.textSelectedLocation.text = "Location Selected: ${"%.4f".format(latitude)}, ${"%.4f".format(longitude)}"
                binding.textSelectedLocation.visibility = View.VISIBLE
            }
        }
    }

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
        binding.buttonPinLocation.setOnClickListener {
            val intent = Intent(activity, MapsActivity::class.java).apply {
                // Pass a flag to tell MapsActivity we are in "select" mode
                putExtra("MODE", "SELECT")
            }
            mapResultLauncher.launch(intent)
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

        if (selectedLatLng == null) {
            Toast.makeText(context, "Please pin your shop location", Toast.LENGTH_SHORT).show()
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
        viewModel.saveUserDetails(
            userId = currentUserId,
            fullName = fullName,
            age = age,
            contact = contact,
            shopName = shopName,
            shopDesc = shopDesc,
            latitude = selectedLatLng?.latitude,
            longitude = selectedLatLng?.longitude
        )
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
