package com.example.movieretrofit

import android.os.Bundle
<<<<<<< HEAD
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.movieretrofit.databinding.ActivitySearchBinding

//const val API_KEY = "6e787e3ba6ae4fdf8ff34be760e2b14e"
const val API_KEY = "f1a7af1bc1744b7c8fd25cb13f716aef"
=======
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.databinding.ActivitySearchBinding

//const val API_KEY = "6e787e3ba6ae4fdf8ff34be760e2b14e" // maxlepinskih
//const val API_KEY = "3895be68fe5b44b49bb4e3848c012498" // polina
const val API_KEY = "f1a7af1bc1744b7c8fd25cb13f716aef" // gogohihi8
>>>>>>> 1ef3a416805247a36f1ec45aceb99df055fa17a8
const val BASE_URI = "https://api.spoonacular.com/food/"

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBarWithNavController(findNavController(R.id.fragment))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

<<<<<<< HEAD
=======
    fun handleNutrientsData(nutrients: Nutrients){
        intent.putExtra("Nutrients", nutrients)
        setResult(RESULT_OK, intent)
        finish()
    }

>>>>>>> 1ef3a416805247a36f1ec45aceb99df055fa17a8
    override fun onBackPressed() {
        super.onBackPressed();
    }
}