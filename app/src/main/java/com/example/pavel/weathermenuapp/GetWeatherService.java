package com.example.pavel.weathermenuapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by pavel on 05.10.18.
 */

public class GetWeatherService extends IntentService {

    public GetWeatherService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        getWeather();
    }

    public void getWeather (){

    }
}
