package com.example.movieretrofit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.movieretrofit.common.Common
import com.example.movieretrofit.databinding.ActivityMainBinding
import com.example.movieretrofit.interfaces.RetrofitServices
import com.example.movieretrofit.model.AllFood
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var mService: RetrofitServices
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mService = Common.retrofitService

        getAllFood()
    }

    private fun getAllFood() {
        mService.getAllFood("Banana").enqueue(object : Callback<AllFood> {
            override fun onFailure(call: Call<AllFood>, t: Throwable) {

            }

            override fun onResponse(call: Call<AllFood>, response: Response<AllFood>) {
                val food = response.body() as AllFood

                val firstReceipt = food.searchResults[0].results[0]
                binding.query.text = firstReceipt.name
                binding.content.text = firstReceipt.content
            }
        })
    }
}