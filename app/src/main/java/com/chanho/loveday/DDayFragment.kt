package com.chanho.loveday

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chanho.loveday.adapter.WpResidencePickerAdapter
import com.chanho.loveday.databinding.FragmentDDayBinding
import com.super_rabbit.wheel_picker.OnValueChangeListener
import com.super_rabbit.wheel_picker.WheelPicker
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DDayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DDayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentDDayBinding
    private var preferences: SharedPreferences? = null

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
        binding = FragmentDDayBinding.inflate(inflater, container, false )

        preferences = requireActivity().getSharedPreferences("setDDay", Context.MODE_PRIVATE)
        val datingDay = preferences?.getString("datingDay", "")

        getSpecialDDayInfo(datingDay)


        return binding.root
    }

    fun getSpecialDDayInfo(dday: String?) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        val startDate = dateFormatter.parse(dday) ?: return

        val calendar = Calendar.getInstance()
        val specialDDayCheck: MutableList<Map<String, Int>> = mutableListOf()

        // 100일 단위의 기념일 추출
        for (day in 0..7200 step 100) {
            val specialDate: Date = if (day == 0) {
                startDate
            } else {
                calendar.apply {
                    time = startDate
                    add(Calendar.DAY_OF_YEAR, day - 1)
                }.time
            }

            val specialStr = dateFormatter.format(specialDate)
            specialDDayCheck.add(mapOf(specialStr to day))
        }

        // 1년 단위의 기념일 추출
        for (year in 1..20) {
            val anniversaryDate: Date = calendar.apply {
                time = startDate
                add(Calendar.YEAR, year)
            }.time

            val anniversaryStr = dateFormatter.format(anniversaryDate)
            val days = year

            val insertIndex = when (year) {
                1 -> 4
                2 -> 9
                3 -> 13
                4 -> 18
                5 -> 23
                6 -> 27
                7 -> 32
                8 -> 37
                9 -> 41
                10 -> 46
                11 -> 51
                12 -> 55
                13 -> 60
                14 -> 65
                15 -> 69
                16 -> 74
                17 -> 79
                18 -> 83
                19 -> 88
                else -> -1
            }

            if (insertIndex >= 0) {
                specialDDayCheck.add(insertIndex, mapOf(anniversaryStr to days))
            } else {
                specialDDayCheck.add(mapOf(anniversaryStr to days))
            }
        }

        println("specialDDayCheck:: $specialDDayCheck")

        val specialDates = specialDDayCheck.map { it.keys.first() }
        val specialDatesArrayList: ArrayList<String> = ArrayList(specialDates)
        setPicker(specialDatesArrayList, specialDDayCheck)
        println("specialDates:: $specialDates")
    }

    fun setPicker(specialDates: ArrayList<String>, specialDDayCheck: MutableList<Map<String, Int>>) {
        binding.wpPicker.apply {
            // Set rounded wrap enable
            setSelectorRoundedWrapPreferred(true)
            // Set wheel item count
//            setWheelItemCount(10)
            // Set wheel max index
            setMax(0)
            // Set wheel min index
            setMin(16)
            // Set selected text color
            setSelectedTextColor(R.color.black)
            // Set unselected text color
            setUnselectedTextColor(R.color.pink)
            // Set user defined adapter
            setAdapter(WpResidencePickerAdapter(specialDates))

            scrollTo(getIndex(specialDates))

            val index = getIndex(specialDates)
            val dayText = specialDDayCheck[index].values.first()

            if (dayText > 99) {
                binding.ddayDatingDay.text = dayText.toString() + "일"
            } else {
                binding.ddayDatingDay.text = dayText.toString() + "주년"
            }

            setOnValueChangeListener(object : OnValueChangeListener {
                override fun onValueChange(picker: WheelPicker, oldVal: String, newVal: String) {
                    val index = specialDates.indexOf(binding.wpPicker.getCurrentItem())
                    val value = specialDDayCheck[index].values.first()
                    if (value == 0) {
                        binding.ddayDatingDay.text = "만난 날"
                    } else {
                        if(value > 99) {
                            binding.ddayDatingDay.text = value.toString() + "일"
                        } else {
                            binding.ddayDatingDay.text = value.toString() + "주년"
                        }

                    }
                }
            })
        }
    }

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DDayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DDayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}