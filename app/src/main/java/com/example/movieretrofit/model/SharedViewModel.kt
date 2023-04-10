package com.example.movieretrofit.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movieretrofit.data.Food

class SharedViewModel : ViewModel() {
    val data = MutableLiveData<Food>()
}