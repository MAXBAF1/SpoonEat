package com.example.movieretrofit.charts

import android.graphics.Color
import com.example.movieretrofit.data.Diet
import com.example.movieretrofit.data.Nutrients
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class BarCharts {
    fun setBarChart(barChart: BarChart, nutrients: Nutrients, diet: Diet) {
        val balancedNutrients = nutrients.getBalancedNutrientsInPercentage(diet)

        val entries = arrayListOf(
            (BarEntry(3f, balancedNutrients.protein, "protein")),
            (BarEntry(2f, balancedNutrients.fat, "fat")),
            (BarEntry(1f, balancedNutrients.carb, "carbs"))
        )

        val dataSet = BarDataSet(entries, "")
        dataSet.stackLabels = arrayOf("Белки", "Жиры", "Углеводы")

        dataSet.colors = arrayListOf(
            getColor(entries[0].y),
            getColor(entries[1].y),
            getColor(entries[2].y)
        )

        val data = BarData(dataSet)
        dataSet.valueTextColor = Color.BLACK
        data.barWidth = 0.7f // ширина колонок
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "$value%"
            }
        })

        barChart.animateY(0)

        val xAxis = barChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        val leftAxis = barChart.axisLeft
        leftAxis.setDrawLabels(true)
        leftAxis.axisMaximum = maxOf(
            balancedNutrients.protein,
            balancedNutrients.fat,
            balancedNutrients.carb
        ) + 10 // максимальное значение на оси Y
        leftAxis.axisMinimum = 0f

        val rightAxis = barChart.axisRight
        rightAxis.setDrawLabels(true)
        rightAxis.axisMaximum = maxOf(
            balancedNutrients.protein,
            balancedNutrients.fat,
            balancedNutrients.carb
        ) + 3 // максимальное значение на оси Y
        rightAxis.axisMinimum = 0f

        barChart.legend.isEnabled = false
        barChart.description.isEnabled = false
        barChart.invalidate()
        xAxis.apply {
            setDrawAxisLine(false)
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            labelRotationAngle = -90f
            setDrawLabels(true)
        }
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value) {
                    3f -> "Белки"
                    2f -> "Жиры"
                    1f -> "Углеводы"
                    else -> ""
                }
            }
        }

        barChart.data = data
        barChart.setFitBars(true)
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)
    }

    private fun getColor(value: Float): Int {
        return when (value) {
            in 90F..110F -> Color.GREEN
            in 80F..120F -> Color.YELLOW
            else -> Color.RED
        }
    }
}