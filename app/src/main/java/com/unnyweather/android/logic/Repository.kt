package com.unnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import com.unnyweather.android.logic.model.Weather


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext
//仓库层统一封装入口
object Repository {
    //返回一个fire对象
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
            //用Result.success()方法来包装获取的城市数据列表。
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            //用Result.failure()方法来包装一个异常信息。
        }
    }
    fun refreshWeather(lng: String, lat: String, placeName: String) = fire(Dispatchers.IO) {
        //同时获取到realtimeResponse和dailyResponse后，如果状态OK，那么就将Realtime和Daily对象取出并封装到一个weather对象中
        //然后使用Result.success()方法来包装这个weather对象，不然就用Result.failuer()方法包装一个异常信息。
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse, dailyResponse)
                Result.success(weather)
            } else {
                Result.failure(
                        RuntimeException(
                                "realtime response status is ${realtimeResponse.status}" +
                                        "daily response status is ${dailyResponse.status}"
                        )
                )
            }
        }
    }


    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
            //使用emit()方法将包装的结果发射出去。
        }

}