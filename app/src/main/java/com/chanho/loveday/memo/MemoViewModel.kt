package com.chanho.loveday.memo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chanho.loveday.NetworkManager
import com.chanho.loveday.model.MemoModel
import java.util.HashMap

class MemoViewModel: ViewModel() {

    var memoModelData = MutableLiveData<List<MemoModel>>()
    var memoRegisterSuccess = MutableLiveData<Boolean>()
    var memoUpdateSuccess = MutableLiveData<Boolean>()
    var memoDeleteSuccess = MutableLiveData<Boolean>()
    var keyRegister = MutableLiveData<Boolean>()
    var sendNotification = MutableLiveData<Boolean>()

    fun fetchData(param: HashMap<String, Any>) {
        NetworkManager.getMemo(param) { data ->
            if (data != null) {
                var sortedData = data.sortedByDescending { it.id ?: Int.MIN_VALUE }
                memoModelData.value = sortedData
            } else {
                // 데이터 가져오기 실패
            }
        }
    }

    fun registerMemo(data: HashMap<String, Any>) {
        NetworkManager.postMemoRequest(data, {
            memoRegisterSuccess.value = true
        }, {
            memoRegisterSuccess.value = false
        })
    }

    fun updateMemo(data: HashMap<String, Any>) {
        NetworkManager.updateMemo(data, {
            memoUpdateSuccess.value = true
        }, {
            memoUpdateSuccess.value = false
        })
    }

    fun deleteMemo(data: HashMap<String, Any>) {
        NetworkManager.deleteMemo(data, {
            memoDeleteSuccess.value = true
        }, {
            memoDeleteSuccess.value = false
        })
    }

    fun registerKey(param: HashMap<String, Any>) {
        NetworkManager.registerPartner(param, {
            keyRegister.value = true
        }) {
            keyRegister.value = false
        }
    }

    fun memoSendNoti(param: HashMap<String, Any>) {
        NetworkManager.memoNoti(param, {
            sendNotification.value = true
        }) {
            sendNotification.value = false
        }
    }
}