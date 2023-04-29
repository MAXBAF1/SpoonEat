package com.example.movieretrofit.model

import com.example.movieretrofit.data.Food


class FoodRepository {
    suspend fun getFoodRecipe(foodName: String): List<Food>{
        val food = restFoodApi.getAllFood(foodName)
        val foodList = food.body()!!.searchResults[0].results
        return foodList
    }
}