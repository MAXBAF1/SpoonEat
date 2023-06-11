package com.example.movieretrofit.data

data class Food(
    var label: String,
    val nutrients: Nutrients,
    val image: String
) {
    constructor() : this("", Nutrients(), "")
}