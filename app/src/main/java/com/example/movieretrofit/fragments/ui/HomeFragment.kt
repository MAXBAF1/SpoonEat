package com.example.movieretrofit.fragments.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.adapter.LastFoodsAdapter
import com.example.movieretrofit.charts.BarCharts
import com.example.movieretrofit.charts.calendarRow.ScrollingCalendarRow
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.databinding.FragmentHomeBinding
import com.example.movieretrofit.model.SharedViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.utils.DateUtils
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebase: Firebase
    private var monthCounter = 5;
    private lateinit var months: Array<String>;

    var nutrients = Nutrients()
    var barCharts = BarCharts()
    lateinit var dateRef: DatabaseReference

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
        dateRef = firebase.mealRef.child(firebase.date)
        binding.lastFoodsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        months = arrayOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
        binding.tvMonth.text = months[abs(monthCounter % 12)]
        setCalendar()
    }

    private fun setCalendar() {
        val scrollingCalendar = ScrollingCalendarRow(binding.mainSingleRowCalendar)
        val myCalendarChangesObserver = object : CalendarChangesObserver {
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                val tvDateText =
                    "${DateUtils.getDayNumber(date)} ${DateUtils.getMonthName(date)}, ${
                        DateUtils.getDayName(date)}"

                binding.tvDate.text = tvDateText
                dateRef = firebase.mealRef.child(
                    "${DateUtils.getDayNumber(date)}:${
                        DateUtils.getMonthNumber(date)
                    }:${DateUtils.getYear(date)}"
                )
                updateViews()
                super.whenSelectionChanged(isSelected, position, date)
            }
        }
        scrollingCalendar.initCalendar(myCalendarChangesObserver)

        binding.btnLeft.setOnClickListener {
            monthCounter -= 1
            binding.tvMonth.text = months[abs(monthCounter % 12)]
            scrollingCalendar.setPreviousMonthDates()
        }
        binding.btnRight.setOnClickListener {
            monthCounter += 1
            binding.tvMonth.text = months[abs(monthCounter % 12)]
            scrollingCalendar.setNextMonthDates()
        }
    }

    fun updateViews() {
        setLastFoods()
        firebase.getUserDietFromFirebase { diet ->
            firebase.getDayFood(dateRef) { foods ->
                nutrients = nutrients.getDaySum(foods)
                setDataToTextView()
                context?.let {
                    barCharts.setBarChart(it, binding.barChart,nutrients, diet)
                }
            }
        }
    }

    private fun setLastFoods() {
        firebase.getDayFood(dateRef) { foods ->
            if (activity != null) {
                val adapter = LastFoodsAdapter(requireContext(), foods.reversed())
                binding.lastFoodsRecyclerView.adapter = adapter
            }
        }
    }

    private fun setDataToTextView() {
        val caloriesText = "${nutrients.calories.roundToInt()}"
        val proteinText = "Белки: ${nutrients.protein.roundToInt()} гр."
        val fatText = "Жиры: ${nutrients.fat.roundToInt()} гр."
        val carbText = "Углеводы: ${nutrients.carb.roundToInt()} гр."
        binding.tvCalories.text = caloriesText
        binding.tvProtein.text = proteinText
        binding.tvFat.text = fatText
        binding.tvCarbs.text = carbText
    }


    fun updateNutrientsListener() {
        val viewModel: SharedViewModel by activityViewModels()
        viewModel.data.observe(viewLifecycleOwner) { data ->
            Log.d("MyLog", "updateNutrients")
            firebase.sendCurrentMealDataToFirebase(data)

            updateViews()
        }
    }

    fun deleteItemByIndex(numToDelete: Int) {
        var firebase = Firebase()
        firebase.loadUser()
        var dateRef = firebase.mealRef.child(firebase.date)

        var counter = numToDelete
        dateRef.orderByKey().limitToLast(numToDelete).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnap in snapshot.children) {
                    if (numToDelete == counter){
                        childSnap.ref.removeValue()
                        updateViews()
                        break
                    }
                    else{
                        counter --
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("onDeleteClick", "onCancelled", error.toException())
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}