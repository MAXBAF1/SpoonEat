package com.example.movieretrofit.fragments.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.adapter.LastFoodsAdapter
import com.example.movieretrofit.charts.BarCharts
import com.example.movieretrofit.data.Diet
import com.example.movieretrofit.data.Food
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.databinding.FragmentHomeBinding
import com.example.movieretrofit.model.SharedViewModel
import com.github.mikephil.charting.data.BarData
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

        updateNutrients()
        onClickDelete()
        //updateNutrientsListener()
        setLastThreeFoods()
    }

    private fun setLastThreeFoods() {
        firebase.getDayFood(firebase.dateRef) { foods ->
            if (activity != null) {
                val reversedFoods = foods.reversed()
                when {
                    reversedFoods.size >= 3 -> {
                        val adapter =
                            LastFoodsAdapter(requireContext(), reversedFoods.subList(0, 3))
                        binding.lastFoodsRecyclerView.adapter = adapter
                    }
                    reversedFoods.size == 2 -> {
                        val adapter =
                            LastFoodsAdapter(requireContext(), reversedFoods.subList(0, 2))
                        binding.lastFoodsRecyclerView.adapter = adapter
                    }
                    reversedFoods.size == 1 -> {
                        val adapter = LastFoodsAdapter(requireContext(), listOf(reversedFoods[0]))
                        binding.lastFoodsRecyclerView.adapter = adapter
                    }
                    else -> {
                        val adapter = LastFoodsAdapter(requireContext(), listOf())
                        binding.lastFoodsRecyclerView.adapter = adapter
                    }
                }
            }
        }
    }

    fun updateNutrients() {
        firebase.getUserDietFromFirebase { diet ->
            firebase.getDayFood(firebase.dateRef) { foods ->
                Log.e("crash", "foods is $foods, nutrients is $nutrients")

                if (foods.isEmpty()) {
                    Log.e("crash", "isEmpty branch")
                    BarCharts().setEmptyBarChart(binding.barChart)
                } else {
                    nutrients = nutrients.getDaySum(foods)
                    setDataToTextView()
                    BarCharts().setBarChart(binding.barChart, nutrients, diet)
                    Log.e("crash", "setBarChart branch")
                }
            }
        }
    }

    private fun setDataToTextView() {
        binding.tvCalories.text = "Калории: ${nutrients.calories.roundToInt()} ккал."
        binding.tvProtein.text = "Белки: ${nutrients.protein.roundToInt()} гр."
        binding.tvFat.text = "Жиры: ${nutrients.fat.roundToInt()} гр."
        binding.tvCarbs.text = "Углеводы: ${nutrients.carb.roundToInt()} гр."

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
                    updateNutrients()
                    setLastThreeFoods()
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

            updateNutrients()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}