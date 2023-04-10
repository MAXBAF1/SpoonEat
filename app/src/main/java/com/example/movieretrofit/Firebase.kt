package com.example.movieretrofit

import android.util.Log
import com.example.movieretrofit.data.Diet
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.utils.MySharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class Firebase {
    var diet: Diet = Diet()
    var username: String
    var database: FirebaseDatabase
    var usersRef: DatabaseReference
    var dateRef: DatabaseReference
    var dietRef: DatabaseReference
    val sharedPreferences = MySharedPreferences(MainActivity())

    private var auth: FirebaseAuth = Firebase.auth
    private val sdf = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())
    private val date = sdf.format(Calendar.getInstance().time)

    init {
        username = auth.currentUser!!.displayName.toString()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")
        dateRef = usersRef.child(username).child("meal").child(date)
        dietRef = usersRef.child(username).child("diet")
    }

    fun loadUser() {
        usersRef.child(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Если пользователя нет в базе данных, создаем новый узел для него
                    usersRef.child(username).setValue("")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        Log.e("item", auth.currentUser!!.displayName.toString())
    }

    fun signOut() {
        auth.signOut()
    }


    fun getCurrentDayFoodDataFromFirebase(callback: (List<Food>) -> Unit) {
        val foodData = mutableListOf<Food>()
        dateRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (mealSnapshot in dataSnapshot.children) {
                    val meal = mealSnapshot.getValue(Food::class.java)
                    meal?.let {
                        val nutrients = Nutrients(
                            it.nutrients!!.grams,
                            it.nutrients!!.calories,
                            it.nutrients!!.protein,
                            it.nutrients!!.fat,
                            it.nutrients!!.carbs
                        )
                        foodData.add(Food(it.name, it.image, it.content, nutrients))
                    }
                }
                callback(foodData)

                for (food in foodData) {
                    sharedPreferences.saveFood(food)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("item", "in getNutrientsFromFirebase Firebase onCancelled")
            }
        })
    }

    fun getUserDietFromFirebase(callback: (diet: Diet) -> Unit) {
        dietRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dietHashMap = dataSnapshot.value as? HashMap<*, Float>

                if (dietHashMap != null)
                    diet =
                        Diet(dietHashMap["protein"]!!, dietHashMap["fat"]!!, dietHashMap["carbs"]!!)
                callback(diet)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("item", "in getNutrientsFromFirebase Firebase onCancelled")
            }
        })
    }

    fun sendCurrentMealDataToFirebase(foodDataToSend: Food) {
        val query = dateRef.child(usersRef.push().key ?: "blablabla")

        query.child("name").setValue(foodDataToSend.name)
        query.child("image").setValue(foodDataToSend.image)
        query.child("nutrients").child("grams").setValue(foodDataToSend.nutrients!!.grams)
        query.child("nutrients").child("calories").setValue(foodDataToSend.nutrients!!.calories)
        query.child("nutrients").child("protein").setValue(foodDataToSend.nutrients!!.protein)
        query.child("nutrients").child("calories").setValue(foodDataToSend.nutrients!!.calories)
        query.child("carbs").setValue(
            (((
                    foodDataToSend!!.nutrients!!.calories
                            - (foodDataToSend!!.nutrients!!.fat * 9.3
                            + foodDataToSend!!.nutrients!!.protein * 4.1)) / 4.1)
                    * foodDataToSend.nutrients!!.grams)
                .toInt()
        )

        sharedPreferences.saveFood(foodDataToSend)
    }

    fun sendUserDietToFirebase(diet: Diet) {
        Log.e("item", "in Firebase sendUserDietToFirebase ${diet.proteinCoeff}")

        dietRef.child("protein").setValue(diet.proteinCoeff.toInt())
        dietRef.child("fat").setValue(diet.fatCoeff.toInt())
        dietRef.child("carbs").setValue(diet.carbsCoeff.toInt())
    }
}