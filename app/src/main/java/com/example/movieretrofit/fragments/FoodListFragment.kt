package com.example.movieretrofit.fragments

<<<<<<< HEAD
=======
import android.content.Context
>>>>>>> 1ef3a416805247a36f1ec45aceb99df055fa17a8
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
=======
import android.widget.Button
import android.widget.Toast
>>>>>>> 1ef3a416805247a36f1ec45aceb99df055fa17a8
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
<<<<<<< HEAD
import com.example.movieretrofit.adapter.FoodAdapter
import com.example.movieretrofit.adapter.FoodClickListener
import com.example.movieretrofit.adapter.FoodTextInputEditTextAdapter
import com.example.movieretrofit.databinding.FragmentFoodListBinding
=======
import com.example.movieretrofit.MainActivity
import com.example.movieretrofit.R
import com.example.movieretrofit.SearchActivity
import com.example.movieretrofit.adapter.FoodAdapter
import com.example.movieretrofit.adapter.FoodTextInputEditTextAdapter
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.databinding.FragmentFoodListBinding
import com.example.movieretrofit.interfaces.AddFoodListener
import com.example.movieretrofit.interfaces.FoodClickListener
>>>>>>> 1ef3a416805247a36f1ec45aceb99df055fa17a8
import com.example.movieretrofit.model.FoodViewModel
import com.example.retrofittraining.Utils.inputCheck
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

<<<<<<< HEAD
class FoodListFragment : Fragment(), FoodClickListener {
=======
class FoodListFragment : Fragment(), FoodClickListener, AddFoodListener {
>>>>>>> 1ef3a416805247a36f1ec45aceb99df055fa17a8
    lateinit var foodViewModel: FoodViewModel
    var binding: FragmentFoodListBinding? = null

    val adapterTextInput = FoodTextInputEditTextAdapter(this)
<<<<<<< HEAD
    private val adapter = FoodAdapter()
=======
    private val adapter = FoodAdapter(this)
>>>>>>> 1ef3a416805247a36f1ec45aceb99df055fa17a8

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFoodListBinding.inflate(inflater, container, false)
        this.binding = binding

        //Adapter for food
        val recyclerView = binding.recyclerFood
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //Adapter for foodTextInputList
        val recyclerViewTextInput = binding.recyclerwatchlist
        recyclerViewTextInput.adapter = adapterTextInput
        recyclerViewTextInput.layoutManager = LinearLayoutManager(requireContext())

        binding.tvFood.addTextChangedListener(simpleTextWatcher)

        //ViewModel
        foodViewModel = ViewModelProvider(this)[FoodViewModel::class.java]

        binding.textInputLayout.setEndIconOnClickListener {
            lifecycleScope.launch {

                binding.progressBar.visibility = View.VISIBLE

                val tvFoodEditText = binding.tvFood.text.toString()
                if (inputCheck(tvFoodEditText)) {
                    val food = foodViewModel.getFoodRecipe(tvFoodEditText)
                    Log.d("FOOD", "$food")
                    adapter.setData(food)
                    binding.recyclerFood.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE

                    if (food.isNotEmpty()) {
                        binding.recyclerFood.visibility = View.VISIBLE
                        binding.listsearch.visibility = View.GONE
                        binding.errorlist.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                    } else binding.errorlist.visibility = View.VISIBLE
                    Log.d("FOOD", "$food")
                    adapter.setData(food)
                } else {
                    binding.errorlist.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerFood.visibility = View.GONE
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // TextWatcher
    private val simpleTextWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding!!.listsearch.visibility = View.VISIBLE
        }

        override fun afterTextChanged(s: Editable?) {
            lifecycleScope.launch {
                flow {
                    try {
                        val text = s.toString()
                        delay(100)
                        if (adapterTextInput.foodList.isEmpty()) delay(300)
                        binding!!.progressBar.visibility = View.VISIBLE
                        val food = foodViewModel.getFoodRecipe(text)
                        emit(adapterTextInput.setData(food.takeLast(food.size - 1)))
                        binding!!.progressBar.visibility = View.GONE
                        Log.d("textWatcher", text)
                        if (s!!.isEmpty()) binding!!.listsearch.visibility = View.GONE
                    } catch (_: Exception) {
                    }
                }.collect()
            }
        }
    }

    // Recycler onItemClick
    override fun onFoodClickListener(food: String) {
        lifecycleScope.launch {
            val tvFoodEditText = binding!!.tvFood.text.toString()
            if (inputCheck(tvFoodEditText)) {
                binding!!.tvFood.setText(food)
                val food = foodViewModel.getFoodRecipe(food)
                adapter.setData(food.take(1))
                binding!!.recyclerFood.visibility = View.VISIBLE
                binding!!.progressBar.visibility = View.GONE
                binding!!.listsearch.visibility = View.GONE

                if (food.isNotEmpty()) {
                    binding!!.recyclerFood.visibility = View.VISIBLE
                    binding!!.progressBar.visibility = View.GONE
                } else binding!!.errorlist.visibility = View.VISIBLE
                Log.d("FOOD", "$food")
                adapter.setData(food.take(1))
            } else {
                binding!!.errorlist.visibility = View.VISIBLE
                binding!!.progressBar.visibility = View.GONE
                binding!!.recyclerFood.visibility = View.GONE
            }
        }
    }
<<<<<<< HEAD
=======

    override fun onNutrientsReceived(nutrients: Nutrients) {
        val activity = requireActivity() as SearchActivity
        activity.handleNutrientsData(nutrients)
        // закрываем фрагмент
        activity.onBackPressed()
    }
>>>>>>> 1ef3a416805247a36f1ec45aceb99df055fa17a8
}