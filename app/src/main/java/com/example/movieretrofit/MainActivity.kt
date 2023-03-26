package com.example.movieretrofit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var launcher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var kcal = intent.getStringExtra("kcal")
        var fat = intent.getStringExtra("fat")
        var procnt = intent.getStringExtra("procnt")

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) activityResultSignIn(result.data)
            }

        onClickSearch()


        if (kcal != null){
            var bkcal: Float = kcal.toString().toFloat()
            var bfat: Float = fat.toString().toFloat()
            var bprocnt: Float = procnt.toString().toFloat()

            Log.e("item", bkcal.toString())
            setBarChart(bkcal, bfat, bprocnt)
        }

    }

    private fun activityResultSignIn(data: Intent?) {
        if (data == null) return

        val food = data.getSerializableExtra("Food") as Food

        binding.name.text = food.name
        binding.content.text = food.content
        Picasso.get()
            .load(food.image?.toUri())
            .placeholder(R.mipmap.ic_launcher)
            .into(binding.image)
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

        val barDataSet = BarDataSet(entries, "g")
        val data = BarData(barDataSet)
        binding.barChart.data = data // set the data and list of lables into chart
        barDataSet.color = resources.getColor(R.color.dark_green)

        binding.barChart.animateY(5000)
    }
}