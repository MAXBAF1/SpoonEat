package com.example.movieretrofit.charts.calendar

import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.R
import com.example.movieretrofit.charts.BarCharts
import com.example.movieretrofit.data.Nutrients
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CurrentDayDecorator (context: Activity?, today: CalendarDay): DayViewDecorator {
    private val drawable : Drawable?
    var myDay = today


    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == myDay
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable!!)
    }

    init {
        drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_round_circle)
    }
}