package com.example.movieretrofit.fragments.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.R
import com.example.movieretrofit.charts.ColumnCharts
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
            if (activity != null) calendar.addDecorators(
                CurrentDayDecorator(requireActivity(), today), CalendarDecorator(nutrientList)
            )
        }

        firebase.getWeeklyNutrients { nutrientList ->
            val columnCharts = ColumnCharts()
            context?.let { it ->
                columnCharts.setColumnChartCalories(
                    it,
                    binding.columnChart,
                    nutrientList.map { nutrients ->
                        nutrients.getBalancedNutrientsInPercentage(
                            firebase.diet
                        )
                    }
                )
            }
        }

        binding.weekButton.setOnClickListener {
            binding.weekButton.setBackgroundResource(R.drawable.add_btn_background)
            binding.monthButton.setBackgroundResource(R.drawable.fill_btn_background_green)
            binding.weekButton.setTextColor(Color.BLACK)
            binding.monthButton.setTextColor(Color.WHITE)
            binding.lCNutrients.visibility = View.VISIBLE
            binding.columnChart.visibility = View.VISIBLE
            //binding.lCCalories.visibility = View.VISIBLE
            binding.calendarView.visibility = View.GONE
            //binding.linLNutHint.visibility = View.VISIBLE
        }

        binding.monthButton.setOnClickListener {
            binding.monthButton.setBackgroundResource(R.drawable.add_btn_background)
            binding.weekButton.setBackgroundResource(R.drawable.fill_btn_background_green)
            binding.weekButton.setTextColor(Color.WHITE)
            binding.monthButton.setTextColor(Color.BLACK)
            binding.lCNutrients.visibility = View.GONE
            binding.columnChart.visibility = View.GONE
            //binding.lCCalories.visibility = View.GONE
            binding.calendarView.visibility = View.VISIBLE
            //binding.linLNutHint.visibility = View.GONE
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = StatisticsFragment()
    }
}
