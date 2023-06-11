package com.example.movieretrofit.charts

import android.content.Context
import android.graphics.Color
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Diet
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class PieCharts(private val context: Context, private val pieChart: PieChart) {
    fun setPieChart(diet: Diet) {
        val entries = arrayListOf(
            PieEntry(diet.proteinCf), PieEntry(diet.fatCf), PieEntry(diet.carbsCf)
        )
        val pieDataSet = PieDataSet(entries, "Colors")
        setDataSet(pieDataSet)

        val pieData = PieData(pieDataSet)
        setPieData(pieData)

        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = false
        pieChart.setTouchEnabled(false)
        pieChart.data = pieData
        pieChart.invalidate()
    }

    private fun setDataSet(pieDataSet: PieDataSet) {
        pieDataSet.apply {
            colors = listOf(
                context.getColor(R.color.protein),
                context.getColor(R.color.fat),
                context.getColor(R.color.carb)
            )
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String =
                    String.format("%.0f%%", value)
            }
        }
    }

    private fun setPieData(pieData: PieData) {
        pieData.apply {
            setValueTextColor(Color.WHITE)
            setValueTextSize(12f)
        }
    }
}
