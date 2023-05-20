package com.example.movieretrofit.data

class Nutrients() : java.io.Serializable {
    var grams: Float = 1f
    var calories: Float = 0f
    var protein: Float = 0f
    var fat: Float = 0f
    var carb: Float = 0f

    constructor(
        _grams: Float, _calories: Float, _protein: Float, _fat: Float, _carbs: Float
    ) : this() {
        grams = _grams
        calories = _calories
        protein = _protein
        fat = _fat
        carb = _carbs
    }

    fun updateWithNutrition(nutrition: Nutrition){
        calories = nutrition.calories
        protein = nutrition.protein.removeSuffix("g").toFloat()
        fat = nutrition.fat.removeSuffix("g").toFloat()
        carb = nutrition.carbs.removeSuffix("g").toFloat()
    }

    fun getDaySum(foodItems: List<Food>): Nutrients {
        var currentDaySum = Nutrients()
        foodItems.forEach {
            currentDaySum += it.nutrients
        }

        return currentDaySum
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

    private fun getNutrientWithCf(nutrient: Float, sumGrams: Float, cf: Int): Float =
        100 - (cf / grams - (nutrient / sumGrams * 100))

    operator fun plus(newNutrients: Nutrients): Nutrients {
        calories += newNutrients.calories
        protein += newNutrients.protein
        fat += newNutrients.fat
        carb += newNutrients.carb
        return this
    }

}

