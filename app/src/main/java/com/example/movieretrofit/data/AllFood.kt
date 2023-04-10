package com.example.movieretrofit.data

data class AllFood(
    var query: String? = null,
    //@SerializedName("searchResults")
    var searchResults: List<CategoryFood>
)


