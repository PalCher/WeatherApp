package com.example.pavel.weathermenuapp;

/**
 * Created by pavel on 16.10.18.
 */

public class WeatherData {
    private long id;
    private String city;
    private String temperature;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
