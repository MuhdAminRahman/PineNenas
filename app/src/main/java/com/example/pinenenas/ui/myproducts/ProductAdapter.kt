// /app/src/main/java/com/example/pinenenas/ui/myproducts/ProductAdapter.kt
package com.example.pinenenas.ui.myproducts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pinenenas.data.model.Product
import com.example.pinenenas.databinding.ItemProductBinding
import java.util.Currency

class ProductAdapter(
    private val listener: OnItemClickListener,
    private val isOwnerView: Boolean = true // Default to true for MyProducts
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    // Interface to handle clicks on the entire item and specific buttons
    interface OnItemClickListener {
        fun onEditClick(product: Product)
        fun onDeleteClick(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Pass the new boolean to the ViewHolder
        return ProductViewHolder(binding, listener, isOwnerView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = getItem(position)
        holder.bind(currentProduct)
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val listener: OnItemClickListener,
        private val isOwnerView: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        // Bind product data to the views in item_product.xml
        fun bind(product: Product) {
            binding.textProductName.text = product.name
            binding.textProductDescription.text = product.description
            binding.textProductQuantity.text = "Stock: ${product.quantity}"

            // Format the price to local currency (e.g., MYR)
            val currency: Currency = Currency.getInstance("MYR")

            binding.textProductPrice.text = buildString {
                append(currency.symbol)
                append(product.price)
            }
            // TODO: Load image using a library like Glide or Coil
            // For now, we'll use a placeholder if you have one.
            // Example with Glide:
            // Glide.with(itemView.context)
            //     .load(product.imageUrl)
            //     .placeholder(R.drawable.ic_placeholder_image) // A default image
            //     .into(binding.imageProduct)

            if (isOwnerView) {
                // If it's the owner's view (MyProducts), show the buttons
                binding.buttonEditProduct.visibility = View.VISIBLE
                binding.buttonDeleteProduct.visibility = View.VISIBLE
            } else {
                // If it's a public view (MyShop), hide the buttons
                binding.buttonEditProduct.visibility = View.GONE
                binding.buttonDeleteProduct.visibility = View.GONE
            }
            // Set up click listeners for the buttons
            binding.buttonEditProduct.setOnClickListener {
                listener.onEditClick(product)
            }

            binding.buttonDeleteProduct.setOnClickListener {
                listener.onDeleteClick(product)
            }
            itemView.setOnClickListener {
                if (!isOwnerView) {
                    // For example, trigger the same action as the edit button for now
                    listener.onEditClick(product)
                }
            }
        }
    }
}

// DiffUtil helps the adapter efficiently update the list
class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
