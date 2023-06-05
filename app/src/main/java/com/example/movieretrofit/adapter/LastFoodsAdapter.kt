package com.example.movieretrofit.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.fragments.ui.HomeFragment
import kotlin.math.roundToInt

class LastFoodsAdapter(
    private val context: Context,
    private val foods: List<Food>,
    private val homeFragment: HomeFragment
    ) :  RecyclerView.Adapter<LastFoodsAdapter.FoodViewHolder>() {

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
        val food = foods[position]
        holder.bind(food)
        holder.itemView.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context).create()
            val layoutInflater = LayoutInflater.from(context)
            val view = layoutInflater.inflate(R.layout.foods_list, null)
            initView(view, food)

            alertDialog.setView(view)
            val deleteBtn = view.findViewById<Button>(R.id.button_delete_food)
            deleteBtn.visibility = View.VISIBLE
            deleteBtn.setOnClickListener {
                homeFragment.deleteItemByIndex(position + 1)
                homeFragment.updateViews()
                alertDialog.dismiss()
            }
            alertDialog.window?.setBackgroundDrawableResource(R.drawable.rounded_background)
            alertDialog.show()
        }
    }

    private fun initView(view: View, food: Food) {
        val proteinText = "${food.nutrients.protein.roundToInt()} г"
        val fatText = "${food.nutrients.fat.roundToInt()} г"
        val carbText = "${food.nutrients.carb.roundToInt()} г"
        val gramsText = "Размер порции - ${(food.nutrients.grams * 100).roundToInt()} г"
        view.findViewById<TextView>(R.id.tv_food_label).text = food.label
        view.findViewById<TextView>(R.id.food_protein).text = proteinText
        view.findViewById<TextView>(R.id.food_fat).text = fatText
        view.findViewById<TextView>(R.id.food_carbs).text = carbText
        view.findViewById<TextView>(R.id.food_size_tv).text = gramsText
        view.findViewById<EditText>(R.id.ed_gram).visibility = View.GONE
        view.findViewById<Button>(R.id.button_add_food).visibility = View.GONE
        Glide.with(context)
            .load(food.image)
            .transform(RoundedCorners(30))
            .into(view.findViewById(R.id.food_image))
    }
}