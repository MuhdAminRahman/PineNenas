package com.example.pinenenas.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.pinenenas.MapsActivity
import com.example.pinenenas.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProfileData()
    }

    private fun observeProfileData() {
        // Use lifecycleScope to launch a coroutine that is automatically
        // cancelled when the fragment's view is destroyed.
        viewLifecycleOwner.lifecycleScope.launch {
            // collectLatest will cancel and restart the block if new data arrives
            // before the previous block has finished execution.
            viewModel.userDetail.collectLatest { userDetail ->
                if (userDetail != null) {
                    // We have data, so update the UI
                    binding.textFullName.text = userDetail.fullName
                    binding.textAge.text = userDetail.age.toString()
                    binding.textContactNumber.text = userDetail.contactNumber
                    binding.textShopName.text = userDetail.shopName
                    binding.textShopDescription.text = userDetail.shopDescription
                    if (userDetail.shopLatitude != null && userDetail.shopLongitude != null) {
                        binding.buttonViewShopOnMap.visibility = View.VISIBLE
                        binding.buttonViewShopOnMap.setOnClickListener {
                            val intent = Intent(activity, MapsActivity::class.java).apply {
                                putExtra("MODE", "VIEW")
                                putExtra("latitude", userDetail.shopLatitude)
                                putExtra("longitude", userDetail.shopLongitude)
                            }
                            startActivity(intent)
                        }
                    } else {
                        binding.buttonViewShopOnMap.visibility = View.GONE
                    }
                    if (userDetail.farmLatitude != null && userDetail.farmLongitude != null) {
                        binding.buttonViewFarmOnMap.visibility = View.VISIBLE
                        binding.buttonViewFarmOnMap.setOnClickListener {
                            val intent = Intent(activity, MapsActivity::class.java).apply {
                                putExtra("MODE", "VIEW")
                                putExtra("latitude", userDetail.farmLatitude)
                                putExtra("longitude", userDetail.farmLongitude)
                            }
                            startActivity(intent)
                        }
                    } else {
                        binding.buttonViewFarmOnMap.visibility = View.GONE
                    }

                    setupSocialMediaButton(binding.buttonInstagram, userDetail.instagramHandle) { handle -> "https://www.instagram.com/$handle" }
                    setupSocialMediaButton(binding.buttonFacebook, userDetail.facebookUrl) { url -> url } // Assumes full URL
                    setupSocialMediaButton(binding.buttonTiktok, userDetail.tiktokHandle) { handle -> "https://www.tiktok.com/@$handle" }

                } else {
                    // The userDetail is null, which means no profile exists yet.
                    // You could show a message or a button to create one.
                    binding.textFullName.text = "No profile found."
                    // Clear other fields as well
                    binding.textAge.text = ""
                    binding.textContactNumber.text = ""
                    binding.textShopName.text = ""
                    binding.textShopDescription.text = ""
                }
            }
        }
    }
    private fun setupSocialMediaButton(button: View, value: String?, urlBuilder: (String) -> String) {
        if (!value.isNullOrBlank()) {
            button.visibility = View.VISIBLE
            button.setOnClickListener {
                try {
                    val url = urlBuilder(value)
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            button.visibility = View.GONE
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
