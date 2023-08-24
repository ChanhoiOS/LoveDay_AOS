package com.chanho.loveday

import com.chanho.loveday.model.CalendarModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

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

    fun postRequest(data: HashMap<String, Any>, success: () -> Unit, failure: () -> Unit) {
        apiService.saveCalendar(data).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    println("Calendar First Save Success")
                    success()
                } else {
                    println("Calendar First Save Fail")
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

interface ApiService {
    @POST("api/calendar")
    fun saveCalendar(@Body data: HashMap<String, Any>): Call<Void>

    @GET("api/calendar")
    fun getCalendar(@QueryMap params: HashMap<String, Any>): Call<List<CalendarModel>>
}
