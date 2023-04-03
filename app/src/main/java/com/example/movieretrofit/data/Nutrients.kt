package com.example.movieretrofit.data

class Nutrients() : java.io.Serializable {
    var calories: Float? = null
    var protein: Float? = null
    var fat: Float? = null
    var carbs: Float? = null

    constructor(_calories: Float?, _protein: Float?, _fat: Float?, _carbs: Float?) : this() {
        calories = _calories
        protein = _protein
        fat = _fat
        carbs = _carbs
    }

    fun getNutrients(content: String) : Nutrients{
        val calories = Regex("""\b(\d+)\s*calories\b""").find(content)?.groups?.get(1)?.value?.toFloat()
        val protein = Regex("""\b(\d+)\s*g of protein\b""").find(content)?.groups?.get(1)?.value?.toFloat()
        val fat = Regex("""\b(\d+)\s*g of fat\b""").find(content)?.groups?.get(1)?.value?.toFloat()
        val carbs = (calories !!- (fat !!* 9.3 + protein !!* 4.1)/4.1).toString().split('.')[0].toFloat()
        return Nutrients(calories, protein, fat, carbs)
    }

    operator fun plus(newNutrients: Nutrients): Nutrients {
        calories = calories!! + newNutrients.calories!!
        protein = protein!! + newNutrients.protein!!
        fat = fat!! + newNutrients.fat!!
        return this
    }
}
