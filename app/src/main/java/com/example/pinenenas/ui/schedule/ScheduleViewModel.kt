// src/main/java/com/example/pinenenas/ui/schedule/ScheduleViewModel.kt

package com.example.pinenenas.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.BarEntry
import kotlin.collections.ArrayList

class ScheduleViewModel : ViewModel() {

    // This LiveData will hold the list of planting entries for the chart.
    private val _plantingEntries = MutableLiveData<List<BarEntry>>()
    val plantingEntries: LiveData<List<BarEntry>> = _plantingEntries

    // This LiveData will hold the list of harvest entries for the chart.
    private val _harvestEntries = MutableLiveData<List<BarEntry>>()
    val harvestEntries: LiveData<List<BarEntry>> = _harvestEntries

    // init block is a good place to load initial data.
    init {
        loadScheduleData()
    }

    /**
     * Fetches the schedule data.
     * In a real app, this would fetch data from a repository (which might get it from a
     * Room database or a remote server).
     */
    private fun loadScheduleData() {
        // --- This is where you would fetch your real user data ---
        // For demonstration, we'll use the same sample data.
        val plantings = ArrayList<BarEntry>()
        plantings.add(BarEntry(0f, 1f)) // Planted in January
        plantings.add(BarEntry(2f, 1f)) // Planted in March

        // --- Data processing logic should be in the ViewModel, not the Fragment ---
        val harvests = ArrayList<BarEntry>()
        // Pineapple harvesting is typically 18-24 months after planting. Let's assume 18.
        val harvestMonth1 = (0 + 18) % 12 // (Jan + 18 months) -> July
        harvests.add(BarEntry(harvestMonth1.toFloat(), 1f))
        val harvestMonth2 = (2 + 18) % 12 // (Mar + 18 months) -> September
        harvests.add(BarEntry(harvestMonth2.toFloat(), 1f))
        // --- End of sample data ---

        // Post the values to LiveData so the UI can observe changes.
        _plantingEntries.value = plantings
        _harvestEntries.value = harvests
    }
}
