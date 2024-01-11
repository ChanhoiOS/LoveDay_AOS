package com.chanho.loveday.memo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chanho.loveday.NetworkManager
import com.chanho.loveday.model.MemoModel
import java.util.HashMap

class MemoViewModel: ViewModel() {

    var memoModelData = MutableLiveData<List<MemoModel>>()

    fun fetchData(param: HashMap<String, Any>) {
        NetworkManager.getMemo(param) { data ->
            if (data != null) {
                var sortedData = data.sortedByDescending { it.id ?: Int.MIN_VALUE }
                Log.e("sort: ", sortedData.toString())
                memoModelData.value = sortedData
                Log.e("value: ", memoModelData.value.toString())
            } else {
                // 데이터 가져오기 실패
            }
        }
    }
}