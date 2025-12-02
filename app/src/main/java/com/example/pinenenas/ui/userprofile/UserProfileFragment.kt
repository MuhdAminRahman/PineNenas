package com.example.pinenenas.ui.userprofile


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
// Import NavController and findNavController
import androidx.navigation.fragment.findNavController
import com.example.pinenenas.MapsActivity
import com.example.pinenenas.R // Import R
import com.example.pinenenas.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

// This new fragment will display another user's profile
class UserProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // We will need a factory to pass the SavedStateHandle to the ViewModel
    private val viewModel: UserProfileViewModel by viewModels {
        UserProfileViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // We can reuse the existing fragment_profile.xml layout
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProfileData()
    }

    private fun observeProfileData() {
        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle ensures the coroutine is active only when the view is active
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userDetail.collect { userDetail ->
                    if (userDetail != null) {
                        binding.textFullName.text = userDetail.fullName
                        binding.textAge.text = userDetail.age.toString()
                        binding.textContactNumber.text = userDetail.contactNumber
                        binding.textShopName.text = userDetail.shopName
                        binding.textShopDescription.text = userDetail.shopDescription
                        if (userDetail.shopLatitude != null && userDetail.shopLongitude != null) {
                            binding.buttonViewOnMap.visibility = View.VISIBLE
                            binding.buttonViewOnMap.setOnClickListener {
                                val intent = Intent(activity, MapsActivity::class.java).apply {
                                    putExtra("MODE", "VIEW")
                                    putExtra("latitude", userDetail.shopLatitude)
                                    putExtra("longitude", userDetail.shopLongitude)
                                }
                                startActivity(intent)
                            }
                        } else {
                            binding.buttonViewOnMap.visibility = View.GONE
                        }
                        binding.buttonViewShop.visibility = View.VISIBLE
                        binding.buttonViewShop.setOnClickListener {
                            val bundle = Bundle().apply {
                                putLong("userId", userDetail.userId)
                                putString("shopName", userDetail.shopName)
                            }
                            findNavController().navigate(
                                R.id.action_userProfileFragment_to_myShopFragment,
                                bundle
                            )
                        }
                    } else {
                        binding.textFullName.text = "Profile not available."
                        binding.textAge.text = ""
                        binding.textContactNumber.text = ""
                        binding.textShopName.text = ""
                        binding.textShopDescription.text = ""
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
