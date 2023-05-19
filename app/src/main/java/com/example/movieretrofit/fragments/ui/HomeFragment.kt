package com.example.movieretrofit.fragments.ui

import android.graphics.Color
import android.os.Build
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
import com.example.movieretrofit.R
import com.example.movieretrofit.adapter.LastFoodsAdapter
import com.example.movieretrofit.charts.BarCharts
import com.example.movieretrofit.charts.CalendarRow
import com.example.movieretrofit.data.Nutrients
import com.example.movieretrofit.databinding.FragmentHomeBinding
import com.example.movieretrofit.model.SharedViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import java.util.*
import kotlin.math.roundToInt
import com.github.mikephil.charting.utils.Utils.init
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.calendar_item.view.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebase: Firebase

    private val calendar = Calendar.getInstance()
    private var currentMonth = 0

    var nutrients = Nutrients()
    var calendarRow = CalendarRow()
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
        binding.lastFoodsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        updateViews()
        onClickDelete()
        //updateNutrientsListener()
        setLastFoods()
        initCalendar()
    }

    private fun setLastFoods() {
        firebase.getDayFood(dateRef) { foods ->
            if (activity != null) {
                val adapter = LastFoodsAdapter(requireContext(), foods.reversed())
                binding.lastFoodsRecyclerView.adapter = adapter
            }
        }
    }

    fun updateViews() {
        setLastFoods()
        firebase.getUserDietFromFirebase { diet ->
            firebase.getDayFood(dateRef) { foods ->
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
        val caloriesText = "${nutrients.calories.roundToInt()}"
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
            dateRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val lastChildKey = dataSnapshot.children.lastOrNull()!!.key
                    lastChildKey?.let { key ->
                        dateRef.child(key).removeValue()
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

    private fun initCalendar() {
        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        val myCalendarViewManager = object :
            CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                val cal = Calendar.getInstance()
                cal.time = date
                return if (isSelected)
                    when (cal[Calendar.DAY_OF_WEEK]) {
                        Calendar.MONDAY -> R.layout.first_special_selected_calendar_item
                        Calendar.WEDNESDAY -> R.layout.second_special_selected_calendar_item
                        Calendar.FRIDAY -> R.layout.third_special_selected_calendar_item
                        else -> R.layout.selected_calendar_item
                    }
                else
                    when (cal[Calendar.DAY_OF_WEEK]) {
                        Calendar.MONDAY -> R.layout.first_special_calendar_item
                        Calendar.WEDNESDAY -> R.layout.second_special_calendar_item
                        Calendar.FRIDAY -> R.layout.third_special_calendar_item
                        else -> R.layout.calendar_item
                    }
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                holder.itemView.tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)
            }
        }

        val myCalendarChangesObserver = object :
            CalendarChangesObserver {
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                binding.tvDate.text = "${DateUtils.getMonthName(date)}, ${DateUtils.getDayNumber(date)}  "
                Log.e("item", "${DateUtils.getDayNumber(date)}:${DateUtils.getMonthNumber(date)}:${DateUtils.getYear(date)}")
                binding.tvDay.text = DateUtils.getDayName(date)
                dateRef = firebase.mealRef.child("${DateUtils.getDayNumber(date)}:${DateUtils.getMonthNumber(date)}:${DateUtils.getYear(date)}")
                Log.e("item", "${dateRef}")
                updateViews()
                super.whenSelectionChanged(isSelected, position, date)
            }
        }

        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                val cal = Calendar.getInstance()
                cal.time = date
                return when (cal[Calendar.DAY_OF_WEEK]) {
                    Calendar.SATURDAY -> true
                    else -> true
                }
            }
        }

        val singleRowCalendar = binding.mainSingleRowCalendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            setDates(calendarRow.getFutureDatesOfCurrentMonth())
            init()
        }

        binding.btnRight.setOnClickListener {
            singleRowCalendar.setDates(calendarRow.getDatesOfNextMonth())
        }

        binding.btnLeft.setOnClickListener {
            singleRowCalendar.setDates(calendarRow.getDatesOfPreviousMonth())
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}