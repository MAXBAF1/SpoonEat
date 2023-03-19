package com.example.movieretrofit.model

import com.google.gson.annotations.SerializedName

data class AllFood(
    var query: String? = null,
    //@SerializedName("searchResults")
    var searchResults: List<Category>
)

data class Category(
    var name: String? = null,
    var results: List<CertainFood>
)

data class CertainFood(
    var name: String? = null,
    var content: String? = null
)
