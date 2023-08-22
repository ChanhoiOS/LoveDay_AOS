package com.chanho.loveday

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.applikeysolutions.cosmocalendar.selection.OnDaySelectedListener
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager
import com.applikeysolutions.cosmocalendar.selection.SingleSelectionManager
import com.chanho.loveday.databinding.ActivitySetDayBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class SetDayActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetDayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetDayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calendarView.selectionManager = SingleSelectionManager(OnDaySelectedListener {
            val weekYear = binding.calendarView.selectedDays[0].calendar.weekYear
            Log.d("selected: ", weekYear.toString())

            val month = binding.calendarView.selectedDays[0].calendar[2] + 1
            Log.d("month: ", month.toString())

            val dayNumber = binding.calendarView.selectedDays[0].dayNumber
            Log.d("dayNumber: ", dayNumber.toString())

            val formattedDate = String.format("%04d-%02d-%02d", weekYear, month, dayNumber)
            Log.d("formattedDate", formattedDate)

        })
    }
}

