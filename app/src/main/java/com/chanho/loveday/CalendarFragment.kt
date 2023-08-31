package com.chanho.loveday

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.chanho.loveday.application.MyApplication
import com.chanho.loveday.databinding.FragmentCalendarBinding
import com.chanho.loveday.model.CalendarModel
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
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
    private lateinit var binding: FragmentCalendarBinding
    private var calendarModelData: List<CalendarModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false )

        setBtnEvent()
        setCalendarEvent()
        setData()

        return binding.root
    }

    private fun setBtnEvent() {
        binding.calendarReloadButton.setOnClickListener {
            setData()
        }

        binding.calendarKeyButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("사랑의 키 등록")
            builder.setMessage("설정창에 있는 상대의 키를 등록하고 메모를 공유해요!")

            val input = EditText(requireContext())
            input.hint = "LoveDay"
            builder.setView(input)

            builder.setPositiveButton("등록") { dialog, _ ->
                val enteredText = input.text.toString()
                if (enteredText.isNotBlank()) {
                    MyApplication.prefs.setString("partnerKey", enteredText)

                    registerKey(enteredText)
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }
    }

    private fun setCalendarEvent() {
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

                    if (contentForTargetDate != null) {
                        showDeleteConfirmationPopup(selectedDate)
                        binding.calendarDayText.text = selectedDate
                        binding.calendarDayContent.text = contentForTargetDate
                    } else {
                        showTextInputPopup(selectedDate)
                    }
                }
            }
        })
    }

    private fun setData() {
        val privateKey = MyApplication.prefs.getString("privateKey", "")
        val partnerKey = MyApplication.prefs.getString("partnerKey", "")

        val param = HashMap<String, Any>()
        param["writer"] = privateKey
        if (partnerKey != "") {
            param["partner"] = partnerKey
            binding.calendarKeyButton.setImageResource(R.drawable.main_heart_middle)
        }
        fetchData(param)
    }

    private fun fetchData(param: HashMap<String, Any>) {
        NetworkManager.getCalendar(param) { data ->
            if (data != null) {
                calendarModelData = data
                addSpecialDateDecorators(data)
            } else {
                // 데이터 가져오기 실패
            }
        }
    }

    private fun addSpecialDateDecorators(calendarModels: List<CalendarModel>) {
        val specialDates = extractSpecialDates(calendarModels)
        val eventDecorator = EventDecorator(requireContext(), Color.parseColor("#FF879B"), specialDates)
        binding.calendarManageView.removeDecorators()
        binding.calendarManageView.addDecorator(eventDecorator)

        binding.calendarManageView.setTitleFormatter(MonthArrayTitleFormatter(resources.getTextArray(R.array.custom_months)))
        binding.calendarManageView.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)));
        binding.calendarManageView.setHeaderTextAppearance(R.style.CalendarWidgetHeader);

    }

    private fun extractSpecialDates(calendarModels: List<CalendarModel>): Set<CalendarDay> {
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
        val privateKey = MyApplication.prefs.getString("privateKey", "")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("일정 등록")

        val input = EditText(requireContext())
        input.hint = "새로운 일정을 입력하세요."
        builder.setView(input)

        builder.setPositiveButton("저장") { dialog, _ ->
            val enteredText = input.text.toString()
            if (enteredText.isNotBlank()) {

                val param = HashMap<String, Any>()

                param["specialDate"] = specialDate
                param["content"] = enteredText
                param["writer"] = privateKey

                postCalendar(param)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showDeleteConfirmationPopup(specialDate: String) {
        val privateKey = MyApplication.prefs.getString("privateKey", "")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("기념일 삭제")
        builder.setMessage("정말로 기념일을 삭제하시겠습니까?\n(상대방에 의해 작성된 일정은 삭제할 수 없습니다.)")

        builder.setPositiveButton("삭제") { dialog, _ ->
            val param = HashMap<String, Any>()
            param["specialDate"] = specialDate
            param["writer"] = privateKey
            deleteCalendar(param)
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun postCalendar(data: HashMap<String, Any>) {
        NetworkManager.postCalendarRequest(data,
            {
                setData()
            },
            {
                // 실패 처리 로직을 여기에 작성합니다.
            })
    }

    private fun deleteCalendar(data: HashMap<String, Any>) {
        NetworkManager.deleteCalendar(data, {
            setData()
        }) {

        }
    }

    private fun registerKey(partner: String) {
        val privateKey = MyApplication.prefs.getString("privateKey", "")

        val param = HashMap<String, Any>()
        param["partner"] = partner
        param["writer"] = privateKey

        NetworkManager.postKey(param, {
            setData()
            binding.calendarKeyButton.setImageResource(R.drawable.main_heart_middle)
        }) {
            println("키등록 실패")
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

class EventDecorator : DayViewDecorator {
    private var color = 0
    private lateinit var dates: HashSet<CalendarDay>
    private val drawable: Drawable?

    constructor(context: Context, date: CalendarDay, color: Int) {
        this.color = color
        this.dates = HashSet()
        this.dates.add(date)
        this.drawable = context.getDrawable(R.drawable.calendar_selector)
    }

    constructor(context: Context, color: Int, dates: Collection<CalendarDay>) {
        this.color = color
        this.dates = HashSet(dates)
        this.drawable = context.getDrawable(R.drawable.calendar_selector)
    }

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(DotSpan(10F, color))
    }
}





