package com.example.movieretrofit.fragments.ui.chat.common

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.fragments.ui.chat.MessageAdapter
import com.example.movieretrofit.fragments.ui.chat.common.Constants.OPEN_GOOGLE
import com.example.movieretrofit.fragments.ui.chat.common.Constants.OPEN_SEARCH
import com.example.movieretrofit.model.restFoodApi
import com.example.movieretrofit.translator.Translator
import kotlinx.coroutines.*
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

object BotResponse {
    private val messageList = mutableListOf<String>()
    private lateinit var adapter: MessageAdapter

    fun basicResponses(_message: String): String {
        val random = (0..2).random()
        val message = _message.toLowerCase()

        return when {
            message.contains("добавь")-> {
                val food: String = message.toLowerCase().substringAfterLast("добавь ")
                Log.e("response", "food is ${food}")

                "Продукт $food добавлен"
            }

            message.contains("сколько") && (message.contains("калорий") || message.contains("ккал")) -> {
                val food: String = message.toLowerCase().substringAfterLast("в ")
                "В продукте $food калорий:"
            }

            message.contains("сколько") && (message.contains("белков")) -> {
                val food: String = message.toLowerCase().substringAfterLast("в ")
                "В продукте $food белков:"
            }

            message.contains("сколько") && (message.contains("жиров")) -> {
                val food: String = message.toLowerCase().substringAfterLast("в ")
                "В продукте $food жиров:"
            }

            message.contains("сколько") && (message.contains("углеводов")) -> {
                val food: String = message.toLowerCase().substringAfterLast("в ")
                "В продукте $food углеводов:"
            }

            //Flips a coin
            message.contains("подбрось") && message.contains("монетку") -> {
                val r = (0..1).random()
                val result = if (r == 0) "орёл" else "решка"

                "Я подбросил монету, и она упала на $result"
            }

            //Math calculations
            message.contains("реши") -> {
                val equation: String? = message.substringAfterLast("реши")
                return try {
                    val answer = SolveMath.solveMath(equation ?: "0")
                    "$answer"

                } catch (e: Exception) {
                    "Извините, я не могу это решить"
                }
            }

            //Hello
            message.contains("привет") -> {
                when (random) {
                    0 -> "Привет!"
                    1 -> "Sup"
                    2 -> "Buongiorno!"
                    else -> "error"
                }
            }

            //What time is it?
            message.contains("времени") && message.contains("?") -> {
                val timeStamp = Timestamp(System.currentTimeMillis())
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val date = sdf.format(Date(timeStamp.time))

                date.toString()
            }

            //Open Google
            message.contains("открой") && message.contains("google") -> {
                OPEN_GOOGLE
            }

            //Search on the internet
            message.contains("поиск") -> {
                OPEN_SEARCH
            }

            //When the programme doesn't understand...
            else -> {
                when (random) {
                    0 -> "I don't understand..."
                    1 -> "Try asking me something different"
                    2 -> "Idk"
                    else -> "error"
                }
            }
        }
    }
}