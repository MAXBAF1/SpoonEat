package com.example.movieretrofit.interfaces

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslateService {
    @GET("/language/translate/v2")
    fun translate(
        @Query("key") apiKey: String,
        @Query("q") text: String,
        @Query("target") target: String
    ): Call<TranslateResponse>
}

data class TranslateResponse(
    val data: Data
)

data class Data(
    val translations: List<Translation>
)

data class Translation(
    val translatedText: String
)