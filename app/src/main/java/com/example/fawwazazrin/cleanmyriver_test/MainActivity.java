package com.example.fawwazazrin.cleanmyriver_test;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    static double loc_long;
    static double loc_lat;
    String TAG = "MainActivity";
    String finaladdress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView camera_button = (CardView) findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ImageViewerActivity.class);
                startActivity(intent);

            }
        });

        CardView help_button = (CardView) findViewById(R.id.help_button);
        help_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        CardView about_button = (CardView) findViewById(R.id.about_button);
        about_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                loc_long = location.getLongitude();
                loc_lat = location.getLatitude();
                Log.i(TAG, "Location: " + loc_long + " " + loc_lat);
                getAddress(loc_lat, loc_long);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10
                );
                return;

            } else {
                locationFind();
            }
        }



    }

    public void locationFind() {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
    }

    public void getAddress(double loc_lat, double loc_long) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        //StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geocoder.getFromLocation(loc_lat, loc_long, 1);

            if (address != null) {

                String city = address.get(0).getAddressLine(0);
                String state = address.get(0).getAdminArea();
                finaladdress = city + " " + state;
                Log.w(TAG, "Address: " + finaladdress);
                //ImageActivity.setAddress(finaladdress);
                //ImageActivity.appearAddress();

            } else {
                Log.w(TAG, "Address is null");
            }
        } catch (IOException e) {
        } catch (NullPointerException e) {
        }
    }

}