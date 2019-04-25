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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    static double loc_long; //GPS longitude
    static double loc_lat;  //GPS latitude
    String TAG = "MainActivity";
    String finaladdress;    //address variable from GPS coordinates
    Animation topanimation;     //animation variable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topanimation = AnimationUtils.loadAnimation(this, R.anim.anime_top_to_bottom);  //set topanimation with top to bottom animation from drawable
        CardView camera_button = (CardView) findViewById(R.id.camera_button);   //camera button initialization
        CardView help_button = (CardView) findViewById(R.id.help_button);   //help button initialization
        CardView about_button = (CardView) findViewById(R.id.about_button);     //about button initialization

        /*
            Set animation for each button
         */
        camera_button.setAnimation(topanimation);
        help_button.setAnimation(topanimation);
        about_button.setAnimation(topanimation);


        /*
            Set intent for the buttons
         */
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, DemoActivity.class);
                startActivity(intent);

            }
        });


        help_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });


        about_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        /*
            Location Manager to constantly detect location at a set interval of time
         */
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                loc_long = location.getLongitude();
                loc_lat = location.getLatitude();
                Log.i(TAG, "Location: " + loc_long + " " + loc_lat);    //to log the location
                //getAddress(loc_lat, loc_long);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            /*
                if location is disabled, the app directs to the Settings page
             */
            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        /*
            Checks permission from phone to access location
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10
                );
                return;

            } else {
                locationFind();     //function that sets the interval to detect GPS location
            }
        }



    }

    /*
        Function to request location update
     */
    public void locationFind() {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
    }

    /*
        Function to write the address based on the GPS coordinates
     */
    public String getAddress(double loc_lat, double loc_long) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        //StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geocoder.getFromLocation(loc_lat, loc_long, 1);

            if (address != null) {

                String city = address.get(0).getAddressLine(0);
                String state = address.get(0).getAdminArea();
                finaladdress = city + " " + state;
                Log.w(TAG, "Address: " + finaladdress);     //log address on the logcat
                //ImageActivity.setAddress(finaladdress);
                //ImageActivity.appearAddress();

                return finaladdress;    //returns the address

            } else {
                Log.w(TAG, "Address is null");
                return "Address not detected";
            }
        } catch (IOException e) {
            return "Address not detected";
        } catch (NullPointerException e) {
            Log.i("LOCATION", "ADDRESS NOT DETECTED");
            Toast toast = Toast.makeText(getApplicationContext(), "Location null", Toast.LENGTH_SHORT);
            toast.show();
            return "Address not detected";    //catch if it returns null
        }
    }

}