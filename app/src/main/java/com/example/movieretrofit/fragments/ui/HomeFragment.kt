package com.example.movieretrofit.fragments.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.adapter.LastFoodsAdapter
import com.example.movieretrofit.charts.BarCharts
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.databinding.FragmentHomeBinding
import com.example.movieretrofit.model.SharedViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlin.math.roundToInt

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebase: Firebase

    var nutrients = Nutrients()

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

        binding.lastFoodsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        updateViews()
        onClickDelete()
        //updateNutrientsListener()
        setLastFoods()
    }

    private fun setLastFoods() {
        firebase.getDayFood(firebase.dateRef) { foods ->
            if (activity != null) {
                val adapter = LastFoodsAdapter(requireContext(), foods.reversed())
                binding.lastFoodsRecyclerView.adapter = adapter
            }
        }
    }

    fun updateViews() {
        setLastFoods()
        firebase.getUserDietFromFirebase { diet ->
            firebase.getDayFood(firebase.dateRef) { foods ->
                Log.e("crash", "foods is $foods, nutrients is $nutrients")

                if (foods.isEmpty()) {
                    Log.e("crash", "isEmpty branch")
                    BarCharts().setEmptyBarChart(binding.barChart)
                } else {
                    nutrients = nutrients.getDaySum(foods)
                    setDataToTextView()
                    context?.let { BarCharts().setBarChart(it, binding.barChart, nutrients, diet) }
                    Log.e("crash", "setBarChart branch")
                }
            }
        }
    }

    private fun setDataToTextView() {
        val caloriesText = "Калории: ${nutrients.calories.roundToInt()} ккал."
        val proteinText = "Белки: ${nutrients.protein.roundToInt()} гр."
        val fatText = "Жиры: ${nutrients.fat.roundToInt()} гр."
        val carbText = "Углеводы: ${nutrients.carb.roundToInt()} гр."
        binding.tvCalories.text = caloriesText
        binding.tvProtein.text = proteinText
        binding.tvFat.text = fatText
        binding.tvCarbs.text = carbText
    }

    private fun onClickDelete() {
        binding.btnDelete.setOnClickListener {
            val query = firebase.dateRef

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val lastChildKey = dataSnapshot.children.lastOrNull()!!.key
                    lastChildKey?.let { key ->
                        query.child(key).removeValue()
                    }
                    updateViews()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("item", "onCancelled", databaseError.toException())
                }
            })
        }
    }

    fun updateNutrientsListener() {
        val viewModel: SharedViewModel by activityViewModels()
        viewModel.data.observe(viewLifecycleOwner) { data ->
            Log.d("MyLog", "updateNutrients")
            firebase.sendCurrentMealDataToFirebase(data)

            updateViews()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}