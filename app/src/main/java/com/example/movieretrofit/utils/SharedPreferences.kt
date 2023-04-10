package com.example.movieretrofit.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.movieretrofit.data.Food
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MySharedPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveFood(food: Food) {
        val editor = sharedPreferences.edit()
        editor.putString(food.name, gson.toJson(food))
        editor.apply()
    }

    fun getFoods(): List<Food> {
        val foods = mutableListOf<Food>()
        val allEntries: Map<String, *> = sharedPreferences.all

        for ((key, value) in allEntries) {
            val json = value as String
            val food = gson.fromJson<Food>(json, object : TypeToken<Food>() {}.type)
            foods.add(food)
        }

        return foods
    }
}