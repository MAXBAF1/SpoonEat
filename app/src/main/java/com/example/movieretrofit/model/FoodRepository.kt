package com.example.movieretrofit.model

import com.example.movieretrofit.data.Food

class FoodRepository {
    suspend fun getRecipes(foodName: String): List<Food> {
        val food = restFoodApi.getFoodRecipe(foodName)
        val foodList = arrayListOf<Food>()

        food.body()?.hints?.filter { it.food.image != "" }?.forEach {
            foodList.add(it.food)
        }

        return foodList
    }
}