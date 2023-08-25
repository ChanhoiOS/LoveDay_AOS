package com.chanho.loveday

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.chanho.loveday.databinding.FragmentCalendarBinding
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
    private var specialDateName: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentCalendarBinding
    private var calendarModelData: List<CalendarModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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

                    if (contentForTargetDate != null) {
                        showDeleteConfirmationPopup(selectedDate)
                    } else {
                        showTextInputPopup(selectedDate)
                    }
                }
            }
        })

        setData()

        return binding.root
    }

    private fun setData() {
        val param = HashMap<String, Any>()
        param["writer"] = "lul7qF"
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
        binding.calendarManageView.removeDecorators()
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

    private fun showTextInputPopup(specialDate: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter Text")

        val input = EditText(requireContext())
        input.hint = "새로운 일정을 입력하세요."
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val enteredText = input.text.toString()
            if (enteredText.isNotBlank()) {
                // 여기에서 입력된 텍스트를 사용하거나 처리할 수 있습니다.
                //Snackbar.make(requireView(), "Entered text: $enteredText", Snackbar.LENGTH_SHORT).show()
                val param = HashMap<String, Any>()

                param["specialDate"] = specialDate
                param["content"] = enteredText
                param["writer"] = "lul7qF"

                postCalendar(param)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showDeleteConfirmationPopup(specialDate: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("기념일 삭제")
        builder.setMessage("정말로 기념일을 삭제하시겠습니까?\n(상대방에 의해 작성된 일정은 삭제할 수 없습니다.)")

        builder.setPositiveButton("삭제") { dialog, _ ->
            val param = HashMap<String, Any>()
            param["specialDate"] = specialDate
            param["writer"] = "lul7qF"
            deleteCalendar(param)
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    fun postCalendar(data: HashMap<String, Any>) {
        NetworkManager.postRequest(data,
            {
                setData()
            },
            {
                // 실패 처리 로직을 여기에 작성합니다.
            })
    }

    fun deleteCalendar(data: HashMap<String, Any>) {
        NetworkManager.deleteCalendar(data, {
            setData()
        }) {

        }
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