package com.example.movieretrofit.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.MainActivity
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.fragments.ui.HomeFragment
import com.example.movieretrofit.interfaces.AddFoodListener
import com.example.movieretrofit.model.SharedViewModel
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

class FoodAdapter(private val addFoodListener: AddFoodListener) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    var foodList = emptyList<Food>()
    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.foods_list, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val currentItem = foodList[position]

        Picasso.get().load(currentItem.image?.toUri()).placeholder(R.mipmap.ic_launcher)
            .into(holder.itemView.findViewById<ImageView>(R.id.food_image))

        holder.itemView.findViewById<TextView>(R.id.food_name).text = currentItem.name
        val food = Food(
            currentItem.name,
            currentItem.image,
            currentItem.content,
            Nutrients().getNutrients(currentItem.content!!)
        )
        Log.e("item", "nutrients in Food adapter ${currentItem.nutrients}")

        initNutrientsTv(holder, food)

        holder.itemView.findViewById<Button>(R.id.button_add_food).setOnClickListener {
            val etGrams = holder.itemView.findViewById<EditText>(R.id.et_gram).text
            val grams: Float = if (etGrams.isEmpty()) 1f else {
                etGrams.toString().toFloat() / 100f
            }
            food.nutrients.grams = grams
            Log.e("item", " in Food adapter $grams")

            addFoodListener.onFoodReceived(food)
            Log.e("watcher", "1 in Food adapter onBindViewHolder addFoodListener ")

            val firebase = Firebase()
            firebase.sendCurrentMealDataToFirebase(food)
        }
    }

    private fun initNutrientsTv(holder: FoodViewHolder, food: Food) {
        val calories = "${food.nutrients.calories.roundToInt()} ккал"
        val protein = "${food.nutrients.protein.roundToInt()} гр."
        val fat = "${food.nutrients.fat.roundToInt()} гр."

        holder.itemView.findViewById<TextView>(R.id.food_calories).text = calories
        holder.itemView.findViewById<TextView>(R.id.food_protein).text = protein
        holder.itemView.findViewById<TextView>(R.id.food_fat).text = fat
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun setData(foods: List<Food>) {
        this.foodList = foods
        notifyDataSetChanged()
    }
}