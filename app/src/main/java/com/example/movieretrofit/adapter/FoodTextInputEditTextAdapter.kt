package com.example.movieretrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.interfaces.FoodClickListener
import com.example.movieretrofit.translator.Translator
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class FoodTextInputEditTextAdapter(private val foodClickListener: FoodClickListener) :
    RecyclerView.Adapter<FoodTextInputEditTextAdapter.FoodTextInputViewHolder>() {

    var foodList = emptyList<Food>()
    private val translator = Translator()

    class FoodTextInputViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodTextInputViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.last_food_item, parent, false
        )
        return FoodTextInputViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodTextInputViewHolder, position: Int) {
        val currentItem = foodList[position]
        val nameTextView: TextView = holder.itemView.findViewById(R.id.foodNameTextView)
        val foodCaloriesTv: TextView = holder.itemView.findViewById(R.id.food_calories_tv)
        val imageView: ImageView = holder.itemView.findViewById(R.id.foodImageView)

        translator.translateEnRu(currentItem.label) {
            nameTextView.text = it
        }
        foodCaloriesTv.visibility = View.GONE
        Picasso.get().load(currentItem.image.toUri()).transform(RoundedCornersTransformation(10, 0))
            .placeholder(R.drawable.main_icon).into(imageView)

        holder.itemView.setOnClickListener {
            foodClickListener.onFoodClickListener(currentItem.label)
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun setData(foods: List<Food>) {
        this.foodList = foods
        notifyDataSetChanged()
    }
}