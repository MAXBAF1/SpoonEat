package com.example.movieretrofit.common

import com.example.movieretrofit.interfaces.RetrofitServices
import com.example.movieretrofit.retrofit.RetrofitClient

object Common {
    private const val BASE_URL = "https://api.spoonacular.com/food/"
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}