package com.example.movieretrofit.utils

import com.example.movieretrofit.data.Food
import com.example.movieretrofit.data.Nutrients

class Helper {
    var nutrientsCoeff = Nutrients(0, 0f, 1f, 1f, 1f)

    fun countCurrentDaySum(foodItem: Food): Nutrients {
        var currentDaySum = Nutrients()
        currentDaySum += Nutrients(
            0,
            foodItem.nutrients!!.calories,
            foodItem.nutrients!!.protein,
            foodItem.nutrients!!.fat,
            foodItem.nutrients!!.carbs
        )
        return currentDaySum
    }

    fun countCoefficientCurrentDaySum(foodItems: List<Food>): Nutrients {
        var currentCoefficientDaySum = Nutrients()
        for (item in foodItems ){
            currentCoefficientDaySum += Nutrients(
                0,
                0f,
                (item.nutrients!!.protein / item.nutrients!!.grams ) * 100 * nutrientsCoeff.protein,
                (item.nutrients!!.fat / item.nutrients!!.grams ) * 100 * nutrientsCoeff.fat,
                (item.nutrients!!.carbs / item.nutrients!!.grams ) * 100 * nutrientsCoeff.carbs,
            )
        }

        return currentCoefficientDaySum
    }



}