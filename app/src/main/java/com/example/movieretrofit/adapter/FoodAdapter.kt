package com.example.movieretrofit.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
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

        var contx = currentItem.content.toString()
        holder.itemView.findViewById<TextView>(R.id.food_name).text = currentItem.name

        var dictionaryCBZHU = mutableMapOf("calories" to "", "fat" to "", "protein" to "")
        var cleanedStr = contx.replace("<b>", "").replace("</b>", "").replace("of ", "")
        var arr = cleanedStr.split(Regex("[^\\p{Alnum}]"))
        for(i in 1 until  arr.size ){
                if (arr[i] == "protein" || arr[i]== "fat" || arr[i]== "calories"){
                    when (arr[i]){
                        "protein" -> dictionaryCBZHU["protein"] = arr[i-1].dropLast(1)
                        "fat" -> dictionaryCBZHU["fat"] = arr[i-1].dropLast(1)
                        "calories" -> dictionaryCBZHU["calories"] = arr[i-1]
                    }
            }
        }

         holder.itemView.findViewById<TextView>(R.id.food_enerckcal).text =
             dictionaryCBZHU.get("calories") + "kcal"
        holder.itemView.findViewById<TextView>(R.id.food_fat).text =
            dictionaryCBZHU.get("fat") +  "g"
        holder.itemView.findViewById<TextView>(R.id.food_procnt).text =
            dictionaryCBZHU.get("protein") + "g"

        holder.itemView.findViewById<Button>(R.id.button_add_food).setOnClickListener  { v ->
            val intent = Intent(holder.itemView.context, MainActivity::class.java)
            intent.putExtra("kcal", dictionaryCBZHU.get("calories"))
            intent.putExtra("fat", dictionaryCBZHU.get("fat"))
            intent.putExtra("procnt", dictionaryCBZHU.get("protein"))
            holder.itemView.context.startActivity(intent)
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