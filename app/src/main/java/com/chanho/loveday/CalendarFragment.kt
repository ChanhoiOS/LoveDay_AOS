package com.chanho.loveday

import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chanho.loveday.databinding.FragmentCalendarBinding
import com.chanho.loveday.databinding.FragmentDDayBinding
import com.chanho.loveday.model.CalendarModel
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentCalendarBinding
    private var calendarModelData: List<CalendarModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater, container, false )

        binding.calendarManageView.setOnDateChangedListener(object : OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                // 날짜 클릭 이벤트 처리
                if (selected) {

                    val year = date.year.toString()

                    val month = if (date.month < 10) {
                        "0${date.month}"
                    } else {
                        date.month.toString()
                    }

                    val day = if (date.day < 10) {
                        "0${date.day}"
                    } else {
                        date.day.toString()
                    }

                    var selectedDate = "$year-$month-$day"

                    val contentForTargetDate = calendarModelData?.find { calendarModel ->
                        calendarModel.specialDate == selectedDate
                    }?.content
            println("contentForTargetDate: $contentForTargetDate")
                }
            }
        })

        setData()

        return binding.root
    }

    private fun setData() {
        val param = HashMap<String, Any>()
        param["writer"] = "inbwvv"
        fetchData(param)
    }

    private fun fetchData(param: HashMap<String, Any>) {
        NetworkManager.getCalendar(param, { data ->
            if (data != null) {
                calendarModelData = data
                addSpecialDateDecorators(data)
            } else {
                // 데이터 가져오기 실패
            }
        })
    }

    fun addSpecialDateDecorators(calendarModels: List<CalendarModel>) {
        val specialDates = extractSpecialDates(calendarModels)
        val eventDecorator = EventDecorator(Color.parseColor("#0E406B"), specialDates)
        binding.calendarManageView.addDecorator(eventDecorator)
    }

    fun extractSpecialDates(calendarModels: List<CalendarModel>): Set<CalendarDay> {
        val specialDates = mutableSetOf<CalendarDay>()

        for (model in calendarModels) {
            val specialDate = model.specialDate
            if (specialDate != null) {
                val dateParts = specialDate.split("-")
                if (dateParts.size == 3) {
                    val year = dateParts[0].toInt()
                    val month = dateParts[1].toInt()
                    val day = dateParts[2].toInt()
                    specialDates.add(CalendarDay.from(year, month, day))
                }
            }
        }

        return specialDates
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CalendarFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

class SaturdayDecorator : DayViewDecorator {

    private val calendar = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay): Boolean {
//        day.copyTo(calendar)
        val weekDay: Int = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SATURDAY
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(Color.parseColor("#87CEFA")))
    }
}

class EventDecorator() : DayViewDecorator {

    private var color = 0
    private lateinit var dates : HashSet<CalendarDay>

    constructor(color: Int, dates: Collection<CalendarDay>) : this() {
        this.color=color
        this.dates=HashSet(dates)
    }

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(DotSpan(10F, color))
    }
}