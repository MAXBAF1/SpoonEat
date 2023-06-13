package com.example.movieretrofit.model

import com.example.movieretrofit.data.Food
import com.example.movieretrofit.data.FoodList
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//const val API_KEY = "6e787e3ba6ae4fdf8ff34be760e2b14e" // maxlepinskih
//const val API_KEY = "3895be68fe5b44b49bb4e3848c012498" // polina
//const val API_KEY = "f1a7af1bc1744b7c8fd25cb13f716aef" // gogohihi8
//const val BASE_URI = "https://api.spoonacular.com/"

//const val APP_ID = "5ae50da9"
//const val APP_KEY = "54ce7472b5ced85685ff91a880c4a224"
const val APP_ID = "4dd1f4dd"
const val APP_KEY = "d2eef9637298e9b2cd763375c5b05021"
const val BASE_URI = "https://api.edamam.com/"

interface FoodApiService {
    @GET("api/food-database/v2/parser?")
    suspend fun getFoodRecipe(
        @Query("ingr") ingr: String,
        @Query("app_id") app_id: String = APP_ID,
        @Query("app_key") app_key: String = APP_KEY
    ): Response <FoodList>

}

var okHttpClient = OkHttpClient.Builder().build()

var retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URI)
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()

var restFoodApi: FoodApiService = retrofit.create(FoodApiService::class.java)
