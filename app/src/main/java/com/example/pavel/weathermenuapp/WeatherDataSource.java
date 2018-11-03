package com.example.pavel.weathermenuapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by pavel on 16.10.18.
 */

public class WeatherDataSource implements Closeable {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public WeatherDataReader getWeatherDataReader() {
        return weatherDataReader;
    }

    private WeatherDataReader weatherDataReader;

    public WeatherDataSource(Context context){
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        weatherDataReader = new WeatherDataReader(database);
        weatherDataReader.open();
    }

    @Override
    public void close() throws IOException {
        weatherDataReader.close();
        dbHelper.close();
    }

    public WeatherData AddData(String city, String temperature){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CITY, city);
        values.put(DatabaseHelper.COLUMN_TEMP, temperature);
        long insertID = database.insert(DatabaseHelper.TABLE_WEATHER,
                null, values);
        WeatherData newData = new WeatherData();
        newData.setTemperature(temperature);
        newData.setCity(city);
        newData.setId(insertID);
        return newData;
    }

    public void EditData (WeatherData data, String city, String temperature){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CITY, city);
        values.put(DatabaseHelper.COLUMN_TEMP, temperature);
        values.put(DatabaseHelper.COLUMN_ID, data.getId());
        database.update(DatabaseHelper.TABLE_WEATHER, values,
                dbHelper.COLUMN_ID + "=" + data.getId(),
                null);

    }

}
