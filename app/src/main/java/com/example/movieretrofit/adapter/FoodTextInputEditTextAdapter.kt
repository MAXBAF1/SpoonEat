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
import com.squareup.picasso.Picasso

class FoodTextInputEditTextAdapter(private val foodClickListener: FoodClickListener) :
    RecyclerView.Adapter<FoodTextInputEditTextAdapter.FoodTextInputViewHolder>() {

    var foodList = emptyList<Food>()

    class FoodTextInputViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodTextInputViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.foodtextinput_list, parent, false
            )
        return FoodTextInputViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodTextInputViewHolder, position: Int) {
        val currentItem = foodList[position]
        holder.itemView.findViewById<TextView>(R.id.tv_foodtexinput).text = currentItem.name
        Picasso.get()
            .load(currentItem.image?.toUri())
            .placeholder(R.mipmap.ic_launcher)
            .into(holder.itemView.findViewById<ImageView>(R.id.imaging))
        holder.itemView.setOnClickListener {
            foodClickListener.onFoodClickListener(currentItem.name!!)
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