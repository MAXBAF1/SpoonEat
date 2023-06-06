package com.example.movieretrofit.charts

import android.content.Context
import android.graphics.Color
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Nutrients
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.util.*
import kotlin.math.roundToInt

class ColumnCharts {
    private val startWeek = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 6

    fun setColumnChartCalories(
        context: Context, barChart: BarChart, nutrientList: List<Nutrients>
    ) {
        val entriesProtein = mutableListOf<BarEntry>()
        val entriesFat = mutableListOf<BarEntry>()
        val entriesCarbs = mutableListOf<BarEntry>()

        nutrientList.forEachIndexed { index, value ->
            entriesProtein.add(BarEntry(startWeek + index.toFloat(), value.protein))
            entriesFat.add(BarEntry(startWeek + index.toFloat(), value.fat))
            entriesCarbs.add(BarEntry(startWeek + index.toFloat(), value.carb))
        }

        val dataSetProtein = BarDataSet(entriesProtein, "Белки")
        val proteinColor = context.getColor(R.color.protein)
        settingsDataSet(dataSetProtein, proteinColor)

        val dataSetFat = BarDataSet(entriesFat, "Жиры")
        val fatColor = context.getColor(R.color.fat)
        settingsDataSet(dataSetFat, fatColor)

        val dataSetCarbs = BarDataSet(entriesCarbs, "Углеводы")
        val carbColor = context.getColor(R.color.carb)
        settingsDataSet(dataSetCarbs, carbColor)

        val groupSpace = 0.5f
        val barSpace = 0.0f
        val barWidth = 0.15f

        val barData = BarData(arrayListOf<IBarDataSet>().apply {
            if (nutrientList.any { it.protein > 0 && it.fat > 0 && it.carb > 0 }) {
                add(dataSetProtein)
                add(dataSetFat)
                add(dataSetCarbs)
            } else {
                dataSetProtein.color = Color.TRANSPARENT
                dataSetFat.color = Color.TRANSPARENT
                dataSetCarbs.color = Color.TRANSPARENT
                add(dataSetProtein)
                add(dataSetFat)
                add(dataSetCarbs)
            }
        })

        barData.barWidth = barWidth
        barChart.data = barData

        barChart.setFitBars(true)
        barChart.invalidate()

        val xAxis = barChart.xAxis
        xAxis.axisMinimum = startWeek - 0.5f
        xAxis.axisMaximum = startWeek + 6 + 0.5f
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        barChart.axisRight.setDrawGridLines(false)
        barChart.axisRight.setDrawLabels(false)

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (!value.isNaN()) "${value.roundToInt()}%" else ""
            }
        }

        val ll1 = LimitLine(100f)
        ll1.lineColor = context.getColor(R.color.green)
        ll1.lineWidth = 3f
        barChart.axisLeft.removeAllLimitLines()
        barChart.axisLeft.addLimitLine(ll1)

        barChart.groupBars(startWeek.toFloat(), groupSpace, barSpace)
        barChart.isDoubleTapToZoomEnabled = false // Предотвращение растяжения пользователем
        barChart.setScaleEnabled(false) // Предотвращение растяжения пользователем
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false // Легенда
        barChart.isDoubleTapToZoomEnabled = false
    }

    private fun settingsDataSet(dataSet: BarDataSet, color: Int) {
        dataSet.color = color
        dataSet.valueTextColor = color
        dataSet.valueTextSize = 0f
    }
}