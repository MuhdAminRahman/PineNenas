package com.example.pinenenas.ui.myproducts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinenenas.R
import com.example.pinenenas.data.model.Product
import com.example.pinenenas.databinding.FragmentMyProductsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MyProductsFragment : Fragment(), ProductAdapter.OnItemClickListener {

    private var _binding: FragmentMyProductsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyProductsViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProductsBinding.inflate(inflater, container, false)

        // Initialize the adapter and pass 'this' as the listener
        productAdapter = ProductAdapter(this)

        setupRecyclerView()
        setupFab()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProducts()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewProducts.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupFab() {
        binding.fabAddProduct.setOnClickListener {
            showAddEditProductDialog(null)        }
    }

    private fun observeProducts() {
        // Observe the LiveData from the ViewModel
        viewModel.userProducts.observe(viewLifecycleOwner) { products ->
            products?.let {
                productAdapter.submitList(it)
            }
        }
    }

    // This method is called when the "Edit" button is clicked in the adapter
    override fun onEditClick(product: Product) {
        showAddEditProductDialog(product)    }

    // This method is called when the "Delete" button is clicked in the adapter
    override fun onDeleteClick(product: Product) {
        // Show a confirmation dialog before deleting
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete '${product.name}'?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewModel.delete(product)
                Toast.makeText(context, "'${product.name}' deleted", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    /**
     * Shows a dialog to add a new product or edit an existing one.
     * @param product The product to edit, or null to add a new one.
     */
    private fun showAddEditProductDialog(product: Product?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_edit_product, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_text_product_name)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.edit_text_product_description)
        val priceEditText = dialogView.findViewById<EditText>(R.id.edit_text_product_price)
        val quantityEditText = dialogView.findViewById<EditText>(R.id.edit_text_product_quantity)

        val isEditing = product != null
        val dialogTitle = if (isEditing) "Edit Product" else "Add New Product"
        val positiveButtonText = if (isEditing) "Save" else "Add"

        // If editing, pre-fill the fields with existing product data
        if (isEditing) {
            nameEditText.setText(product.name)
            descriptionEditText.setText(product.description)
            priceEditText.setText(product.price.toString())
            quantityEditText.setText(product.quantity.toString())
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(dialogTitle)
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton(positiveButtonText) { _, _ ->
                val name = nameEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()
                val price = priceEditText.text.toString().toDoubleOrNull()
                val quantity = quantityEditText.text.toString().toIntOrNull()

                if (name.isNotEmpty() && description.isNotEmpty() && price != null && quantity != null) {
                    if (isEditing) {
                        // Update existing product
                        val updatedProduct = product.copy(
                            name = name,
                            description = description,
                            price = price,
                            quantity = quantity
                        )
                        viewModel.update(updatedProduct)
                        Toast.makeText(context, "Product updated", Toast.LENGTH_SHORT).show()
                    } else {
                        val currentUserId = viewModel.getCurrentUserId()
                        if (currentUserId == -1L) {
                            Toast.makeText(context, "Error: Not logged in", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }
                        val newProduct = Product(
                            userId =currentUserId,
                            name = name,
                            description = description,
                            price = price,
                            quantity = quantity
                        )
                        viewModel.insert(newProduct)
                        Toast.makeText(context, "Product added", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please fill out all fields correctly", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
