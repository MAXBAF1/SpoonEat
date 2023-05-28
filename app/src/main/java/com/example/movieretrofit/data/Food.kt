package com.example.movieretrofit.data

data class Food(
    val label: String,
    //@SerializedName("nutrients")
    val nutrients: Nutrition,
    val image: String,
    var realNutrients: Nutrients
): java.io.Serializable