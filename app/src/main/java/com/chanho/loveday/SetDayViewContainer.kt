package com.chanho.loveday

import android.view.View
import android.widget.TextView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.ViewContainer

// 날짜를 표시하는 영역 Container 클래스
class DayViewContainer(view: View) : ViewContainer(view) {
    val monthText: TextView = view.findViewById(R.id.tv_month)
    val dateText: TextView = view.findViewById(R.id.tv_date)
    val dayText: TextView = view.findViewById(R.id.tv_day)
    lateinit var day: CalendarDay

    init {
        view.setOnClickListener {
            // 날짜 선택 시 처리 정의

        }


    }

    fun bind(day: CalendarDay) {
        this.day = day
        monthText.text = day.date.toString()//monthFormatter.print(DateTime(day.date.toString()))
        dateText.text = day.date.toString()
        dayText.text = day.date.toString()

        //val dayWidth = binding.calendarView.daySize.width
        //dateText.layoutParams = LinearLayout.LayoutParams(dayWidth, dayWidth)

        //if (day.date == selectedDate) {
            //dateText.setBackgroundResource(R.drawable.calendar_selected_bg)
        //} else {
            //dateText.setBackgroundResource(R.color.col_transparency)
        //}
    }
}


// 날짜 binder 연결
//binding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
//    override fun create(view: View) = DayViewContainer(view)
//    override fun bind(container: DayViewContainer, day: CalendarDay) {
//        container.day = day
//        container.bind(day)
//    }
//}