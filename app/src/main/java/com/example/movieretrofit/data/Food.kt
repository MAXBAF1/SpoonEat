package com.example.movieretrofit.data

data class Food(
    var name: String? = null,
    var image: String? = null,
    var content: String? = null,
    var nutrients: Nutrients = Nutrients()
) : java.io.Serializable