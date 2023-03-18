package com.example.movieretrofit.interfaces

import com.example.movieretrofit.model.AllFood
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitServices {
    @GET("search?apiKey=6e787e3ba6ae4fdf8ff34be760e2b14e")
    fun getAllFood(@Query("query") query: String = "apple"): Call<AllFood>
}