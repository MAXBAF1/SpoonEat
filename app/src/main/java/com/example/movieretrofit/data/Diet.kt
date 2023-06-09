package com.example.movieretrofit.data

class Diet() : java.io.Serializable {
    var name: String = ""
    var proteinCf: Float = 30f
    var fatCf: Float = 30f
    var carbsCf: Float = 40f

    constructor(_name: String, _proteinCf: Float, _fatCf: Float, _carbsCf: Float) : this() {
        name = _name
        proteinCf = _proteinCf
        fatCf = _fatCf
        carbsCf = _carbsCf
    }

    constructor(_proteinCf: Float, _fatCf: Float, _carbsCf: Float) : this() {
        proteinCf = _proteinCf
        fatCf = _fatCf
        carbsCf = _carbsCf
    }
}