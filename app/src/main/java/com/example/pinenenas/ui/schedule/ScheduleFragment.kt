// In C:/Users/BOSS/Desktop/CS/utem/y3sem1/BITP3223_SOFTWARE_PROJECT_MANAGEMENT/Project/app/src/main/java/com/example/pinenenas/ui/schedule/ScheduleFragment.kt
package com.example.pinenenas.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Import viewModels
import com.example.pinenenas.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ScheduleFragment : Fragment() {

    // Initialize the ViewModel using the by viewModels() KTX delegate
    private val scheduleViewModel: ScheduleViewModel by viewModels()
    private lateinit var chart: BarChart // Make chart a class-level property

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_schedule, container, false)
        chart = root.findViewById(R.id.schedule_chart)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChart()
        observeViewModel()
    }

    private fun setupChart() {
        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)

        // Setup X-Axis (Months)
        val xAxis = chart.xAxis
        val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        xAxis.valueFormatter = IndexAxisValueFormatter(months)
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.setLabelCount(12, false)
        xAxis.granularity = 1f
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 12f

        // Setup Y-Axis
        chart.axisLeft.axisMinimum = 0f
        chart.axisRight.isEnabled = false
    }

    private fun observeViewModel() {
        // Observe both planting and harvesting LiveData
        scheduleViewModel.plantingEntries.observe(viewLifecycleOwner) { plantingEntries ->
            val harvestEntries = scheduleViewModel.harvestEntries.value
            if (harvestEntries != null) {
                updateChartData(plantingEntries, harvestEntries)
            }
        }

        scheduleViewModel.harvestEntries.observe(viewLifecycleOwner) { harvestEntries ->
            val plantingEntries = scheduleViewModel.plantingEntries.value
            if (plantingEntries != null) {
                updateChartData(plantingEntries, harvestEntries)
            }
        }
    }

    private fun updateChartData(plantingEntries: List<BarEntry>, harvestEntries: List<BarEntry>) {
        val plantingDataSet = BarDataSet(plantingEntries, "Planted")
        // It's better to use ContextCompat.getColor to resolve colors properly
        plantingDataSet.color = requireContext().getColor(R.color.purple_500)

        val harvestDataSet = BarDataSet(harvestEntries, "Harvest")
        harvestDataSet.color = requireContext().getColor(R.color.teal_200)

        val barData = BarData(plantingDataSet, harvestDataSet)
        barData.barWidth = 0.4f

        chart.data = barData
        // Group the bars for the same month together.
        // The fromX parameter (first) should be the start of your axis.
        chart.groupBars(0f, 0.08f, 0.06f)
        chart.invalidate() // refresh chart
    }
}
