package com.unnyweather.android.logic.network;

import com.unnyweather.android.SunnyWeatherApplication;
import com.unnyweather.android.logic.model.DailyResponse;
import com.unnyweather.android.logic.model.RealtimeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WeatherService {

    @GET("v2.5/" + SunnyWeatherApplication.TOKEN + "/{lng},{lat}/realtime.json")
    Call<RealtimeResponse> getRealtimeWeather(@Path("lng") String lng, @Path("lat") String lat);
    //getRealtimeWeather()方法用于获取实时的天气信息
    @GET("v2.5/" + SunnyWeatherApplication.TOKEN + "/{lng},{lat}/daily.json")
    Call<DailyResponse> getDailyWeather(@Path("lng") String lng, @Path("lat") String lat);
    //getDailyWeather()方法用于获取未来的天气信息
}
