package com.example.movieretrofit

import android.util.Log
import com.example.movieretrofit.adapter.FoodAdapter
import com.example.movieretrofit.data.Nutrients
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class Firebase {
    var username: String
    var database: FirebaseDatabase
    var usersRef: DatabaseReference
    var dateRef: DatabaseReference

    private var auth: FirebaseAuth = Firebase.auth
    private val sdf = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())
    private val date = sdf.format(Calendar.getInstance().time)

    init {
        username = auth.currentUser!!.displayName.toString()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")
        dateRef = usersRef.child(username).child("meal").child(date)
    }

    fun loadUser() {
        usersRef.child(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Если пользователя нет в базе данных, создаем новый узел для него
                    usersRef.child(username).setValue("")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок
            }
        })
        Log.e("item", auth.currentUser!!.displayName.toString())
    }

    fun signOut(){
        auth.signOut()
    }
    fun sendDataToFirebase(nutrients: Nutrients) {
        val grams = nutrients.grams / 100
        Log.e("item", " in Firebase $grams")

        val query = dateRef.child(usersRef.push().key ?: "blablabla")

        query.child("grams").setValue(grams)
        query.child("calories").setValue(nutrients.calories * grams)
        query.child("protein").setValue(nutrients.protein * grams)
        query.child("fat").setValue(nutrients.fat * grams)
        query.child("carbs").setValue(
            (((nutrients.calories  - (nutrients.fat * 9.3 + nutrients.protein * 4.1)) / 4.1) * grams).toInt()
        )
    }

    fun getNutrientsFromFirebase(callback: (nutrients: Nutrients) -> Unit) {
        var nutrients = Nutrients()
        dateRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (mealSnapshot in dataSnapshot.children) {
                    val meal = mealSnapshot.getValue(Nutrients::class.java)
                    meal?.let {
                        nutrients += Nutrients(0, it.calories, it.protein, it.fat, it.carbs)
                    }
                }
                callback(nutrients)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("item", "in Firebase onCancelled")
            }
        })
    }
}