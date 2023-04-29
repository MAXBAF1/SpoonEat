package com.example.movieretrofit.utils

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class FirebaseUtils {

    companion object {
        fun getMealsForDateRange(
            username: String,
            startDate: Date,
            endDate: Date,
            listener: ValueEventListener
        ) {
            val sdf = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())
            val usersRef = FirebaseDatabase.getInstance().getReference("users")

            val startCalendar = Calendar.getInstance()
            startCalendar.time = startDate

            val endCalendar = Calendar.getInstance()
            endCalendar.time = endDate

            while (startCalendar <= endCalendar) {
                val mealsRef = usersRef.child(username)
                    .child("meal")
                    .child(sdf.format(startCalendar.time))

                mealsRef.addValueEventListener(listener)
                startCalendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        }
    }
}
