package com.chanho.loveday

import com.chanho.loveday.model.CalendarModel
import com.chanho.loveday.model.MemoModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    @POST("api/calendar")
    fun saveCalendar(@Body data: HashMap<String, Any>): Call<Void>

    @GET("api/calendar")
    fun getCalendar(@QueryMap params: HashMap<String, Any>): Call<List<CalendarModel>>

    @HTTP(method = "DELETE", path="api/calendar", hasBody = true)
    fun deleteCalendar(@Body params: HashMap<String, Any>): Call<Void>

    @GET("api/memo")
    fun getMemo(@QueryMap params: HashMap<String, Any>): Call<List<MemoModel>>

    @POST("api/memo")
    fun saveMemo(@Body data: HashMap<String, Any>): Call<Void>

    @HTTP(method = "DELETE", path="api/memo", hasBody = true)
    fun deleteMemo(@Body params: HashMap<String, Any>): Call<Void>

    @PUT("api/memo")
    fun updateMemo(@Body data: HashMap<String, Any>): Call<Void>

    @POST("api/partner")
    fun postKey(@Body data: HashMap<String, Any>): Call<Void>
}

object NetworkManager {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://52.78.124.184:8080/") // API의 기본 URL 설정
        .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기 추가
        .build()

    private val apiService: ApiService = retrofit.create(ApiService::class.java)

    fun getCalendar(params: HashMap<String, Any>, callback: (List<CalendarModel>?) -> Unit) {
        val call: Call<List<CalendarModel>> = apiService.getCalendar(params)

        call.enqueue(object : Callback<List<CalendarModel>> {
            override fun onResponse(call: Call<List<CalendarModel>>, response: Response<List<CalendarModel>>) {
                if (response.isSuccessful) {
                    val data: List<CalendarModel>? = response.body()
                    callback(data) // 가져온 데이터(data)를 콜백으로 전달
                } else {
                    callback(null) // 실패 시 null을 콜백으로 전달
                }
            }

            override fun onFailure(call: Call<List<CalendarModel>>, t: Throwable) {
                callback(null) // 실패 시 null을 콜백으로 전달
            }
        })
    }

    fun postCalendarRequest(data: HashMap<String, Any>, success: () -> Unit, failure: () -> Unit) {
        apiService.saveCalendar(data).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    println("Post Success")
                    success()
                } else {
                    println("Post Fail")
                    failure()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Network Error: ${t.message}")
                failure()
            }
        })
    }

    fun deleteCalendar(params: HashMap<String, Any>, success: () -> Unit, failure: () -> Unit) {
        apiService.deleteCalendar(params).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    success()
                } else {
                    failure()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                failure()
            }
        })
    }

    fun getMemo(params: HashMap<String, Any>, callback: (List<MemoModel>?) -> Unit) {
        val call: Call<List<MemoModel>> = apiService.getMemo(params)

        call.enqueue(object : Callback<List<MemoModel>> {
            override fun onResponse(call: Call<List<MemoModel>>, response: Response<List<MemoModel>>) {
                if (response.isSuccessful) {
                    val data: List<MemoModel>? = response.body()
                    callback(data) // 가져온 데이터(data)를 콜백으로 전달
                } else {
                    callback(null) // 실패 시 null을 콜백으로 전달
                }
            }

            override fun onFailure(call: Call<List<MemoModel>>, t: Throwable) {
                callback(null) // 실패 시 null을 콜백으로 전달
            }
        })
    }

    fun postMemoRequest(data: HashMap<String, Any>, success: () -> Unit, failure: () -> Unit) {
        apiService.saveMemo(data).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    println("Post Success")
                    success()
                } else {
                    println("Post Fail")
                    failure()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Network Error: ${t.message}")
                failure()
            }
        })
    }

    fun deleteMemo(params: HashMap<String, Any>, success: () -> Unit, failure: () -> Unit) {
        apiService.deleteMemo(params).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    success()
                } else {
                    failure()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                failure()
            }
        })
    }

    fun updateMemo(data: HashMap<String, Any>, success: () -> Unit, failure: () -> Unit) {
        apiService.updateMemo(data).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    println("update Success")
                    success()
                } else {
                    println("update Fail")
                    failure()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Network Error: ${t.message}")
                failure()
            }
        })
    }

    fun postKey(data: HashMap<String, Any>, success: () -> Unit, failure: () -> Unit) {
        apiService.postKey(data).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    println("Key Success")
                    success()
                } else {
                    println("Key Fail")
                    failure()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Network Error: ${t.message}")
                failure()
            }
        })
    }
}


