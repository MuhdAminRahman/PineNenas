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
    private var selectedShopLatLng: LatLng? = null
    private var selectedFarmLatLng: LatLng? = null
    private var mapLauncherMode: String? = null // To know which button was clicked


    private val mapResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val latitude = data?.getDoubleExtra("latitude", -1.0)
            val longitude = data?.getDoubleExtra("longitude", -1.0)

            if (latitude != null && longitude != null && latitude != -1.0 && longitude != -1.0) {
                val selectedLatLng = LatLng(latitude, longitude)
                // Check which location we were selecting
                if (mapLauncherMode == "SHOP") {
                    selectedShopLatLng = selectedLatLng
                    binding.textSelectedShopLocation.text = "Shop Location: ${"%.4f".format(latitude)}, ${"%.4f".format(longitude)}"
                    binding.textSelectedShopLocation.visibility = View.VISIBLE
                } else if (mapLauncherMode == "FARM") {
                    selectedFarmLatLng = selectedLatLng
                    binding.textSelectedFarmLocation.text = "Farm Location: ${"%.4f".format(latitude)}, ${"%.4f".format(longitude)}"
                    binding.textSelectedFarmLocation.visibility = View.VISIBLE
                }
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

        binding.buttonPinShopLocation.setOnClickListener {
            mapLauncherMode = "SHOP" // Set mode before launching
            val intent = Intent(activity, MapsActivity::class.java).apply {
                putExtra("MODE", "SELECT")
            }
            mapResultLauncher.launch(intent)
        }

        binding.buttonPinFarmLocation.setOnClickListener {
            mapLauncherMode = "FARM" // Set mode before launching
            val intent = Intent(activity, MapsActivity::class.java).apply {
                putExtra("MODE", "SELECT")
            }
            mapResultLauncher.launch(intent)
        }

        binding.buttonSaveProfile.setOnClickListener {
            saveProfileData()
        }
    }

    private fun saveProfileData() {
        // ... (get text from input fields like fullName, age, instagram, etc.)
        val fullName = binding.editTextFullName.text.toString().trim()
        val age = binding.editTextAge.text.toString().trim()
        val contact = binding.editTextContactNumber.text.toString().trim()
        val shopName = binding.editTextShopName.text.toString().trim()
        val shopDesc = binding.editTextShopDescription.text.toString().trim()
        val instagram = binding.editTextInstagram.text.toString().trim()
        val facebook = binding.editTextFacebook.text.toString().trim()
        val tiktok = binding.editTextTiktok.text.toString().trim()

        // Validation for required fields remains the same
        if (fullName.isEmpty() || age.isEmpty() || contact.isEmpty() || shopName.isEmpty()) {
            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Validation for locations
        if (selectedShopLatLng == null) {
            Toast.makeText(context, "Please pin your shop/pickup location", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedFarmLatLng == null) {
            Toast.makeText(context, "Please pin your farm location", Toast.LENGTH_SHORT).show()
            return
        }

        val sessionManager = SessionManager(requireContext())
        val currentUserId = sessionManager.getLoggedInUserId()
        // ... (check if currentUserId is valid) ...

        viewModel.saveUserDetails(
            userId = currentUserId,
            fullName = fullName,
            age = age,
            contact = contact,
            shopName = shopName,
            shopDesc = shopDesc,
            shopLatitude = selectedShopLatLng?.latitude,
            shopLongitude = selectedShopLatLng?.longitude,
            farmLatitude = selectedFarmLatLng?.latitude,
            farmLongitude = selectedFarmLatLng?.longitude,
            instagram = instagram,
            facebook = facebook,
            tiktok = tiktok
        )
        // ... (navigate to MainActivity) ...
        Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
