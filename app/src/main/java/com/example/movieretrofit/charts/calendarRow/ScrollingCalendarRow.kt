package com.example.movieretrofit.charts.calendarRow

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieretrofit.R
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.android.synthetic.main.calendar_item.view.*
import java.util.*

class ScrollingCalendarRow(private var calendarView: SingleRowCalendar) {
    private var currentMonth = 0
    private var calendarRow = CalendarRow()
    private lateinit var calendar: Calendar

    fun initCalendar(observer: CalendarChangesObserver) {
        calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        val myCalendarViewManager = object :
            CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                calendar.time = date
                return if (isSelected)
                    R.layout.selected_calendar_item
                else R.layout.calendar_item
                   /* when (calendar[Calendar.DAY_OF_WEEK]) {
                        Calendar.MONDAY -> R.layout.first_special_selected_calendar_item
                        Calendar.WEDNESDAY -> R.layout.second_special_selected_calendar_item
                        Calendar.FRIDAY -> R.layout.third_special_selected_calendar_item
                        else -> R.layout.selected_calendar_item
                    }
                else
                    when (calendar[Calendar.DAY_OF_WEEK]) {
                        Calendar.MONDAY -> R.layout.first_special_calendar_item
                        Calendar.WEDNESDAY -> R.layout.second_special_calendar_item
                        Calendar.FRIDAY -> R.layout.third_special_calendar_item
                        else -> R.layout.calendar_item
                    }*/
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

        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                calendar.time = date
                return when (calendar[Calendar.DAY_OF_WEEK]) {
                    Calendar.SATURDAY -> true
                    else -> true
                }
            }
        }

        calendarView.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = observer
            calendarSelectionManager = mySelectionManager
            setDates(calendarRow.getFutureDatesOfCurrentMonth())
            init()
        }
        val today = calendar.get(Calendar.DAY_OF_MONTH) - 1
        calendarView.select(today)

        calendarView.post {
            val layoutManager = calendarView.layoutManager as LinearLayoutManager
            val offset = calendarView.width / 2 - calendarView.getChildAt(0).width / 2
            layoutManager.scrollToPositionWithOffset(today, offset)
        }
    }

    fun setNextMonthDates() = calendarView.setDates(calendarRow.getDatesOfNextMonth())
    fun setPreviousMonthDates() = calendarView.setDates(calendarRow.getDatesOfPreviousMonth())
}