package com.example.pinenenas.ui.marketplace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pinenenas.data.model.UserDetail
import com.example.pinenenas.databinding.ItemShopBinding

class MarketplaceAdapter(private val listener: OnShopClickListener) :
    ListAdapter<UserDetail, MarketplaceAdapter.ShopViewHolder>(ShopDiffCallback()) {

    interface OnShopClickListener {
        fun onShopClick(userDetail: UserDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val binding = ItemShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ShopViewHolder(
        private val binding: ItemShopBinding,
        private val listener: OnShopClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(userDetail: UserDetail) {
            binding.textShopName.text = userDetail.shopName
            binding.textShopDescription.text = userDetail.shopDescription

            itemView.setOnClickListener {
                listener.onShopClick(userDetail)
            }
        }
    }

    class ShopDiffCallback : DiffUtil.ItemCallback<UserDetail>() {
        override fun areItemsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
            return oldItem == newItem
        }
    }
}
