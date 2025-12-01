package com.example.pinenenas.ui.announcement

import android.icu.util.Currency
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pinenenas.data.model.Announcement
import com.example.pinenenas.databinding.ItemAnnouncementBinding
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

// Add a listener parameter to the adapter's constructor
class AnnouncementAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Announcement, AnnouncementAdapter.AnnouncementViewHolder>(AnnouncementDiffCallback()) {

    // Define an interface for click events
    interface OnItemClickListener {
        fun onItemClick(announcement: Announcement)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val binding = ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnnouncementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val currentItem = getItem(position)
        // Pass the item and listener to the ViewHolder
        holder.bind(currentItem, listener)
    }

    class AnnouncementViewHolder(private val binding: ItemAnnouncementBinding) : RecyclerView.ViewHolder(binding.root) {
        // Update the bind method to accept the listener
        fun bind(announcement: Announcement, listener: OnItemClickListener) {
            binding.textAuthorName.text = announcement.authorDisplayName
            binding.textContent.text = announcement.content
            binding.textTimestamp.text = getFormattedTimestamp(announcement.timestamp)

            if (announcement.price != null) {
                val currency: Currency = Currency.getInstance("MYR")
                binding.textPrice.text = buildString {
                    append(currency.symbol)
                    append(announcement.price)
                }
            } else {
                binding.textPrice.visibility = View.GONE
            }

            // Set the click listener on the root view of the item
            itemView.setOnClickListener {
                listener.onItemClick(announcement)
            }
        }

        private fun getFormattedTimestamp(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            val days = TimeUnit.MILLISECONDS.toDays(diff)

            return when {
                seconds < 60 -> "Just now"
                minutes < 60 -> "$minutes min ago"
                hours < 24 -> "$hours h ago"
                else -> "$days d ago"
            }
        }
    }
}

class AnnouncementDiffCallback : DiffUtil.ItemCallback<Announcement>() {
    override fun areItemsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
        return oldItem == newItem
    }
}
