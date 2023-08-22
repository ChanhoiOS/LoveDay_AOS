package com.chanho.loveday

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.applikeysolutions.cosmocalendar.selection.OnDaySelectedListener
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager
import com.applikeysolutions.cosmocalendar.selection.SingleSelectionManager
import com.chanho.loveday.databinding.ActivitySetDayBinding
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

class SetDayActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetDayBinding
    val today = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetDayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calendarView.selectionManager = SingleSelectionManager(OnDaySelectedListener {
            val year = binding.calendarView.selectedDays[0].calendar.weekYear
            Log.d("selected: ", year.toString())

            val month = binding.calendarView.selectedDays[0].calendar[2] + 1
            Log.d("month: ", month.toString())

            val day = binding.calendarView.selectedDays[0].dayNumber
            Log.d("dayNumber: ", day.toString())

            val formattedDate = String.format("%04d-%02d-%02d", year, month, day)
            Log.d("formattedDate", formattedDate)


            val selectedCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1) // 0부터 시작하는 월을 반영하여 -1 해줍니다.
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val differenceInMillis = today.timeInMillis - selectedCalendar.timeInMillis
            val differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis) + 1 // 사귄 첫 날을 1일로 처리합니다.

            Log.d("differenceInDays", differenceInDays.toString())

        })
    }
}

