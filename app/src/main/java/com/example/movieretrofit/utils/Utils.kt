package com.example.retrofittraining.Utils

import android.text.TextUtils
import java.math.RoundingMode
import java.text.DecimalFormat

fun FloatRoundTo(nutrietns: Float): String {
    val nutrient = DecimalFormat("#.##")
    nutrient.roundingMode = RoundingMode.CEILING
    return nutrient.format(nutrietns)
}

fun inputCheck(name: String): Boolean {
    return !(TextUtils.isEmpty(name))
}

