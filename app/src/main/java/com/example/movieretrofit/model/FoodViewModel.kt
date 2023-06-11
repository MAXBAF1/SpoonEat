package com.example.movieretrofit.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.movieretrofit.data.Food

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: FoodRepository = FoodRepository()

    suspend fun getFoods(foodName: String): List<Food> {
        return repository.getRecipes(foodName)
    }
}