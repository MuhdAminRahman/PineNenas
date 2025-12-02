package com.example.pinenenas.ui.marketplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pinenenas.R
import com.example.pinenenas.data.model.UserDetail
import com.example.pinenenas.databinding.FragmentMarketplaceBinding

class MarketplaceFragment : Fragment(), MarketplaceAdapter.OnShopClickListener {

    private var _binding: FragmentMarketplaceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MarketplaceViewModel by viewModels()
    private lateinit var marketplaceAdapter: MarketplaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarketplaceBinding.inflate(inflater, container, false)

        // Initialize the adapter, passing 'this' fragment as the click listener
        marketplaceAdapter = MarketplaceAdapter(this)

        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeShops()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewShops.adapter = marketplaceAdapter
    }

    private fun observeShops() {
        // Observe the LiveData from the ViewModel
        viewModel.allShops.observe(viewLifecycleOwner) { shops ->
            // When the list of shops changes, submit it to the adapter to update the UI
            shops?.let {
                marketplaceAdapter.submitList(it)
            }
        }
    }

    /**
     * This method is called from the MarketplaceAdapter when a user clicks on a shop.
     */
    override fun onShopClick(userDetail: UserDetail) {
        // Create a bundle with the necessary arguments for MyShopFragment
        val bundle = Bundle().apply {
            putLong("userId", userDetail.userId)
            putString("shopName", userDetail.shopName)
        }
        // Use the NavController to navigate to the shop, using the action defined in mobile_navigation.xml
        findNavController().navigate(R.id.action_nav_marketplace_to_myShopFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
