package com.chanho.loveday.dday

import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DDayViewModel: ViewModel() {

    fun getIndex(specialDates: ArrayList<String>): Int {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()

        var closestDateIndex = -1
        var minDifference = Long.MAX_VALUE

        for ((index, dateString) in specialDates.withIndex()) {
            val date = dateFormat.parse(dateString)
            if (date != null && date.after(currentDate)) {
                val difference = date.time - currentDate.time
                if (difference < minDifference) {
                    closestDateIndex = index
                    minDifference = difference
                }
            }
        }

        return closestDateIndex
    }
}