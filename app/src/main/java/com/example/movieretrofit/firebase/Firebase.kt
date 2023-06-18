package com.example.movieretrofit.firebase

import android.util.Log
import com.example.movieretrofit.data.Diet
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.fragments.ui.HomeFragment
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
    var foodsRef: DatabaseReference
    var dietRef: DatabaseReference
    var usernameRef: DatabaseReference
    var mealRef: DatabaseReference

    private var auth: FirebaseAuth = Firebase.auth
    val sdf = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())
    private var date = sdf.format(Calendar.getInstance().time)
    var firebaseBalance: FirebaseBalance

    init {
        username = auth.currentUser!!.displayName.toString()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")
        usernameRef = usersRef.child(username)
        mealRef = usernameRef.child("meal")
        dateRef = mealRef.child(date)
        foodsRef = dateRef.child("foods")
        dietRef = usernameRef.child("diet")
        getUserDietFromFirebase { diet = it }
        firebaseBalance = FirebaseBalance(this)
        firebaseBalance.updateBalanceInfo()
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

    fun getDateRef(date: String): DatabaseReference {
        return mealRef.child(date)
    }

    fun getDayFoods(foodsReference: DatabaseReference, callback: (List<Food>) -> Unit) {
        foodsReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                callback(snapshot.children.map { data -> data.getValue(Food::class.java)!! })
            }
        }
    }

    fun getTodayNutrients(completion: (List<Nutrients>) -> Unit) =
        getLastNDaysNutrients(1) { completion(it) }

    fun getWeeklyNutrients(completion: (List<Nutrients>) -> Unit) =
        getLastNDaysNutrients(7) { completion(it) }

    fun getMonthNutrients(completion: (List<Nutrients>) -> Unit) =
        getLastNDaysNutrients(31) { completion(it) }

    private fun getLastNDaysNutrients(daysCnt: Int, completion: (List<Nutrients>) -> Unit) {
        val nutrientList = mutableListOf<Nutrients>()

        for (i in daysCnt - 1 downTo 0) {
            val sdf = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)

            val dateRef = getDateRef(sdf.format(calendar.time))
            getDayFoods(dateRef.child("foods")) {

                nutrientList.add(Nutrients().getSumNutrients(it))
                if (nutrientList.size == daysCnt) {
                    completion(nutrientList.toList())
                }
            }
        }
    }

    fun getUserDietFromFirebase(callback: (diet: Diet) -> Unit) {
        dietRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dietDict = dataSnapshot.value as? HashMap<*, *>

                if (dietDict != null && dietDict.size == 4) diet = Diet(
                    dietDict["name"] as String,
                    dietDict["protein"].toString().toFloat(),
                    dietDict["fat"].toString().toFloat(),
                    dietDict["carbs"].toString().toFloat(),
                )
                callback(diet)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("item", "in getNutrientsFromFirebase Firebase onCancelled")
            }
        })
    }

    fun deleteFoodFromFirebase(dateReference: DatabaseReference, numToDelete: Int) {
        var counter = numToDelete
        dateReference.child("foods").orderByKey().limitToLast(numToDelete)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnap in snapshot.children) {
                        if (numToDelete == counter) {
                            childSnap.ref.removeValue()
                            firebaseBalance.updateBalanceInfo()
                            break
                        } else counter--
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("onDeleteClick", "onCancelled", error.toException())
                }
            })
    }

    fun sendCurrentMealDataToFirebase(foodDataToSend: Food) {
        val query = foodsRef.child(usersRef.push().key ?: "blablabla")
        val nutrientsPath = query.child("nutrients")
        val nutrients = foodDataToSend.nutrients
        Log.e("item", nutrients.grams.toString())
        query.child("label").setValue(foodDataToSend.label)
        query.child("image").setValue(foodDataToSend.image)
        nutrientsPath.child("grams").setValue(nutrients.grams)
        nutrientsPath.child("calories").setValue(nutrients.calories * nutrients.grams)
        nutrientsPath.child("protein").setValue(nutrients.protein * nutrients.grams)
        nutrientsPath.child("fat").setValue(nutrients.fat * nutrients.grams)
        nutrientsPath.child("carb").setValue(nutrients.carb * nutrients.grams)

        firebaseBalance.updateBalanceInfo()
    }

    fun sendUserDietToFirebase(diet: Diet) {
        Log.e("item", "in Firebase sendUserDietToFirebase ${diet.proteinCf}")

        dietRef.child("name").setValue(diet.name)
        dietRef.child("protein").setValue(diet.proteinCf)
        dietRef.child("fat").setValue(diet.fatCf)
        dietRef.child("carbs").setValue(diet.carbsCf)
    }
}