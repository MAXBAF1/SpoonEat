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
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.interfaces.AddFoodListener
import com.squareup.picasso.Picasso

class FoodAdapter(private val addFoodListener: AddFoodListener) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    var foodList = emptyList<Food>()
    //private var addFoodListener: AddFoodListener? = null

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
            Nutrients().getNutrients(currentItem.content!!))
        Log.e("item", "nutrients in Food adapter ${currentItem.nutrients}")

        initNutrientsTv(holder, food)

        holder.itemView.findViewById<Button>(R.id.button_add_food).setOnClickListener {
            val etGrams = holder.itemView.findViewById<EditText>(R.id.et_gram).text
            val grams: Int = if (etGrams.isEmpty()) 100 else {
                etGrams.toString().toInt()
            }
            food.nutrients!!.grams = grams
            Log.e("item", " in Food adapter $grams")

            addFoodListener.onFoodReceived(food)
        }
    }

    private fun initNutrientsTv(holder: FoodViewHolder, food: Food) {
        val calories = "${food.nutrients!!.calories.toInt()} ккал"
        val protein = "${food.nutrients!!.protein.toInt()} гр."
        val fat = "${food.nutrients!!.fat.toInt()} гр."

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