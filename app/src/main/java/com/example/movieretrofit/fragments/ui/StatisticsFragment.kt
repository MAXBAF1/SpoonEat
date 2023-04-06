package com.example.movieretrofit.fragments.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.movieretrofit.R
import com.example.movieretrofit.databinding.FragmentStatisticsBinding
import com.example.movieretrofit.databinding.FragmentVoiceBinding

class StatisticsFragment : Fragment() {
    lateinit var binding: FragmentStatisticsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {}

    companion object {
        @JvmStatic
        fun newInstance() = StatisticsFragment()
    }
}