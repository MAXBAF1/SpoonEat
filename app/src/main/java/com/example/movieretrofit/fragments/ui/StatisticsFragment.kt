package com.example.movieretrofit.fragments.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.charts.LineCharts
import com.example.movieretrofit.charts.calendar.CalendarDecorator
import com.example.movieretrofit.charts.calendar.CurrentDayDecorator
import com.example.movieretrofit.databinding.FragmentStatisticsBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class StatisticsFragment : Fragment() {
    lateinit var binding: FragmentStatisticsBinding
    lateinit var firebase: Firebase
    lateinit var calendar: MaterialCalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebase = Firebase()
        val charts = LineCharts()
        //firebase.getDayFoodFromFirebase{ setData() }
        firebase.getWeeklyNutrients { nutrientList ->
            charts.setLineChartCalories(binding.lCCalories, nutrientList)
            context?.let { charts.setLineChartNutrients(it, binding.lCNutrients, nutrientList) }

            //createCalendar(binding.calendarView, nutrientList, requireContext())
            calendar = binding.calendarView
            val today = CalendarDay.today()
            if (activity != null)
                calendar.addDecorators(
                    CurrentDayDecorator(requireActivity(), today),
                    CalendarDecorator(nutrientList)
                )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = StatisticsFragment()
    }
}

