package com.example.movieretrofit.fragments.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.databinding.FragmentSearchBinding
import com.example.movieretrofit.model.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun handleNutrientsData(nutrients: Food) {
        val viewModel: SharedViewModel by activityViewModels()
        viewModel.data.value = nutrients

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bNav)
        bottomNav.selectedItemId = R.id.home
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}