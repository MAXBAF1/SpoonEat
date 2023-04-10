package com.example.movieretrofit.fragments.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.movieretrofit.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {
    lateinit var binding: FragmentStatisticsBinding
    lateinit var firebase: com.example.movieretrofit.Firebase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebase = com.example.movieretrofit.Firebase()
        firebase.getCurrentDayFoodDataFromFirebase{ setData() }

    }

    private fun setData() {
    }

    companion object {
        @JvmStatic
        fun newInstance() = StatisticsFragment()
    }
}