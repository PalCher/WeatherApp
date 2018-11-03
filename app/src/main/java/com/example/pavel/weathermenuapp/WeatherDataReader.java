package com.example.pavel.weathermenuapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by pavel on 16.10.18.
 */

public class WeatherDataReader implements Closeable {

    public Cursor getCursor() {
        return cursor;
    }

    private Cursor cursor;
    private SQLiteDatabase database;

    public WeatherDataReader(SQLiteDatabase database) {
        this.database = database;
    }

    private String [] weatherAllColumns = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_CITY,
            DatabaseHelper.COLUMN_TEMP
    };

    private String [] weatherCityColumn = {
            DatabaseHelper.COLUMN_CITY
    };

    public void open(){
        query();
        cursor.moveToFirst();
    }

    public void query(){
        cursor = database.query(DatabaseHelper.TABLE_WEATHER, weatherAllColumns,
                null,null,null,null,null);
    }

    public void querySelection (String city){
        cursor = database.query(DatabaseHelper.TABLE_WEATHER, weatherAllColumns, "city = ?" ,
                new String[] {city}, null,null,null);
    }

    public void Refresh(){
        int position = cursor.getPosition();
        query();
        cursor.moveToPosition(position);
    }

    @Override
    public void close() throws IOException {
        cursor.close();
    }

    public WeatherData cursorToData(){
        WeatherData weatherData = new WeatherData();
        weatherData.setId(cursor.getLong(0));
        weatherData.setCity(cursor.getString(1));
        weatherData.setTemperature(cursor.getString(2));
        return weatherData ;
    }

    public int getCount (){
        return cursor.getCount();
    }

    public WeatherData getPosition(int position){
        cursor.moveToPosition(position);
        return cursorToData();
    }
}
