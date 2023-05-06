package com.example.movieretrofit.interfaces

import com.example.movieretrofit.data.Food

interface AddFoodListener {
    fun onFoodReceived(food: Food)
}