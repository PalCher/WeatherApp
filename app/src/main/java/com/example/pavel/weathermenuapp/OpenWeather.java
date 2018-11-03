package com.example.pavel.weathermenuapp;

import com.example.pavel.weathermenuapp.model.WeatherRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface OpenWeather {
    @GET("data/2.5/weather")
    Call<WeatherRequest> loadWeather(@Query("lat") Double latitude, @Query("lon") Double longitude,@Query("units") String unitsFormat, @Query("appid") String keyApi);
}
