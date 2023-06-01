package com.example.movieretrofit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Food
import kotlin.math.roundToInt

class LastFoodsAdapter(private val context: Context, private val foods: List<Food>) :
    RecyclerView.Adapter<LastFoodsAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.foodNameTextView)
        private val foodCaloriesTv: TextView = itemView.findViewById(R.id.food_calories_tv)
        private val imageView: ImageView = itemView.findViewById(R.id.foodImageView)

        fun bind(food: Food) {
            nameTextView.text = food.label
            val caloriesText = "${food.nutrients.calories.roundToInt()} кКал"
            foodCaloriesTv.text = caloriesText
            loadImage(food.image, imageView)
        }
    }

    private fun loadImage(url: String?, imageView: ImageView) {
        if (url.isNullOrEmpty()){
            Glide.with(context)
                .load(R.drawable.main_icon)
                .transform(RoundedCorners(10))
                .into(imageView)
        }
        else {
            Glide.with(context)
                .load(url)
                .transform(RoundedCorners(10))
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.last_food_item, parent, false)
        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int = foods.size

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foods[position])
    }
}