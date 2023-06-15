package com.example.movieretrofit.firebase

import com.example.movieretrofit.data.Nutrients
import com.google.firebase.database.DatabaseReference
import java.util.*

class FirebaseBalance(private val firebase: Firebase) {

    fun updateBalanceInfo() {
        val balanceInfoRef = firebase.dateRef.child("balanceInfo")

        firebase.getTodayNutrients { dayNutrients ->
            val sumNutrients = Nutrients().getSum(dayNutrients)
            firebase.getUserDietFromFirebase { diet ->
                val cfNutrients = sumNutrients.getBalancedNutrientsInPercentage(diet)
                val isInBalance = cfNutrients.isInBounds(95f..105f)

                balanceInfoRef.child("isInBalance").setValue(isInBalance)

                getYesterdayBalanceCnt {
                    val newBalanceCnt = if (isInBalance) it + 1 else it
                    balanceInfoRef.child("balanceCnt").setValue(newBalanceCnt)
                    tryUpdateMaxBalanceCnt(newBalanceCnt)
                }
            }
        }
    }

    private fun tryUpdateMaxBalanceCnt(newBalanceCnt: Int) {
        getMaxBalanceCnt { maxBalanceCnt ->
            firebase.usernameRef.child("maxBalanceCnt")
                .setValue(Integer.max(maxBalanceCnt, newBalanceCnt))
        }
    }

    fun getMaxBalanceCnt(result: (balanceCnt: Int) -> Unit) {
        val maxBalanceCntRef = firebase.usernameRef.child("maxBalanceCnt")
        maxBalanceCntRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                result(snapshot.value.toString().toIntOrNull() ?: 0)
            }
        }
    }

    private fun getYesterdayBalanceCnt(result: (balanceCnt: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayRef = firebase.getDateRef(firebase.sdf.format(calendar.time))
        val balanceInfoRef = yesterdayRef.child("balanceInfo")
        balanceInfoRef.child("isInBalance").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val isInBalance = snapshot?.value as? Boolean
                if (isInBalance == false) result(0)
                else getBalanceCnt(balanceInfoRef.child("balanceCnt")) { result(it) }
            }
        }
    }

    fun getDayBalanceCnt(result: (balanceCnt: Int) -> Unit) {
        getBalanceCnt(firebase.dateRef.child("balanceInfo").child("balanceCnt")) { result(it) }
    }

    private fun getBalanceCnt(balanceCntRef: DatabaseReference, result: (balanceCnt: Int) -> Unit) {
        balanceCntRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                result(snapshot.value.toString().toIntOrNull() ?: 0)
            }
        }
    }
}