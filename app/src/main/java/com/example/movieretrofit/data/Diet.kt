package com.example.movieretrofit.data

class Diet() : java.io.Serializable {
    var protetnCoeff: Float = 0.0f
    var fatCoeff: Float = 0.0f
    var carbsCoeff: Float = 0.0f

    constructor(_proteinCoeff: Float, _fatCoeff: Float, _carbsCoeff: Float) : this() {
        protetnCoeff = _proteinCoeff
        fatCoeff = _fatCoeff
        carbsCoeff = _carbsCoeff
    }
}