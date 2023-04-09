package com.example.movieretrofit.fragments.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Diet
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.databinding.FragmentHomeBinding
import com.example.movieretrofit.interfaces.AddFoodListener
import com.example.movieretrofit.model.SharedViewModel
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment(), AddFoodListener {
    var proteinCoeff = 1f
    var fatCoeff = 1f
    var carbsCoeff = 1f

    lateinit var binding: FragmentHomeBinding
    lateinit var firebase: Firebase
    private var launcher: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebase = Firebase()
        firebase.loadUser()
        firebase.getUserDietFromFirebase { getCoeffDiet(it) }
        firebase.getNutrientsFromFirebase { setBarChart(it) }

//        launcher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//                if (result.resultCode == AppCompatActivity.RESULT_OK) {
//                    handleNutrientsData(result.data)
//                }
//            }

        onClickDelete()
        updateNutrients()
    }

    private fun onClickDelete() {
        binding.btnDelete.setOnClickListener {
            val query = firebase.dateRef

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val lastChildKey = dataSnapshot.children.lastOrNull()?.key
                    lastChildKey?.let { key ->
                        query.child(key).removeValue()
                    }
                    firebase.getUserDietFromFirebase { getCoeffDiet(it) }
                    firebase.getNutrientsFromFirebase { setBarChart(it) }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("item", "onCancelled", databaseError.toException())
                }
            })
        }
    }

    private fun updateNutrients() {
        val viewModel: SharedViewModel by activityViewModels()
        viewModel.data.observe(viewLifecycleOwner) { data ->
            firebase.sendDataToFirebase(data)
            firebase.getUserDietFromFirebase { getCoeffDiet(it) }
            firebase.getNutrientsFromFirebase { setBarChart(it) }
        }
    }

    private fun getCoeffDiet(diet: Diet) {
        proteinCoeff = diet.proteinCoeff / 100
        fatCoeff = diet.fatCoeff / 100
        carbsCoeff = diet.carbsCoeff / 100
    }

    private fun setBarChart(nutrients: Nutrients) {
        Log.e("item", "diet in setBarChart, protetnCoeff is  $proteinCoeff")
        Log.e("item", "diet in setBarChart, carbsCoeff is  $carbsCoeff")
        Log.e("item", "diet in setBarChart, fatCoeff is  $fatCoeff")
        val entries = ArrayList<BarEntry>()
        val sum = nutrients.protein + nutrients.fat + nutrients.carbs

        entries.add(BarEntry(3f, nutrients.protein / sum / proteinCoeff, "protein"))
        entries.add(BarEntry(2f, nutrients.fat / sum / fatCoeff, "fat"))
        entries.add(BarEntry(1f, nutrients.carbs / sum / carbsCoeff, "carbs"))

        val barDataSet = BarDataSet(entries, "g")
        val data = BarData(barDataSet)
        binding.barChart.data = data // set the data and list of lables into chart
        barDataSet.color = getColor(requireContext(), R.color.colorGradientEnd)


        binding.barChart.animateY(0)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onNutrientsReceived(nutrients: Nutrients) {}

}