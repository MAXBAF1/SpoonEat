package com.example.movieretrofit.data

class Diet() : java.io.Serializable {
    var proteinCoeff: Float = 30f
    var fatCoeff: Float = 30f
    var carbsCoeff: Float = 40f

    constructor(_proteinCf: Float, _fatCf: Float, _carbsCf: Float) : this() {
        proteinCoeff = _proteinCf
        fatCoeff = _fatCf
        carbsCoeff = _carbsCf
    }
}