// /app/src/main/java/com/example/pinenenas/ui/myshop/MyShopFragment.kt
package com.example.pinenenas.ui.myshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinenenas.data.model.Product
import com.example.pinenenas.databinding.FragmentMyShopBinding
import com.example.pinenenas.ui.myproducts.ProductAdapter

class MyShopFragment : Fragment(), ProductAdapter.OnItemClickListener {

    private var _binding: FragmentMyShopBinding? = null
    private val binding get() = _binding!!

    // Use the custom factory to instantiate the ViewModel
    private val viewModel: MyShopViewModel by viewModels {
        MyShopViewModelFactory()
    }

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyShopBinding.inflate(inflater, container, false)

        // We re-use the ProductAdapter. 'this' is passed as the listener.
        productAdapter = ProductAdapter(this,isOwnerView = false)

        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeShopProducts()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewShopProducts.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeShopProducts() {
        viewModel.shopProducts.observe(viewLifecycleOwner) { products ->
            products?.let {
                productAdapter.submitList(it)
            }
        }
    }

    // Since this is a read-only view, these actions should be different.
    // For now, we can just show a Toast.
    // In the future, this is where you would implement a "Buy" or "Add to Cart" feature.
    override fun onEditClick(product: Product) {
        // This won't be called if we hide the button, but it's good practice to handle it.
        Toast.makeText(context, "Buy feature coming soon for ${product.name}!", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteClick(product: Product) {
        // This won't be called if we hide the button.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
