package com.example.pinenenas.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
