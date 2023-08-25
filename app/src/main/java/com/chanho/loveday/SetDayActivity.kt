package com.chanho.loveday

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.applikeysolutions.cosmocalendar.selection.OnDaySelectedListener
import com.applikeysolutions.cosmocalendar.selection.SingleSelectionManager
import com.chanho.loveday.databinding.ActivitySetDayBinding
import java.text.SimpleDateFormat

import java.util.*
import java.util.concurrent.TimeUnit

class SetDayActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetDayBinding

    private var preferences: SharedPreferences? = null

    var datingDay = ""
    var selectedYear = 2023
    var selectedMonth = 1
    var selectedDay = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetDayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = getSharedPreferences("setDDay", MODE_PRIVATE);

        binding.calendarView.selectionManager = SingleSelectionManager(OnDaySelectedListener {
            val year = binding.calendarView.selectedDays[0].calendar.weekYear
            selectedYear = year
            val month = binding.calendarView.selectedDays[0].calendar[2] + 1
            selectedMonth = month
            val day = binding.calendarView.selectedDays[0].dayNumber
            selectedDay = day

            val formattedDate = String.format("%04d-%02d-%02d", year, month, day)
            datingDay = formattedDate
        })

        binding.bottomButton.setOnClickListener {
            setSpecialDay()
        }
    }

    fun setSpecialDay() {
        val setEditor: SharedPreferences.Editor? = preferences?.edit()
        setEditor?.putBoolean("isSet", true)

        setEditor?.putString("datingDay", datingDay)
        setEditor?.commit()

        getDDay(selectedYear, selectedMonth, selectedMonth)
        getSpecialInfo(datingDay)
    }

    fun getDDay(year: Int, month: Int, day: Int) {
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

        val setEditor: SharedPreferences.Editor? = preferences?.edit()
        setEditor?.putLong("ingDay", differenceInDays)
        setEditor?.commit()
    }

    fun getSpecialInfo(dday: String) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        val startDate: Date = dateFormatter.parse(dday) ?: return

        val calendar = Calendar.getInstance()
        val specialDDayCheck: MutableList<Map<String, Int>> = mutableListOf()

        // 100일 단위의 기념일 추출
        for (day in 0..7200 step 100) {
            val specialDate: Date
            if (day == 0) {
                specialDate = startDate
            } else {
                specialDate = Calendar.getInstance().apply {
                    time = startDate
                    add(Calendar.DAY_OF_YEAR, day - 1)
                }.time
            }

            val specialStr = dateFormatter.format(specialDate)
            specialDDayCheck.add(mapOf(specialStr to day))
        }

        // 1년 단위의 기념일 추출
        for (year in 1..20) {
            val anniversaryDate: Date = Calendar.getInstance().apply {
                time = startDate
                add(Calendar.YEAR, year)
            }.time

            val anniversaryStr = dateFormatter.format(anniversaryDate)
            val days = year * 365
            if (year in 1..3) {
                val insertIndex = (year - 1) * 5 + year
                specialDDayCheck.add(insertIndex, mapOf(anniversaryStr to days))
            } else if (year in 4..20) {
                specialDDayCheck.add(mapOf(anniversaryStr to days))
            }
        }

        getValue(specialDDayCheck)
    }

    fun getValue(specialDay: List<Map<String, Int>>) {
        val specialDate: MutableList<String> = mutableListOf()
        val specialDayName: MutableList<String> = mutableListOf()

        for (day in specialDay) {
            for ((key, value) in day) {
                specialDate.add(key)
                when {
                    value == 0 -> specialDayName.add("만난 날")
                    value % 365 == 0 -> {
                        val year = value / 365
                        specialDayName.add("${year}주년")
                    }
                    else -> specialDayName.add("${value}일")
                }
            }
        }

        saveSpecialDay(specialDate, specialDayName)
    }

    fun saveSpecialDay(specialDate: List<String>, specialDayName: List<String>) {
        // 저장 로직을 여기에 구현
        Log.d("specialDate:: ", specialDate.toString())
        Log.d("specialDayName:: ", specialDayName.toString())
        val getKey = preferences?.getString("privateKey", "") ?: ""

        println("getKey: $getKey")

        for (i in specialDate.indices) {
            val param = HashMap<String, Any>()
            param["specialDate"] = specialDate[i]
            param["content"] = specialDayName[i]
            param["writer"] = getKey
            saveCalendar(param)
        }
    }

    fun saveCalendar(data: HashMap<String, Any>) {
        val url = "http://52.78.124.184:8080/api/calendar"
        NetworkManager.postCalendarRequest(data,
            {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            },
            {
                // 실패 처리 로직을 여기에 작성합니다.
            })
    }
}

