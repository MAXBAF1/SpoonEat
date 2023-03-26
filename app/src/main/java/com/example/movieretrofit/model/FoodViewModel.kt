package com.example.movieretrofit.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieretrofit.data.Food
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: FoodRepository = FoodRepository()

    suspend fun getFoodRecipe(foodName: String): List<Food> {
        viewModelScope.launch(Dispatchers.IO) { }
        return repository.getFoodRecipe(foodName)
    }
}