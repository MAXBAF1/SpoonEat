package com.example.movieretrofit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var launcher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) activityResultSignIn(result.data)
            }

        onClickSearch()
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
}