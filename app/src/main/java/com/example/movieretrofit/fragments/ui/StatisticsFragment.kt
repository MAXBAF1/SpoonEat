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
import com.example.movieretrofit.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {
    lateinit var binding: FragmentStatisticsBinding
    lateinit var firebase: Firebase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebase = Firebase()
        val lineCharts = LineCharts()
        val columnCharts = ColumnCharts()
        firebase.getWeeklyNutrients { weeklyNutrients ->
            context?.let {
                lineCharts.setLineChartNutrients(it, binding.lCNutrientsWeek, weeklyNutrients)
            }
            context?.let {
                columnCharts.setColumnChartCalories(it,
                    binding.columnChart,
                    weeklyNutrients.map { nutrients ->
                        nutrients.getBalancedNutrientsInPercentage(firebase.diet)
                    }
                )
            }
        }

        firebase.getMonthNutrients { nutrientList ->
            //val charts = LineCharts()
            context?.let {
                lineCharts.setLineChartNutrients(
                    it, binding.lCNutrientsMonth, nutrientList
                )
            }
        }

        binding.weekButton.setOnClickListener {
            binding.weekButton.setBackgroundResource(R.drawable.add_btn_background)
            binding.monthButton.setBackgroundResource(R.drawable.fill_btn_background_green)
            binding.weekButton.setTextColor(Color.BLACK)
            binding.monthButton.setTextColor(Color.WHITE)
            binding.lCNutrientsWeek.visibility = View.VISIBLE
            binding.columnChart.visibility = View.VISIBLE
            binding.lCNutrientsMonth.visibility = View.GONE
            binding.tvLinLay.visibility = View.GONE
        }

        binding.monthButton.setOnClickListener {
            binding.monthButton.setBackgroundResource(R.drawable.add_btn_background)
            binding.weekButton.setBackgroundResource(R.drawable.fill_btn_background_green)
            binding.weekButton.setTextColor(Color.WHITE)
            binding.monthButton.setTextColor(Color.BLACK)
            binding.lCNutrientsWeek.visibility = View.GONE
            binding.columnChart.visibility = View.GONE
            binding.lCNutrientsMonth.visibility = View.VISIBLE
            binding.tvLinLay.visibility = View.VISIBLE

            setBalanceTv()

        }
    }

    private fun setBalanceTv() {
        firebase.getDayBalanceCnt {
            val balanceCntText = "Дни в балансе: $it"
            binding.balanceCntTv.text = balanceCntText
        }

        firebase.getMaxBalanceCnt {
            val maxBalanceCntText = "Наибольшая серия дней: $it"
            binding.maxBalanceCntTv.text = maxBalanceCntText
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = StatisticsFragment()
    }
}