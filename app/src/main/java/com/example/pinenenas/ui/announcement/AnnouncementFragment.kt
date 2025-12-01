package com.example.pinenenas.ui.announcement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pinenenas.R
import com.example.pinenenas.data.model.Announcement
import com.example.pinenenas.databinding.FragmentAnnouncementBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// Implement the adapter's click listener interface
class AnnouncementFragment : Fragment(), AnnouncementAdapter.OnItemClickListener {

    private var _binding: FragmentAnnouncementBinding? = null
    private val binding get() = _binding!!

    private lateinit var announcementViewModel: AnnouncementViewModel
    // The adapter is now initialized in onCreateView
    private lateinit var announcementAdapter: AnnouncementAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        announcementViewModel = ViewModelProvider(this).get(AnnouncementViewModel::class.java)
        _binding = FragmentAnnouncementBinding.inflate(inflater, container, false)

        // Pass 'this' as the listener to the adapter
        announcementAdapter = AnnouncementAdapter(this)

        setupRecyclerView()
        setupFab()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAnnouncements()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewAnnouncements.adapter = announcementAdapter
    }

    private fun setupFab() {
        binding.fabAddAnnouncement.setOnClickListener {
            showCreateAnnouncementDialog()
        }
    }

    private fun observeAnnouncements() {
        announcementViewModel.allAnnouncements.observe(viewLifecycleOwner) { announcements ->
            announcementAdapter.submitList(announcements)
        }
    }

    private fun showCreateAnnouncementDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_announcement, null)
        val contentEditText = dialogView.findViewById<EditText>(R.id.edit_text_announcement_content)
        val priceEditText = dialogView.findViewById<EditText>(R.id.edit_text_announcement_price)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New Announcement")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Post") { _, _ ->
                val content = contentEditText.text.toString().trim()
                val price = priceEditText.text.toString().trim().toDoubleOrNull()
                if (content.isNotEmpty()) {
                    announcementViewModel.postAnnouncement(content, price)
                }
            }
            .show()
    }

    // This is the new method that gets called when an item is clicked
    override fun onItemClick(announcement: Announcement) {
        // Prevent clicking on your own announcement
        if (announcement.authorId == announcementViewModel.getCurrentUserId()) {
            Toast.makeText(context, "This is your own announcement.", Toast.LENGTH_SHORT).show()
            return
        }

        val options = arrayOf("View Profile", "Send Message")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Options for ${announcement.authorDisplayName}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val bundle = Bundle().apply {
                            putLong("userId", announcement.authorId)
                        }
                        findNavController().navigate(R.id.action_nav_announcement_to_userProfileFragment, bundle)
                    }
                    1 -> {
                        Toast.makeText(context, "Messaging feature coming soon!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
