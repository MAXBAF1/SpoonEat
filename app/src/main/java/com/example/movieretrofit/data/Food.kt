package com.example.movieretrofit.data

data class Food(
    val label: String,
    val nutrients: Nutrients,
    val image: String
) {
    constructor() : this("", Nutrients(), "")
}