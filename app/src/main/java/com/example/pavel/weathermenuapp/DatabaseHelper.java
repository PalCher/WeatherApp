package com.example.pavel.weathermenuapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by pavel on 16.10.18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_WEATHER = "weather";
    public static final String COLUMN_ID = BaseColumns._ID; // "_id"
    public static final String COLUMN_TEMP = "temperature";
    public static final String COLUMN_CITY = "city";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_WEATHER + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CITY + " TEXT," +
                COLUMN_TEMP + " TEXT);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if ((oldVersion == 1) && (newVersion == 2)) {
            String upgradeQuery = "ALTER TABLE " + TABLE_WEATHER + " ADD COLUMN " +
                    COLUMN_CITY + " TEXT DEFAULT Title";
            db.execSQL(upgradeQuery);
        }
    }
}
