package com.example.movieretrofit.charts.calendar

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.data.Nutrients
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*

class CalendarDecorator(private var nutrientList: List<Nutrients>) : DayViewDecorator {
    private var firebase: Firebase = Firebase()

    private var calendar = Calendar.getInstance()
    private val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(calendar)
        return day?.day!! >= 1
                && day.month == Calendar.getInstance().get(Calendar.MONTH)
                && day.year == Calendar.getInstance().get(Calendar.YEAR)
                && day.day < today
    }

    private fun getColor(callback: (color: Int) -> Unit) {
        firebase.getUserDietFromFirebase { diet ->
            val currCf = nutrientList.last().getBalancedNutrientsInPercentage(diet)

            val color = when {
                isInBounds(currCf, 90F..110F) -> Color.GREEN
                isInBounds(currCf, 80F..120F) -> Color.YELLOW
                else -> Color.RED
            }
            callback(color)
        }
    }

    private fun isInBounds(nutrients: Nutrients, bounds: ClosedRange<Float>): Boolean {
        if (nutrients.fat in bounds && nutrients.carb in bounds && nutrients.protein in bounds) return true
        return false
    }

    override fun decorate(view: DayViewFacade?) {
        getColor {
            Log.e("item", "CalendarDecorator, fun decorate, color is " + it.toString())
            view?.addSpan(object : ForegroundColorSpan(it) {})
        }
    }
}
