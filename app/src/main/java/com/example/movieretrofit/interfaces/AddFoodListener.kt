package com.example.movieretrofit.interfaces

import com.example.movieretrofit.data.Nutrients

interface AddFoodListener {
    fun onNutrientsReceived(nutrients: Nutrients)
}