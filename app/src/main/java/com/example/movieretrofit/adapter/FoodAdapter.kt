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
import androidx.recyclerview.widget.RecyclerView
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.interfaces.AddFoodListener
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
        val food = foodList[position]

        Picasso.get().load(food.image?.toUri()).placeholder(R.mipmap.ic_launcher)
            .into(holder.itemView.findViewById<ImageView>(R.id.food_image))

        //holder.itemView.findViewById<TextView>(R.id.food_name).text = currentItem.name

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
        val protein = "${food.nutrients.protein.roundToInt()} г"
        val fat = "${food.nutrients.fat.roundToInt()} г"
        val carbs = "${food.nutrients.carb.roundToInt()} г"

        holder.itemView.findViewById<TextView>(R.id.food_protein).text = protein
        holder.itemView.findViewById<TextView>(R.id.food_fat).text = fat
        holder.itemView.findViewById<TextView>(R.id.food_carbs).text = carbs
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun setData(foods: List<Food>) {
        this.foodList = foods
        notifyDataSetChanged()
    }
}