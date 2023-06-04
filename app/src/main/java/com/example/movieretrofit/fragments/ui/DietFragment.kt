package com.example.movieretrofit.fragments.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.R
import com.example.movieretrofit.charts.PieCharts
import com.example.movieretrofit.data.Diet
import com.example.movieretrofit.databinding.FragmentDietBinding
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.slider.RangeSlider

class DietFragment : Fragment() {
    private lateinit var binding: FragmentDietBinding
    lateinit var firebase: Firebase

    private var diet: Diet = Diet()
    lateinit var rangeSlider: RangeSlider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDietBinding.inflate(inflater, container, false)
        firebase = Firebase()

        chooseDiet()
        setPieCharts()

        return binding.root
    }

    private fun setPieCharts() {
        val standardEntries = arrayListOf(PieEntry(30f), PieEntry(30f), PieEntry(40f))
        context?.let { PieCharts(it, binding.dietStandardChart).setPieChart(standardEntries) }

        val ketoEntries = arrayListOf(PieEntry(20f), PieEntry(75f), PieEntry(5f))
        context?.let { PieCharts(it, binding.dietKetoChart).setPieChart(ketoEntries) }

        val proteinEntries = arrayListOf(PieEntry(50f), PieEntry(20f), PieEntry(30f))
        context?.let { PieCharts(it, binding.dietProteinChart).setPieChart(proteinEntries) }
    }

    private fun chooseDiet() {
        binding.dietStandart.setOnClickListener { updateDiet(Diet(30, 30, 40)) }
        binding.dietKeto.setOnClickListener { updateDiet(Diet(20, 75, 5)) }
        binding.dietProtein.setOnClickListener { updateDiet(Diet(50, 20, 30)) }
    }

    private fun updateDiet(diet: Diet) {
        rangeSlider.values = listOf(diet.proteinCf.toFloat(), diet.proteinCf.toFloat() + diet.fatCf)
        updateTvCf(diet)
    }

    private fun createSaveDiet() {
        binding.sendDietToFirebase.setOnClickListener {
            Log.e("item", "createSaveDiet in AccountSettingsFragment, fatCf is  ${diet.fatCf}")
            if (diet.proteinCf != 0 && diet.fatCf != 0 && diet.carbsCf != 0) {
                firebase.sendUserDietToFirebase(diet)
                Toast.makeText(requireContext(), "Сохранено", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(requireContext(), "Не может быть 0%!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTvCf(diet: Diet) {
        val proteinText = "${getString(R.string.protein)} ${diet.proteinCf}%"
        val fatText = "${getString(R.string.fat)} ${diet.fatCf}%"
        val carbText = "${getString(R.string.carb)} ${diet.carbsCf}%"
        binding.tvProtein.text = proteinText
        binding.tvFat.text = fatText
        binding.tvCarb.text = carbText
    }

    companion object {
        @JvmStatic
        fun newInstance() = DietFragment()
    }
}