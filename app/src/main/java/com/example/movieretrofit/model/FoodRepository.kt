package com.example.movieretrofit.model

import android.util.Log
import com.example.movieretrofit.data.Food


class FoodRepository {
    suspend fun getRecipes(foodName: String): List<Food>{
        val food = restFoodApi.getFoodRecipe(foodName)
        val foodList = arrayListOf<Food>()

        food.body()!!.hints.forEach { foodList.add(it.food) }

        return foodList

        Log.e("edamam", "food is $food")
        Log.e("edamam", "foodList is $foodList")
    }

   /*suspend fun getRecipeNutrients(id: Int): Nutrition {
       val food = restFoodApi.getRecipeNutrients(id)
       return food.body()!!
   }*/
}