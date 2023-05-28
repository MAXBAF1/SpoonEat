package com.example.movieretrofit

import android.util.Log
import com.example.movieretrofit.data.Diet
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.data.Nutrients
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
    var usernameRef: DatabaseReference
    var mealRef: DatabaseReference

    private var auth: FirebaseAuth = Firebase.auth
    private val sdf = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())
    var date = sdf.format(Calendar.getInstance().time)

    init {
        username = auth.currentUser!!.displayName.toString()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")
        usernameRef = usersRef.child(username)
        mealRef = usernameRef.child("meal")
        dateRef = mealRef.child(date)
        dietRef = usernameRef.child("diet")
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
        Log.e("item", auth.currentUser?.displayName.toString())
    }

    fun signOut() {
        auth.signOut()
    }

    private fun getDateRef(date: String): DatabaseReference {
        return mealRef.child(date)
    }

    fun getDayFood(dataReference: DatabaseReference, callback: (List<Food>) -> Unit) {
        dataReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val foodData = mutableListOf<Food>()
                foodData.addAll(dataSnapshot.children.map { data -> data.getValue(Food::class.java)!! })

                callback(foodData)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("item", "in getNutrientsFromFirebase Firebase onCancelled")
            }
        })
    }

    fun getLastTenFood(callback: (List<Food>) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(username)
            .child("meal")
            .orderByKey()
            .limitToLast(10)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val foods = mutableListOf<Food>()
                for (dateSnap in snapshot.children.reversed()) {
                    for (foodSnap in snapshot.children.reversed()) {
                        val food = foodSnap.getValue(Food::class.java)
                        food?.let { foods.add(it) }
                    }
                }
                callback(foods)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("item", "in getLastTenFood Firebase onCancelled")
            }
        })
    }

    fun getWeeklyNutrients(completion: (List<Nutrients>) -> Unit) {
        val nutrientList = mutableListOf<Nutrients>()

        for (i in 6 downTo 0) {
            val sdf = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)

            val mealsDateRef = getDateRef(sdf.format(calendar.time))
            getDayFood(mealsDateRef) {
                nutrientList.add(Nutrients().getDaySum(it))
                if (nutrientList.size == 7)
                    completion(nutrientList)
            }
        }
    }

    fun getUserDietFromFirebase(callback: (diet: Diet) -> Unit) {
        dietRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dietHashMap = dataSnapshot.value as? HashMap<*, Int>

                if (dietHashMap != null) diet =
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
        val nutrientsPath = query.child("nutrients")
        val nutrients = foodDataToSend.realNutrients
        Log.e("item", nutrients.grams.toString())
        query.child("name").setValue(foodDataToSend.label)
        query.child("image").setValue(foodDataToSend.image)
        nutrientsPath.child("grams").setValue(nutrients.grams)
        nutrientsPath.child("calories").setValue(nutrients.calories * nutrients.grams)
        nutrientsPath.child("protein").setValue(nutrients.protein * nutrients.grams)
        nutrientsPath.child("fat").setValue(nutrients.fat * nutrients.grams)
        nutrientsPath.child("carb").setValue(nutrients.carb * nutrients.grams)
    }

    fun sendUserDietToFirebase(diet: Diet) {
        Log.e("item", "in Firebase sendUserDietToFirebase ${diet.proteinCf}")

        dietRef.child("protein").setValue(diet.proteinCf)
        dietRef.child("fat").setValue(diet.fatCf)
        dietRef.child("carbs").setValue(diet.carbsCf)
    }
}