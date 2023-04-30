package com.example.movieretrofit.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.fragments.ui.AccountSettingsFragment
import com.example.movieretrofit.fragments.ui.HomeFragment
import com.example.movieretrofit.fragments.ui.SearchFragment
import com.example.movieretrofit.fragments.ui.StatisticsFragment
import com.example.movieretrofit.model.FoodViewModel
import com.example.movieretrofit.model.restFoodApi
import com.justai.aimybox.Aimybox
import com.justai.aimybox.api.aimybox.AimyboxRequest
import com.justai.aimybox.api.aimybox.AimyboxResponse
import com.justai.aimybox.core.CustomSkill
import com.justai.aimybox.model.Request
import com.justai.aimybox.model.Response

class ChangeViewSkill(private val context: Context) : CustomSkill<AimyboxRequest, AimyboxResponse> {

    lateinit var queryRequest: String
    private lateinit var firebase: Firebase

    override fun canHandle(response: AimyboxResponse) = response.action == "changeView"

    override suspend fun onRequest(request: AimyboxRequest, aimybox: Aimybox): AimyboxRequest {
        queryRequest = request.query

        Log.e("aimybox", "request query is $queryRequest")
        val foodApiService = restFoodApi
        val allFood = foodApiService.getAllFood(query = queryRequest)

        if (allFood.isSuccessful) {
            var nameFood = allFood.body()!!.searchResults[0].results[0].name
            var imageFood = allFood.body()!!.searchResults[0].results[0].image
            var contentFood = allFood.body()!!.searchResults[0].results[0].content
            var nutrientsFood = Nutrients().getNutrients(contentFood!!)

            var food = Food(nameFood, imageFood, contentFood, nutrientsFood)

            sendToFirebase(food)
        } else {
            Log.e("aimybox", "foodApiService error")
        }
        return request
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