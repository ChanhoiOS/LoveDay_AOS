package com.chanho.loveday.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chanho.loveday.NetworkManager
import com.chanho.loveday.model.CalendarModel
import java.util.HashMap

class CalendarViewModel: ViewModel() {
    var calendarModelData = MutableLiveData<List<CalendarModel>?>()
    var calendarRegisterSuccess = MutableLiveData<Boolean>()
    var calendarUpdateSuccess = MutableLiveData<Boolean>()
    var calendarDeleteSuccess = MutableLiveData<Boolean>()
    var sendNotification = MutableLiveData<Boolean>()

    fun fetchData(param: HashMap<String, Any>) {
        NetworkManager.getCalendar(param) { data ->
            if (data != null) {
                calendarModelData.value = data
            } else {
                // 데이터 가져오기 실패
            }
        }
    }

    fun registerCalendar(data: HashMap<String, Any>) {
        NetworkManager.postCalendarRequest(data,
            {
                calendarRegisterSuccess.value = true
            }) {
                calendarRegisterSuccess.value = false
            }
    }

    fun updateCalendar(data: HashMap<String, Any>) {
        NetworkManager.updateCalendar(data, {
            calendarUpdateSuccess.value = true
        }) {
            calendarUpdateSuccess.value = false
        }
    }

    fun deleteCalendar(data: HashMap<String, Any>) {
        NetworkManager.deleteCalendar(data, {
            calendarDeleteSuccess.value = true
        }) {
            calendarDeleteSuccess.value = false
        }
    }

    fun calenndarSendNoti(param: HashMap<String, Any>) {
        NetworkManager.calendarNoti(param, {
            sendNotification.value = true
        }) {
            sendNotification.value = false
        }
    }
}