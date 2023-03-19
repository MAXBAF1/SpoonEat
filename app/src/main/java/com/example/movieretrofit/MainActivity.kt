package com.example.movieretrofit

import android.R
import android.app.AlertDialog
import android.os.Bundle
import android.widget.SimpleCursorAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.movieretrofit.autoCompleteTv.Provider
import com.example.movieretrofit.common.Common
import com.example.movieretrofit.databinding.ActivityMainBinding
import com.example.movieretrofit.interfaces.RetrofitServices
import com.example.movieretrofit.model.AllFood
import dmax.dialog.SpotsDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var mService: RetrofitServices
    lateinit var binding: ActivityMainBinding
    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mService = Common.retrofitService
        dialog = SpotsDialog.Builder().setCancelable(true).setContext(this).build()

        //initViews()
        onClickSend()
        autoCompleteTv()
    }


    private fun initViews() {
        val allFood = getAllFood()
//        val firstReceipt = allFood!!.searchResults[0].results[0]
//        binding.query.text = firstReceipt.name
//        binding.content.text = firstReceipt.content
    }

    fun getAllFood(query: String = "Banana"): AllFood? {
        dialog.show()
        var allFood: AllFood? = null
        mService.getAllFood(query).enqueue(object : Callback<AllFood> {
            override fun onFailure(call: Call<AllFood>, t: Throwable) {

            }

            override fun onResponse(call: Call<AllFood>, response: Response<AllFood>) {
                allFood = response.body()
                dialog.dismiss()

                val firstReceipt = allFood!!.searchResults[0].results[0]
                binding.query.text = firstReceipt.name
                binding.content.text = firstReceipt.content
            }
        })

        return allFood
    }

    private fun autoCompleteTv() {
        //val actv = AutoCompleteTextView(this)
        //actv.threshold = 1
        binding.autoCompleteTv.threshold = 1
        val from = arrayOf("name")
        val to = intArrayOf(R.id.text1)
        val adapter = SimpleCursorAdapter(this, R.layout.simple_list_item_2, null, from, to, 0)
        adapter.stringConversionColumn = 1

        val provider = Provider()

        adapter.filterQueryProvider = provider
        //actv.setAdapter(adapter)
        binding.autoCompleteTv.setAdapter(adapter)

//        setContentView(
//            actv, ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//        )
    }

    private fun onClickSend() {
        binding.btnSend.setOnClickListener {
            val allFood = getAllFood(binding.autoCompleteTv.text.toString())

            val firstReceipt = allFood!!.searchResults[0].results[0]
//            binding.query.text = firstReceipt.name
//            binding.content.text = firstReceipt.content
        }
    }
}