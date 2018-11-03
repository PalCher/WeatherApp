package com.example.pavel.weathermenuapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pavel.weathermenuapp.model.WeatherRequest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.converter.gson.GsonConverterFactory;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private TextView tv_city;
    private String name;
    private Button btnSave;
    private Button btnLoad;
    private TextInputEditText editCity;
    private TextView textTemp;
    private OpenWeather openWeather;
    private Button getWeather;
    private WeatherDataSource weatherDataSource;
    private WeatherDataReader weatherDataReader;
    private static final int PERMISSION_REQUEST_CODE = 10;
    private LocationManager locationManager;
    private String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGUI();
        InitDataSource();
        initRetrofit();


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
        {


            getWeather.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    requestLocation();
                }
            });
        } else {
            requestMyPermissions();
        }

        //startService(new Intent(MainActivity.this, GetWeatherService.class));


    }

    public void InitDataSource (){
        weatherDataSource = new WeatherDataSource(getApplicationContext());
        weatherDataSource.open();
        weatherDataReader = weatherDataSource.getWeatherDataReader();
    }

    private void requestMyPermissions(){
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE)){
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.length == 3 &&
                    (   grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[2] == PackageManager.PERMISSION_GRANTED )){

                getWeather.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestLocation();
                    }
                });

            }
        }
    }

    public void initGUI(){
       DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
               this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
       drawer.addDrawerListener(toggle);
       toggle.syncState();
       NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
       navigationView.setNavigationItemSelectedListener(this);

       Paper.init(this);

       getWeather = findViewById(R.id.getWeather);
       tv_city = findViewById(R.id.tv_city);
       textTemp = findViewById(R.id.textTemp);
    }


    public void initRetrofit(){
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openWeather = retrofit.create(OpenWeather.class);
    }


    public void requestRetrofit( Double latitude, Double longitude, String unitsFormat, String keyApi){
        openWeather.loadWeather(latitude, longitude,unitsFormat, keyApi)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {

                            String city = response.body().getName();
                            weatherDataReader.querySelection(city);
                            try {
                            if (!weatherDataReader.getCursor().moveToFirst())
                                {
                                    weatherDataSource.AddData(city,
                                            Float.toString(response.body().getMain().getTemp()));
                                } else

                                {
                                    WeatherData data = weatherDataReader.cursorToData();
                                    weatherDataSource.EditData(data, city, Float.toString(response.body().getMain().getTemp()));
                                }
                            } finally {
                                try {
                                    weatherDataReader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            textTemp.setText(Float.toString(response.body().getMain().getTemp()));
                            tv_city.setText(city);

                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        textTemp.setText("Error");
                    }
                });
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        provider = locationManager.getBestProvider(criteria, true);
        if (provider != null){
            locationManager.requestLocationUpdates(provider,10000,10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    Double latitude = location.getLatitude();
                    Double longitude = location.getLongitude();
//                    textTemp.setText(latitude);
//                    tv_city.setText(longitude);

                    requestRetrofit(latitude,longitude, getResources().getString(R.string.Celsius),
                            getResources().getString(R.string.apiKey));
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//
//        Paper.book("OptionsBook").write("city", city.getText());
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        name = Paper.book("OptionsBook").read("city");
//        super.onRestoreInstanceState(savedInstanceState);
//    }

//    @Override
//    protected void onStart() {
//        name = Paper.book("OptionsBook").read("city").toString();
//        city.setText(name);
//        super.onStart();
//    }
//
//    @Override
//    protected void onStop() {
//        Paper.book("OptionsBook").write("city", city.getText());
//        super.onStop();
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.comment) {

        } else if (id == R.id.info) {
        }

        return true;
    }
}
