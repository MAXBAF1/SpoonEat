package com.example.movieretrofit.fragments.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Diet
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.databinding.FragmentHomeBinding
import com.example.movieretrofit.model.SharedViewModel
import com.example.movieretrofit.utils.Helper
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    val helper = Helper()

    lateinit var binding: FragmentHomeBinding
    lateinit var firebase: Firebase


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

        firebase.getUserDietFromFirebase { updateDiet(it) }
        firebase.getCurrentDayFoodDataFromFirebase { setBarChart(it) }
        firebase.getCurrentDayFoodDataFromFirebase { setDataToTextView(it) }


        onClickDelete()
        updateNutrients()
    }

    private fun setDataToTextView(foodData: List<Food>) {
        var totalNutrients = Nutrients()

        for (food in foodData) {
            totalNutrients = helper.countCurrentDaySum(food)
        }

        binding.tvCalories.text = "Калории: " + totalNutrients.calories
        binding.tvProtein.text = "Белки: " + totalNutrients.protein
        binding.tvFat.text = "Жиры: " + totalNutrients.fat
        binding.tvCarbs.text = "Углеводы: " + totalNutrients.carbs

        setUpUserPicture(binding.zaglushkaAvatar, binding.zaglushkaName)
    }

    private fun setUpUserPicture(imageView: ImageView, userName: TextView) {
        Glide.with(this)
            .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
            .into(imageView)
        userName.text = FirebaseAuth.getInstance().currentUser!!.displayName
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
                    firebase.getCurrentDayFoodDataFromFirebase { setBarChart(it) }
                    firebase.getCurrentDayFoodDataFromFirebase { setDataToTextView(it) }
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
            Log.d("MyLog", "updateNutrients")
            firebase.sendCurrentMealDataToFirebase(foodDataToSend = data)
            firebase.getUserDietFromFirebase { updateDiet(it) }
            firebase.getCurrentDayFoodDataFromFirebase { setBarChart(it) }


        }
    }

    private fun updateDiet(diet: Diet) {
        helper.nutrientsCoeff.protein = diet.proteinCoeff / 100
        helper.nutrientsCoeff.fat = diet.fatCoeff / 100
        helper.nutrientsCoeff.carbs = diet.carbsCoeff / 100
    }

    private fun setBarChart(dailyFood: List<Food>) {
        helper.countCoefficientCurrentDaySum(dailyFood)
        Log.e("item", "diet in setBarChart, proteinCf is ${helper.nutrientsCoeff.protein}")
        Log.e("item", "diet in setBarChart, carbsCf is ${helper.nutrientsCoeff.fat}")
        Log.e("item", "diet in setBarChart, fatCf is ${helper.nutrientsCoeff.carbs}")
        val entries = ArrayList<BarEntry>()

        entries.add(BarEntry(3f, helper.nutrientsCoeff.protein,"protein"))
        entries.add(BarEntry(2f, helper.nutrientsCoeff.fat, "fat"))
        entries.add(BarEntry(1f, helper.nutrientsCoeff.carbs, "carbs"))

        val barDataSet = BarDataSet(entries, "g")
        val data = BarData(barDataSet)
        binding.barChart.data = data // set the data and list of lables into chart
        if (context != null)
        //barDataSet.color = getColor(requireContext(), R.color.colorGradientEnd)
            barDataSet.color = getColor(requireContext(), R.color.colorPrimary)

        binding.barChart.animateY(0)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}