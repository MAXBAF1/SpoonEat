package com.example.movieretrofit.charts

import android.content.Context
import android.graphics.Color
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Nutrients
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*

class LineCharts {
    private val curDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    fun setLineChartNutrients(
        context: Context,
        lineChart: LineChart,
        nutrientList: List<Nutrients>
    ) {
        val entriesProtein = mutableListOf<Entry>()
        val entriesFat = mutableListOf<Entry>()
        val entriesCarbs = mutableListOf<Entry>()

        val startDay = curDay - (nutrientList.size - 1)
        nutrientList.forEachIndexed { index, value ->
            entriesProtein.add(Entry(startDay + index.toFloat(), value.protein))
            entriesFat.add(Entry(startDay + index.toFloat(), value.fat))
            entriesCarbs.add(Entry(startDay + index.toFloat(), value.carb))
        }

        val simpleMode = nutrientList.size > 7
        val dataSetProtein = LineDataSet(entriesProtein, "Белки")
        val proteinColor = context.getColor(R.color.protein)
        settingsDataSet(dataSetProtein, proteinColor, proteinColor, simpleMode)
        val dataSetFat = LineDataSet(entriesFat, "Жиры")
        val fatColor = context.getColor(R.color.fat)
        settingsDataSet(dataSetFat, fatColor, fatColor, simpleMode)
        val dataSetCarbs = LineDataSet(entriesCarbs, "Углеводы")
        val carbColor = context.getColor(R.color.carb)
        settingsDataSet(dataSetCarbs, carbColor, carbColor, simpleMode)

        val lineData = LineData(dataSetProtein, dataSetFat, dataSetCarbs)
        settingsLineChart(lineChart, lineData, startDay, nutrientList.size - 1)
    }

    private fun settingsLineChart(
        lineChart: LineChart,
        lineData: LineData,
        startDay: Int,
        daysCnt: Int
    ) {
        //lineChart.axisLeft.setDrawLabels(false)
        lineChart.isDoubleTapToZoomEnabled = false // Предотвращение растяжения пользователем
        lineChart.setScaleEnabled(false) // Предотвращение растяжения пользователем
        lineChart.axisRight.setDrawLabels(false)
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.axisMaximum = startDay + daysCnt + 0.5f

        lineChart.legend.isEnabled = false // Легенда
        lineChart.description.isEnabled = false

        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun settingsDataSet(
        dataSet: LineDataSet,
        color: Int,
        pointColor: Int,
        simpleMode: Boolean
    ) {
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.1f // настройка кривизны
        dataSet.color = color
        dataSet.lineWidth = 3f
        if (simpleMode) {
            dataSet.setCircleColor(Color.TRANSPARENT)
            dataSet.circleHoleColor = Color.TRANSPARENT
            dataSet.circleRadius = 0f
            dataSet.setDrawValues(false)
        } else {
            dataSet.setCircleColor(pointColor)
            dataSet.circleRadius = 5f
            dataSet.valueTextSize = 5f
        }
    }
}