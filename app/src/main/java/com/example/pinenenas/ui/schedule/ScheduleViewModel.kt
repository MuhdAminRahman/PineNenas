package com.example.pinenenas.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.BarEntry

class ScheduleViewModel : ViewModel() {

    private val _plantingEntries = MutableLiveData<List<BarEntry>>()
    val plantingEntries: LiveData<List<BarEntry>> = _plantingEntries

    private val _harvestEntries = MutableLiveData<List<BarEntry>>()
    val harvestEntries: LiveData<List<BarEntry>> = _harvestEntries

    init {
        loadScheduleData()
    }

    /**
     * Creates dummy data for planting and harvesting schedules.
     * In a real app, this data would come from a repository.
     */
    private fun loadScheduleData() {
        // Dummy data for quantities planted each month (Jan=0, Feb=1, ...)
        val monthlyPlantings = floatArrayOf(10f, 15f, 12f, 18f, 25f, 20f, 15f, 10f, 22f, 30f, 18f, 12f)

        val plantings = ArrayList<BarEntry>()
        monthlyPlantings.forEachIndexed { index, quantity ->
            plantings.add(BarEntry(index.toFloat(), quantity))
        }

        // Logic to calculate harvests.
        // Assuming a pineapple harvest cycle of 18 months after planting.
        val harvests = ArrayList<BarEntry>()
        monthlyPlantings.forEachIndexed { plantingMonth, plantedQuantity ->
            // The harvest will be 18 months later. The modulo finds the correct month in the year.
            val harvestMonth = (plantingMonth + 18) % 12
            harvests.add(BarEntry(harvestMonth.toFloat(), plantedQuantity))
        }

        // Post the data to LiveData so the Fragment can observe it.
        _plantingEntries.value = plantings
        // We group the harvests by month to show a total.
        _harvestEntries.value = groupHarvestsByMonth(harvests)
    }

    /**
     * Groups multiple harvest entries for the same month into a single entry.
     */
    private fun groupHarvestsByMonth(entries: List<BarEntry>): List<BarEntry> {
        return entries
            .groupBy { it.x } // Group by month (the x-value)
            .map { (month, monthlyEntries) ->
                // For each month, sum up the quantities (the y-values)
                BarEntry(month, monthlyEntries.sumOf { it.y.toDouble() }.toFloat())
            }
    }
}
