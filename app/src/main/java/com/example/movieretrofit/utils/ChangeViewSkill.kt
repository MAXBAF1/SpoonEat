package com.example.movieretrofit.utils

import android.content.Context
import android.util.Log
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.data.FoodList
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.model.restFoodApi
import com.justai.aimybox.Aimybox
import com.justai.aimybox.api.aimybox.AimyboxRequest
import com.justai.aimybox.api.aimybox.AimyboxResponse
import com.justai.aimybox.core.CustomSkill
import com.justai.aimybox.model.Response

class ChangeViewSkill(private val context: Context) : CustomSkill<AimyboxRequest, AimyboxResponse> {

    lateinit var queryRequest: String
    private lateinit var firebase: Firebase

    override fun canHandle(response: AimyboxResponse) = response.action == "changeView"

    override suspend fun onRequest(request: AimyboxRequest, aimybox: Aimybox): AimyboxRequest {
        queryRequest = request.query
        Log.e("aimybox", "request query is $queryRequest")

        val foodApiService = restFoodApi
        val allFood = foodApiService.getFoodRecipe(queryRequest)

        val words = queryRequest.split(" ")

        if ("calories" in words)  findCalories()
        else addFood(allFood)

        return request
    }

    private fun findCalories() {

    }

    private fun addFood(allFood: retrofit2.Response<FoodList>) {
        if (allFood.isSuccessful) {
            var nameFood = allFood.body()!!.hints[0].food.label
            var imageFood = allFood.body()!!.hints[0].food.image
            var nutrientsFood = Nutrients(1f,
                allFood.body()!!.hints[0].food.nutrients.calories,
                allFood.body()!!.hints[0].food.nutrients.protein,
                allFood.body()!!.hints[0].food.nutrients.fat,
                allFood.body()!!.hints[0].food.nutrients.carb
            )
            Log.e("aimybox", "${imageFood}")

            var food = Food(nameFood, nutrientsFood, imageFood)
            sendToFirebase(food)

        } else {
            Log.e("aimybox", "foodApiService error")
        }
    }

    private fun sendToFirebase(food: Food) {
        firebase = Firebase()
        firebase.sendCurrentMealDataToFirebase(food)
        //HomeFragment().updateNutrients()
    }

    override suspend fun onResponse(
        response: AimyboxResponse,
        aimybox: Aimybox,
        defaultHandler: suspend (Response) -> Unit
    ) {

        Log.e("aimybox", "onResponse query request is $queryRequest")
    }
}