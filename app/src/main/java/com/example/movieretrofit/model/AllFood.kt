package com.example.movieretrofit.model

data class AllFood(
    var query: String? = null,
    var totalResults: Int,
    var searchResults: List<Category>
)

data class Category(
    var name: String? = null,
    var results: List<CertainFood>
)

data class CertainFood(
    var name: String? = null
)
