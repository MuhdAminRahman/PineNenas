package com.example.pinenenas.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.pinenenas.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
