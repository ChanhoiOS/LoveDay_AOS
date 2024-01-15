package com.chanho.loveday.main

import androidx.lifecycle.ViewModel
import com.chanho.loveday.NetworkManager
import java.util.*
import java.util.concurrent.TimeUnit

class MainViewModel: ViewModel() {

    fun getDDay(year: Int, month: Int, day: Int): String {
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
        val differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis) + 1

        return differenceInDays.toString()
    }

    fun accessHistory(data: HashMap<String, Any>) {
        NetworkManager.accessHistory(data, {

        }) {

        }

    }
}