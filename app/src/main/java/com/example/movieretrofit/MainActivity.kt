package com.example.movieretrofit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var username: String
    lateinit var database: FirebaseDatabase
    lateinit var usersRef: DatabaseReference

    private val PERMISSION_REQUEST_CODE = 200
    private var launcher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        username =  auth.currentUser!!.displayName.toString()

        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")
        loadUser()
        getNutrientsFromFirebase()

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK){
                    handleNutrientsData(result.data)
                }
            }

        onClickSearch()
        onClickDelete()
    }

    private fun onClickDelete() {
        binding.btnDelete.setOnClickListener {
            val databaseReference = FirebaseDatabase.getInstance().reference
            val currentDate = Calendar.getInstance()

            val query = databaseReference
                .child("users")
                .child(username)
                .child("meal")
                .child("${currentDate.get(Calendar.DAY_OF_MONTH)}:${ currentDate.get(Calendar.MONTH) + 1}:${currentDate.get(Calendar.YEAR)}")

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val lastChildKey = dataSnapshot.children.lastOrNull()?.key
                    lastChildKey?.let { key ->
                        query.child(key).removeValue()
                    }
                    getNutrientsFromFirebase()
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("item", "onCancelled", databaseError.toException())
                }
            })
        }
    }

    private fun loadUser() {
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
        Log.e("item", auth.currentUser!!.displayName.toString() )
    }

    private fun handleNutrientsData(data: Intent?) {
        if (data == null) {
            Log.d("MyLog", "Data is NULL")
            return
        }
        val nutrients = data.getSerializableExtra("Nutrients") as Nutrients
        getNutrientsFromFirebase()
        sendDataToFirebase(nutrients)
    }

    private fun getNutrientsFromFirebase() {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val currentDate = Calendar.getInstance()

        val query = databaseReference
            .child("users")
            .child(username)
            .child("meal")
            .child("${currentDate.get(Calendar.DAY_OF_MONTH)}:${ currentDate.get(Calendar.MONTH) + 1}:${currentDate.get(Calendar.YEAR)}")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (mealSnapshot in dataSnapshot.children) {
                    val dictionary = HashMap<String, Int>()
                    val meal = mealSnapshot.getValue(Nutrients::class.java)
                    meal?.let {
                        dictionary["calories"] =
                            ((dictionary["calories"] ?: 0) + it.calories!!).toInt()
                        dictionary["fat"] = ((dictionary["fat"] ?: 0) + it.fat!!).toInt()
                        dictionary["protein"] =
                            ((dictionary["protein"] ?: 0) + it.protein!!).toInt()
                    }
                    setBarChart(
                        dictionary.get("protein")!!.toFloat(),
                        dictionary.get("fat")!!.toFloat(),
                        (dictionary.get("calories")!! - (dictionary.get("fat")!! * 9.3 + dictionary.get("protein")!! * 4.1)/4.1).toString().split('.')[0].toFloat()
                    )
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun sendDataToFirebase(nutrients: Nutrients) {
        val userName = auth.currentUser!!.displayName.toString()
        val currentDate = Calendar.getInstance()

        val userDataRef = usersRef
            .child(userName)
            .child("meal")
            .child("${currentDate.get(Calendar.DAY_OF_MONTH)}:${ currentDate.get(Calendar.MONTH) + 1}:${currentDate.get(Calendar.YEAR)}")
            .child(usersRef.push().key ?: "blablabla")
        userDataRef.child("calories").setValue(nutrients.calories)
        userDataRef.child("protein").setValue(nutrients.protein)
        userDataRef.child("fat").setValue(nutrients.fat)
        userDataRef.child("carbs").setValue(
            (nutrients.calories !!- (nutrients.fat !!* 9.3 + nutrients.protein !!* 4.1)/4.1).toString().split('.')[0].toFloat()

        )
    }

    private fun onClickSearch() {
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            launcher!!.launch(intent)
        }
    }

    private fun setBarChart(protein: Float?, fat: Float?, carb: Float?) {

        val entries = ArrayList<BarEntry>()

        /*  x -  координаты для обозначения кол-ва данных; у - длина графика  */
        entries.add(BarEntry(3f, protein ?: 0f, "protein"))
        entries.add(BarEntry(2f, fat ?: 0f, "fat"))
        entries.add(BarEntry(1f, carb ?: 0f, "carb"))

        val barDataSet = BarDataSet(entries, "g")
        val data = BarData(barDataSet)
        binding.barChart.data = data // set the data and list of lables into chart
        barDataSet.color = this.getColor(R.color.purple)

        binding.barChart.animateY(500)
    }
}