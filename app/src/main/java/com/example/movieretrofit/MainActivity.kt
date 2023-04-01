package com.example.movieretrofit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var name: String
    //lateinit var adapter: UserAdapter
    private val PERMISSION_REQUEST_CODE = 200

    lateinit var database: FirebaseDatabase
    lateinit var myRef: DatabaseReference

    private var launcher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        Log.e("item", auth.currentUser!!.displayName.toString() )
        //name = auth.currentUser!!.displayName.toString()

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) handleNutrientsData(result.data)
            }
        onClickSearch()
    }

    private fun handleNutrientsData(data: Intent?) {
        if (data == null) {
            Log.d("MyLog", "Data is NULL")
            return
        }

        val nutrients = data.getSerializableExtra("Nutrients") as Nutrients
        val calories = nutrients.calories
        val protein = nutrients.protein
        val fat = nutrients.fat

        Log.e("item", calories.toString())
        setBarChart(calories, fat, protein)
    }

    private fun onClickSearch() {
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            launcher!!.launch(intent)
        }
    }

    private fun setBarChart(calories: Float?, fat: Float?, protein: Float?) {

        val entries = ArrayList<BarEntry>()

        /*  x -  координаты для обозначения кол-ва данных; у - длина графика  */
        entries.add(BarEntry(1f, calories ?: 0f, "kcal"))
        entries.add(BarEntry(2f, fat ?: 0f, "fat"))
        entries.add(BarEntry(3f, protein ?: 0f, "protein"))

        val barDataSet = BarDataSet(entries, "g")
        val data = BarData(barDataSet)
        binding.barChart.data = data // set the data and list of lables into chart
        barDataSet.color = this.getColor(R.color.purple)

        binding.barChart.animateY(500)
    }
}