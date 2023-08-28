package com.chanho.loveday.adapter

import com.super_rabbit.wheel_picker.WheelAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/** 지역 선택 Adapter */
class WpResidencePickerAdapter(var specialDates: ArrayList<String>) : WheelAdapter {
    //get item value based on item position in wheel
    override fun getValue(position: Int): String {

        if(position < 0){
            if((position + specialDates.size) == 0){

            }
            return specialDates[(position + specialDates.size)]
        }
        return specialDates[(position%specialDates.size)]
    }

    //get item position based on item string value
    override fun getPosition(vale: String): Int {
        return 0
    }

    //return a string with the approximate longest text width, for supporting WRAP_CONTENT
    override fun getTextWithMaximumLength(): String {
        return "Mmm 00, 0000"
    }

    //return the maximum index
    override fun getMaxIndex(): Int {
//        return Integer.MAX_VALUE
        return specialDates.size-1
    }

    //return the minimum index
    override fun getMinIndex(): Int {
//        return Integer.MIN_VALUE
        return 0
    }
}