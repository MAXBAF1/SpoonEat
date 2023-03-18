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
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {
    lateinit var mService: RetrofitServices
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mService = Common.retrofitService

        getAllFood()
        onClickSend()
    }

    private fun getAllFood(query: String = "Banana") {
        mService.getAllFood(query).enqueue(object : Callback<AllFood> {
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

    private fun onClickSend(){
        binding.btnSend.setOnClickListener {
            getAllFood(binding.edQuery.text.toString())
        }
    }
}