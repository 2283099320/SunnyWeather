package com.sunnyweather.android.logic.network

import com.unnyweather.android.logic.network.PlaceService
import com.unnyweather.android.logic.network.ServiceCreator
import com.unnyweather.android.logic.network.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {


    private val placeService = ServiceCreator.create(PlaceService::class.java)
    private val weatherService = ServiceCreator.create(WeatherService::class.java)
    suspend fun getDailyWeather(lng: String,lat: String) = weatherService.getDailyWeather(lng,lat).await()
    suspend fun getRealtimeWeather(lng: String,lat: String) = weatherService.getRealtimeWeather(lng,lat).await()
    //外部调用SunnyWeatherNetwork的searchPlaces()函数时，Retrofit就会立即发起网络请求，同时当前的协程也会被阻塞住。
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()
    //searchPlaces()函数得到await()函数的返回值后会将该数据再返回至上一层。
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}