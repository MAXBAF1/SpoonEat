package com.example.movieretrofit.charts

import android.content.Context
import android.graphics.Color
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Nutrients
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*

class LineCharts {
    private val startWeek = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 6

    fun setLineChartCalories(lineChart: LineChart, nutrientList: List<Nutrients>) {
        val entriesCalories = mutableListOf<Entry>()

        nutrientList.forEachIndexed { index, value ->
            entriesCalories.add(Entry(startWeek + index.toFloat(), value.calories))
        }

        val dataSetCalories = LineDataSet(entriesCalories, "Калории")
        settingsDataSet(dataSetCalories, Color.MAGENTA, Color.MAGENTA)

        val lineData = LineData(dataSetCalories)
        settingsLineChart(lineChart, lineData, startWeek)
    }

    fun setLineChartNutrients(
        context: Context,
        lineChart: LineChart,
        nutrientList: List<Nutrients>
    ) {
        val entriesProtein = mutableListOf<Entry>()
        val entriesFat = mutableListOf<Entry>()
        val entriesCarbs = mutableListOf<Entry>()

        nutrientList.forEachIndexed { index, value ->
            entriesProtein.add(Entry(startWeek + index.toFloat(), value.protein))
            entriesFat.add(Entry(startWeek + index.toFloat(), value.fat))
            entriesCarbs.add(Entry(startWeek + index.toFloat(), value.carb))
        }

        val dataSetProtein = LineDataSet(entriesProtein, "Белки")
        val proteinColor = context.getColor(R.color.protein)
        settingsDataSet(dataSetProtein, proteinColor, proteinColor)
        val dataSetFat = LineDataSet(entriesFat, "Жиры")
        val fatColor = context.getColor(R.color.fat)
        settingsDataSet(dataSetFat, fatColor, fatColor)
        val dataSetCarbs = LineDataSet(entriesCarbs, "Углеводы")
        val carbColor = context.getColor(R.color.carb)
        settingsDataSet(dataSetCarbs, carbColor,carbColor)

        val lineData = LineData(dataSetProtein, dataSetFat, dataSetCarbs)
        settingsLineChart(lineChart, lineData, startWeek)
    }

    private fun settingsLineChart(lineChart: LineChart, lineData: LineData, startWeek: Int) {
        //lineChart.axisLeft.setDrawLabels(false)
        lineChart.isDoubleTapToZoomEnabled = false
        lineChart.setScaleEnabled(false)
        lineChart.axisRight.setDrawLabels(false)
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.xAxis.axisMaximum = startWeek + 6 + 0.5f

        lineChart.legend.isEnabled = true
        lineChart.description.isEnabled = false

        lineChart.isDoubleTapToZoomEnabled = false

        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun settingsDataSet(dataset: LineDataSet, color: Int, pointColor: Int) {
        dataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataset.cubicIntensity = 0.1f // настройка кривизны
        dataset.color = color
        dataset.setCircleColor(pointColor)
        dataset.lineWidth = 3f
        dataset.circleRadius = 5f
        dataset.setDrawValues(true)
        dataset.valueTextSize = 5f
    }
}