package com.example.movieretrofit.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.data.Nutrition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: FoodRepository = FoodRepository()

    suspend fun getRecipes(foodName: String): List<Food> {
        viewModelScope.launch(Dispatchers.IO) { }
        return repository.getRecipes(foodName)
    }

    suspend fun getRecipeNutrients(id: Int): Nutrition {
        viewModelScope.launch(Dispatchers.IO) { }
        return repository.getRecipeNutrients(id)
    }
}