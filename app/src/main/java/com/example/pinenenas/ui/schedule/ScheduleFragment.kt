package com.example.pinenenas.ui.schedule

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.pinenenas.R
import com.example.pinenenas.databinding.FragmentScheduleBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    // Initialize the ViewModel using the KTX delegate
    private val scheduleViewModel: ScheduleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
        observeViewModel()
    }

    private fun setupChart() {
        binding.scheduleChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            isHighlightFullBarEnabled = false

            // Configure X-Axis (Months)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.valueFormatter = IndexAxisValueFormatter(
                arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            )

            // Configure Y-Axis
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false

            // Configure Legend
            legend.isWordWrapEnabled = true
        }
    }

    private fun observeViewModel() {
        // Observe planting data
        scheduleViewModel.plantingEntries.observe(viewLifecycleOwner) { plantingEntries ->
            val harvestEntries = scheduleViewModel.harvestEntries.value
            if (harvestEntries != null) {
                updateChartData(plantingEntries, harvestEntries)
            }
        }

        // Observe harvest data
        scheduleViewModel.harvestEntries.observe(viewLifecycleOwner) { harvestEntries ->
            val plantingEntries = scheduleViewModel.plantingEntries.value
            if (plantingEntries != null) {
                updateChartData(plantingEntries, harvestEntries)
            }
        }
    }

    private fun updateChartData(plantingEntries: List<BarEntry>, harvestEntries: List<BarEntry>) {
        val plantingColor = ContextCompat.getColor(requireContext(), R.color.purple_500)
        val harvestColor = ContextCompat.getColor(requireContext(), R.color.teal_200)

        val plantingDataSet = BarDataSet(plantingEntries, "Planted").apply { color = plantingColor }
        val harvestDataSet = BarDataSet(harvestEntries, "Harvest").apply { color = harvestColor }

        val barData = BarData(plantingDataSet, harvestDataSet)
        barData.barWidth = 0.40f // Set width for each bar

        binding.scheduleChart.data = barData

        // Group the bars for planting and harvesting for each month
        val groupSpace = 0.08f
        val barSpace = 0.06f
        binding.scheduleChart.xAxis.axisMinimum = 0f
        // Calculate the correct maximum for the X-axis to show all groups
        binding.scheduleChart.xAxis.axisMaximum = barData.getGroupWidth(groupSpace, barSpace) * 12
        binding.scheduleChart.groupBars(0f, groupSpace, barSpace)

        binding.scheduleChart.invalidate() // Refresh the chart
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
