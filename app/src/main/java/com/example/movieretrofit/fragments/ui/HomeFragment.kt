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
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.databinding.FragmentHomeBinding
import com.example.movieretrofit.model.SharedViewModel
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var firebase: Firebase

    private var nutrientsWithCf = Nutrients()
    private var nutrients = Nutrients()

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

        updateNutrients()
        onClickDelete()
        updateNutrientsListener()
    }

    private fun updateNutrients() {
        firebase.getUserDietFromFirebase { diet ->
            firebase.getCurrentDayFoodDataFromFirebase { foods ->
                nutrients = nutrients.getCurrentDaySum(foods)
                nutrientsWithCf = nutrientsWithCf.getNutrientsWithCf(nutrients, diet)
                setDataToTextView()
                setBarChart()
            }
        }
    }

    private fun setDataToTextView() {
        binding.tvCalories.text = "Калории: " + nutrients.calories
        binding.tvProtein.text = "Белки: " + nutrients.protein
        binding.tvFat.text = "Жиры: " + nutrients.fat
        binding.tvCarbs.text = "Углеводы: " + nutrients.carb

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
                    
                    updateNutrients()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("item", "onCancelled", databaseError.toException())
                }
            })
        }
    }

    private fun updateNutrientsListener() {
        val viewModel: SharedViewModel by activityViewModels()
        viewModel.data.observe(viewLifecycleOwner) { data ->
            Log.d("MyLog", "updateNutrients")
            firebase.sendCurrentMealDataToFirebase(data)

            updateNutrients()
        }
    }

    private fun setBarChart() {
        Log.e("item", "diet in setBarChart, proteinWithCf is ${nutrientsWithCf.protein}")
        Log.e("item", "diet in setBarChart, carbsWithCf is ${nutrientsWithCf.fat}")
        Log.e("item", "diet in setBarChart, fatWithCf is ${nutrientsWithCf.carb}")
        val entries = ArrayList<BarEntry>()

        entries.add(BarEntry(3f, nutrientsWithCf.protein,"protein"))
        entries.add(BarEntry(2f, nutrientsWithCf.fat, "fat"))
        entries.add(BarEntry(1f, nutrientsWithCf.carb, "carbs"))

        val barDataSet = BarDataSet(entries, "g")
        val data = BarData(barDataSet)
        binding.barChart.data = data // set the data and list of lables into chart
        if (context != null)
            barDataSet.color = getColor(requireContext(), R.color.colorPrimary)

        binding.barChart.animateY(0)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}