package com.example.movieretrofit

import android.content.Intent
import android.os.Bundle
import android.util.Log
<<<<<<< HEAD
import android.widget.ImageView
=======
>>>>>>> 1ef3a416805247a36f1ec45aceb99df055fa17a8
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
<<<<<<< HEAD
import androidx.core.net.toUri
import com.example.movieretrofit.data.Food
=======
import com.example.movieretrofit.data.Nutrients
>>>>>>> 1ef3a416805247a36f1ec45aceb99df055fa17a8
import com.example.movieretrofit.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
<<<<<<< HEAD
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
=======

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

>>>>>>> 1ef3a416805247a36f1ec45aceb99df055fa17a8
    private var launcher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

<<<<<<< HEAD
        var kcal = intent.getStringExtra("kcal")
        var fat = intent.getStringExtra("fat")
        var procnt = intent.getStringExtra("procnt")

        onClickSearch()


        if (kcal != null){
            var bkcal: Float = kcal.toString().toFloat()
            var bfat: Float = fat.toString().toFloat()
            var bprocnt: Float = procnt.toString().toFloat()

            Log.e("item", bkcal.toString())
            setBarChart(bkcal, bfat, bprocnt)
        }

    }


    private fun onClickSearch() {
        binding.btnSearch.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }
    }

    private fun setBarChart(kcal: Float, fat: Float, procnt: Float) {

        val entries = ArrayList<BarEntry>()

        /*  x -  координаты для обозначения кол-ва данных
        * у - длина графика  */
        entries.add(BarEntry(1f, kcal, "kcal"))
        entries.add(BarEntry(2f, fat, "fat"))
        entries.add(BarEntry(3f, procnt, "procnt"))
=======
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
>>>>>>> 1ef3a416805247a36f1ec45aceb99df055fa17a8

        val barDataSet = BarDataSet(entries, "g")
        val data = BarData(barDataSet)
        binding.barChart.data = data // set the data and list of lables into chart
<<<<<<< HEAD
        barDataSet.color = resources.getColor(R.color.dark_green)

        binding.barChart.animateY(5000)
=======
        barDataSet.color = this.getColor(R.color.purple)

        binding.barChart.animateY(500)
>>>>>>> 1ef3a416805247a36f1ec45aceb99df055fa17a8
    }
}