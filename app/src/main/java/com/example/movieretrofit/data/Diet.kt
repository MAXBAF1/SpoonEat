package com.example.movieretrofit.data

class Diet() : java.io.Serializable {
    var proteinCf: Int = 30
    var fatCf: Int = 30
    var carbsCf: Int = 40

    constructor(_proteinCf: Int, _fatCf: Int, _carbsCf: Int) : this() {
        proteinCf = _proteinCf
        fatCf = _fatCf
        carbsCf = _carbsCf
    }
}