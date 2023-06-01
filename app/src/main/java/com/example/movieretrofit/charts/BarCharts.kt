package com.example.movieretrofit.charts

import android.content.Context
import android.graphics.Color
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Diet
import com.example.movieretrofit.data.Nutrients
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt


class BarCharts {
    fun setBarChart(context: Context, barChart: BarChart, nutrients: Nutrients, diet: Diet) {
        barChart.setNoDataText("Съешь что-нибудь)")
        barChart.isDoubleTapToZoomEnabled = false
        barChart.setScaleEnabled(false)
        val balancedNutrients = nutrients.getBalancedNutrientsInPercentage(diet)

        val entries = arrayListOf(
            (BarEntry(3f, balancedNutrients.protein, "protein")),
            (BarEntry(2f, balancedNutrients.fat, "fat")),
            (BarEntry(1f, balancedNutrients.carb, "carbs"))
        )

        val dataSet = BarDataSet(entries, "")
        dataSet.stackLabels = arrayOf("Белки", "Жиры", "Углеводы")

        dataSet.colors = listOf(
            context.getColor(R.color.protein),
            context.getColor(R.color.fat),
            context.getColor(R.color.carb)
        )

        val data = BarData(dataSet)
        dataSet.valueTextColor = Color.BLACK
        data.barWidth = 0.8f // ширина колонок
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.roundToInt()}%"
            }
        })

        barChart.animateY(0)

        val xAxis = barChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        val leftAxis = barChart.axisLeft
        leftAxis.setDrawLabels(false)
        leftAxis.axisMaximum = maxOf(
            balancedNutrients.protein,
            balancedNutrients.fat,
            balancedNutrients.carb
        ) + 4 // максимальное значение на оси Y
        leftAxis.axisMinimum = 0f

        val rightAxis = barChart.axisRight
        rightAxis.setDrawLabels(true)
        rightAxis.axisMaximum = maxOf(
            balancedNutrients.protein,
            balancedNutrients.fat,
            balancedNutrients.carb
        ) + 4 // максимальное значение на оси Y
        rightAxis.axisMinimum = 0f
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
        }.apply {
            xAxis.setLabelCount(3, false)
        }

        barChart.data = data
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(false)
        rightAxis.setDrawGridLines(false)

        rightAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.roundToInt()}%"
            }
        }

        val ll1 = LimitLine(100f)
        ll1.lineColor = context.getColor(R.color.red)
        ll1.lineWidth = 3f
        //ll1.labelPosition = LimitLabelPosition.RIGHT_TOP

        leftAxis.removeAllLimitLines()
        leftAxis.addLimitLine(ll1)
    }
}