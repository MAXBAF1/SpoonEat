package com.example.movieretrofit.model

import com.example.movieretrofit.data.Food
import com.example.movieretrofit.data.Nutrition


class FoodRepository {
    suspend fun getRecipes(foodName: String): List<Food>{
        val food = restFoodApi.getAllFood(foodName)
        val foodList = food.body()!!.searchResults[0].results
        return foodList
    }

    suspend fun getRecipeNutrients(id: Int): Nutrition{
        val food = restFoodApi.getRecipeNutrients(id)
        return food.body()!!
    }
}