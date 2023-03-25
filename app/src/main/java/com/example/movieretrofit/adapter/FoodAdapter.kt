package com.example.movieretrofit.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.movieretrofit.MainActivity
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Food
import com.example.retrofittraining.Utils.DoubleRoundTo
import com.squareup.picasso.Picasso

class FoodAdapter : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    var foodList = emptyList<Food>()

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.foods_list,
                parent,
                false
            )
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val currentItem = foodList[position]

        Picasso.get()
            .load(currentItem.image?.toUri())
            .placeholder(R.mipmap.ic_launcher)
            .into(holder.itemView.findViewById<ImageView>(R.id.food_image))

        holder.itemView.findViewById<Button>(R.id.button_add_food).setOnClickListener { view ->
            onClickAddFoodBtn(view, currentItem, holder)
            //view.context.startActivity(Intent(holder.itemView.context, MainActivity::class.java))
        }

        holder.itemView.findViewById<TextView>(R.id.food_name).text = currentItem.name
        holder.itemView.findViewById<TextView>(R.id.food_enerckcal).text =
            /*DoubleRoundTo(currentItem.food.nutrients.ENERC_KCAL) +*/ "kcal"
        holder.itemView.findViewById<TextView>(R.id.food_fat).text =
            /*DoubleRoundTo(currentItem.food.nutrients.FAT) +*/ "g"
        holder.itemView.findViewById<TextView>(R.id.food_procnt).text =
            /*DoubleRoundTo(currentItem.food.nutrients.PROCNT) +*/ "g"
    }

    private fun onClickAddFoodBtn(view: View, food: Food, holder: FoodViewHolder) {
        val intent = Intent(holder.itemView.context, MainActivity::class.java)
        intent.putExtra("Food", food)

//        setResult(AppCompatActivity.RESULT_OK, intent)
//        finish()


        view.context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun setData(foods: List<Food>) {
        this.foodList = foods
        notifyDataSetChanged()
    }
}