package com.example.movieretrofit.data

import com.google.gson.annotations.SerializedName

data class Nutrients(
    var grams: Float = 1f,
    @SerializedName("ENERC_KCAL") var calories: Float = 0f,
    @SerializedName("PROCNT") var protein: Float = 0f,
    @SerializedName("FAT") var fat: Float = 0f,
    @SerializedName("CHOCDF") var carb: Float = 0f
) : java.io.Serializable {
    fun getSumNutrients(foods: List<Food>): Nutrients {
        var sum = Nutrients()
        foods.forEach { sum += it.nutrients }
        return sum
    }

    fun getSum(nutrients: List<Nutrients>): Nutrients {
        var sum = Nutrients()
        nutrients.forEach { sum += it }
        return sum
    }

    fun getBalancedNutrientsInPercentage(diet: Diet): Nutrients {
        val cfNutrients = Nutrients()
        val sumGrams = (protein + fat + carb) * grams

        cfNutrients.protein += getNutrientWithCf(protein, sumGrams, diet.proteinCf)
        cfNutrients.fat += getNutrientWithCf(fat, sumGrams, diet.fatCf)
        cfNutrients.carb += getNutrientWithCf(carb, sumGrams, diet.carbsCf)

        return Nutrients(
            grams,
            calories,
            cfNutrients.protein,
            cfNutrients.fat,
            cfNutrients.carb
        )
    }

     fun isInBounds(bounds: ClosedRange<Float>): Boolean {
        if (fat in bounds && carb in bounds && protein in bounds)
            return true
        return false
    }

    private fun getNutrientWithCf(nutrient: Float, sumGrams: Float, cf: Float): Float =
        100 - (cf / grams - (nutrient / sumGrams * 100))

    operator fun plus(newNutrients: Nutrients): Nutrients {
        calories += newNutrients.calories
        protein += newNutrients.protein
        fat += newNutrients.fat
        carb += newNutrients.carb
        return this
    }
}

