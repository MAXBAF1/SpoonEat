package com.example.movieretrofit.charts.calendar

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import com.example.movieretrofit.data.Nutrients
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*

class CalendarDecorator(private var nutrientList: List<Nutrients>) : DayViewDecorator {
    private var calendar = Calendar.getInstance()
    private val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(calendar)
        return day?.day!! >= 1 && day.month == Calendar.getInstance()
            .get(Calendar.MONTH) && day.year == Calendar.getInstance()
            .get(Calendar.YEAR) && day.day < today
    }

    private fun getColor(nutrients: Nutrients): Int {
        return when {
            isInBounds(nutrients, 90F..110F) -> Color.GREEN
            isInBounds(nutrients, 80F..120F) -> Color.YELLOW
            else -> Color.RED
        }
    }

    private fun isInBounds(nutrients: Nutrients, bounds: ClosedRange<Float>): Boolean {
        if (nutrients.fat in bounds && nutrients.carb in bounds && nutrients.protein in bounds)
            return true
        return false
    }

    override fun decorate(view: DayViewFacade?) {

        view?.addSpan(object : ForegroundColorSpan(getColor(nutrientList.last())) {})

    }
}
