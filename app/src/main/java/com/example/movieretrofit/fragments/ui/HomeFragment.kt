package com.example.movieretrofit.fragments.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.charts.BarCharts
import com.example.movieretrofit.data.Food
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

        updateNutrients()
        onClickDelete()
        //updateNutrientsListener()
        setLastThreeFoods()
    }

    private fun setLastThreeFoods() {
        firebase.getDayFood(firebase.dateRef) { foods ->
            if (activity != null) {
                when {
                    foods.size >= 3 -> {
                        binding.textViewFirstFood.text = foods[foods.size - 3].name
                        binding.textViewSecondFood.text = foods[foods.size - 2].name
                        binding.textViewThirdFood.text = foods.last().name

                        Glide.with(requireContext())
                            .load(foods[foods.size - 3].image)
                            .into(binding.imageViewFirstFood)
                        Glide.with(requireContext())
                            .load(foods[foods.size - 2].image)
                            .into(binding.imageViewSecondFood)
                        Glide.with(requireContext())
                            .load(foods.last().image)
                            .into(binding.imageViewThirdFood)
                    }
                    foods.size == 2 -> {
                        binding.textViewFirstFood.text = foods[0].name
                        binding.textViewSecondFood.text = foods[1].name

                        Glide.with(requireContext())
                            .load(foods[0].image)
                            .into(binding.imageViewFirstFood)
                        Glide.with(requireContext())
                            .load(foods[1].image)
                            .into(binding.imageViewSecondFood)

                        binding.textViewThirdFood.text = ""
                        binding.imageViewThirdFood.setImageResource(android.R.color.transparent)
                    }
                    foods.size == 1 -> {
                        binding.textViewFirstFood.text = foods.last().name

                        Glide.with(requireContext())
                            .load(foods.last().image)
                            .into(binding.imageViewFirstFood)

                        binding.textViewSecondFood.text = ""
                        binding.imageViewSecondFood.setImageResource(android.R.color.transparent)
                        binding.textViewThirdFood.text = ""
                        binding.imageViewThirdFood.setImageResource(android.R.color.transparent)
                    }
                    else -> {
                        binding.textViewFirstFood.text = ""
                        binding.imageViewFirstFood.setImageResource(android.R.color.transparent)
                        binding.textViewSecondFood.text = ""
                        binding.imageViewSecondFood.setImageResource(android.R.color.transparent)
                        binding.textViewThirdFood.text = ""
                        binding.imageViewThirdFood.setImageResource(android.R.color.transparent)
                    }
                }
            }
        }
    }

//    private fun deleteFood(food: Food) {
//        firebase.deleteFood(firebase.dateRef, food) {
//            setLastThreeFoods()
//        }
//    }

    private fun updateNutrients() {
        firebase.getUserDietFromFirebase { diet ->
            firebase.getDayFood(firebase.dateRef) { foods ->
                nutrients = nutrients.getDaySum(foods)
                setDataToTextView()
                BarCharts().setBarChart(binding.barChart, nutrients, diet)
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
                    val lastChildKey = dataSnapshot.children.lastOrNull()?.key
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

    private fun updateNutrientsListener() {
        val viewModel: SharedViewModel by activityViewModels()
        viewModel.data.observe(viewLifecycleOwner) { data ->
            Log.d("MyLog", "updateNutrients")
            firebase.sendCurrentMealDataToFirebase(data)

            updateNutrients()
            Log.e("watcher", "3 in Home Fragment updateNutrientsListener")

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}