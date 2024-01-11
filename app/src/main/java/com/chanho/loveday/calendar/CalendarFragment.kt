package com.chanho.loveday

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chanho.loveday.NetworkManager
import com.chanho.loveday.application.MyApplication
import com.chanho.loveday.calendar.CalendarViewModel
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
    private lateinit var viewModel: CalendarViewModel

    private lateinit var loadingIndicator: View
    private var calendarModelData: List<CalendarModel>? = null
    private var editWriter = ""
    private var editCalendar = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false )
        viewModel = ViewModelProvider(requireActivity()).get(CalendarViewModel::class.java)

        binding.calendarEditBtn.visibility = View.GONE

        setObserver()
        setBtnEvent()
        setCalendarEvent()
        setData()
        initView()

        return binding.root
    }

    private fun initView() {
        loadingIndicator = binding.calendarProgressBar
    }

    private fun setBtnEvent() {
        val privateKey = MyApplication.prefs.getString("privateKey", "")

        binding.calendarReloadButton.setOnClickListener {
            loadingIndicator.visibility = View.VISIBLE
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

        binding.calendarEditBtn.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("일정 수정")

            var btnAction: DialogInterface.OnClickListener?
            btnAction = DialogInterface.OnClickListener { _, _ ->
                if (editWriter == privateKey) {
                    showUpdateInputPopup(true, editCalendar)
                } else {
                    showUpdateInputPopup(false, editCalendar)
                }
            }

            dialog.setPositiveButton("수정", btnAction)

            btnAction = DialogInterface.OnClickListener { _, _ ->
                if (editWriter == privateKey) {
                    showDeleteConfirmationPopup(editCalendar)
                } else {
                    print("editWriter $editWriter")
                    print("privateKey $privateKey")
                    Toast.makeText(requireContext(), "상대방에 의해 작성된 일정은 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.setNegativeButton("삭제", btnAction)

            btnAction = DialogInterface.OnClickListener { _, _ ->
                println("취소")
            }
            dialog.setNeutralButton("취소", btnAction)

            dialog.show()
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

                    val writerForTargetDate = calendarModelData?.find { calendarModel ->
                        calendarModel.specialDate == selectedDate
                    }?.writer

                    editWriter = writerForTargetDate?.writer ?: ""
                    editCalendar = selectedDate

                    if (contentForTargetDate != null) {
                        binding.calendarEditBtn.visibility = View.VISIBLE
                        binding.calendarDayText.text = selectedDate
                        binding.calendarDayContent.text = contentForTargetDate
                    } else {
                        binding.calendarEditBtn.visibility = View.GONE
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
        viewModel.fetchData(param)
    }

    private fun setObserver() {
        viewModel.calendarModelData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                calendarModelData = it
                addSpecialDateDecorators(it)
                setCalendarContent()
            }
            loadingIndicator.visibility = View.GONE
        })

        viewModel.calendarRegisterSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it) {
                    setData()
                    calenndarSendNoti("일정 등록")
                    viewModel.calendarRegisterSuccess.value = false
                } else {

                }
            }
        })

        viewModel.calendarUpdateSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it) {
                    setData()
                    calenndarSendNoti("일정 수정")
                    viewModel.calendarUpdateSuccess.value = false
                } else {

                }
            }
        })

        viewModel.calendarDeleteSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it) {
                    setData()
                    viewModel.calendarDeleteSuccess.value = false
                } else {

                }
            }
        })

        viewModel.sendNotification.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it) {
                    Log.e("노티 발송 성공", it.toString())
                } else {
                    Log.e("노티 발송 실패", it.toString())
                }
            }
        })

        viewModel.keyRegister.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it) {
                    setData()
                    binding.calendarKeyButton.setImageResource(R.drawable.main_heart_middle)
                } else {

                }
            }
        })
    }

    private fun setCalendarContent() {
        val contentForTargetDate = calendarModelData?.find { calendarModel ->
            calendarModel.specialDate == editCalendar
        }?.content

        if (contentForTargetDate != null) {
            binding.calendarDayContent.text = contentForTargetDate
        } else {
            binding.calendarDayContent.text = "메모 내용"
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

    private fun showUpdateInputPopup(isMe: Boolean, specialDate: String) {
        val privateKey = MyApplication.prefs.getString("privateKey", "")
        val partnerKey = MyApplication.prefs.getString("partnerKey", "")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("일정 수정")

        val input = EditText(requireContext())
        input.hint = "수정할 내용을 입력하세요."
        builder.setView(input)

        builder.setPositiveButton("저장") { dialog, _ ->
            val enteredText = input.text.toString()

            if (enteredText.isNotBlank()) {
                val param = HashMap<String, Any>()
                param["specialDate"] = specialDate
                param["content"] = enteredText
                if (isMe) {
                    param["writer"] = privateKey
                } else {
                    param["writer"] = partnerKey
                }

                updateCalendar(param)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun postCalendar(data: HashMap<String, Any>) {
        viewModel.registerCalendar(data)
    }

    private fun deleteCalendar(data: HashMap<String, Any>) {
        viewModel.deleteCalendar(data)
    }

    private fun updateCalendar(data: HashMap<String, Any>) {
        viewModel.updateCalendar(data)
    }

    private fun registerKey(partner: String) {
        val privateKey = MyApplication.prefs.getString("privateKey", "")
        val token = MyApplication.prefs.getString("fcmToken", "")

        val param = HashMap<String, Any>()
        param["partner"] = partner
        param["writer"] = privateKey
        param["token"] = token

        viewModel.registerKey(param)
    }

    private fun calenndarSendNoti(type: String) {
        val partnerKey = MyApplication.prefs.getString("partnerKey", "")
        val param = HashMap<String, Any>()
        param["partner"] = partnerKey
        param["type"] = type

        viewModel.calenndarSendNoti(param)
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





