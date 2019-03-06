package com.example.fawwazazrin.cleanmyriver_test;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.Character.isUpperCase;

public class ImageViewerActivity extends AppCompatActivity {

    public int REQUEST_CODE = 20;
    public ImageView imageView;
    private TextView coordinates;
    private TextView addressText;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button uploadButton;
    private DatabaseReference mDatabase;
    private double loclong;
    private double loclad;
    private String manufacturer;
    private String model;
    private static String TAG = "MyActivity";
    private String filepath;
    private static Bitmap b;
    private static String finaladdress;
    private double currentTime;
    private File output;
    public static String f;
    //private boolean external;
    //FirebaseStorage storage;
    //StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageviewer);
        onLaunchCamera();
        coordinates = (TextView) findViewById(R.id.coordinates);
        addressText = (TextView) findViewById(R.id.addrTextView);
        uploadButton = (Button) findViewById(R.id.upload);
        //storage = FirebaseStorage.getInstance();
        //storageReference = storage.getReference();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                loclong = location.getLongitude();
                loclad = location.getLatitude();
                coordinates.append("\n " + loclad + " " + loclong);
                getAddress(loclad, loclong);

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


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED&&checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET},10
                );
                return;
            }
            else{
                locationFind();
            }
        }


        uploadButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(ImageViewerActivity.this, MainActivity.class);
                startActivity(intent);

            }

        });
    }

    public void onLaunchCamera() {


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        output = new File(dir, "CameraContent.jpeg");
        filepath = output.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));

        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Bitmap photo = BitmapFactory.decodeFile(filepath);
                //Bitmap photo = (Bitmap) data.getExtras().get("data");
                //setBitmap(photo);
                setFile(filepath);
                imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(photo);
                //getAddress(loclad, loclong);
                getPhoneModel();
                pushDatabase();

            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationFind();
                }
                return;
        }
    }

    private void locationFind() {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
            }

    public void pushDatabase() {
        Log.i(TAG, "Function working");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Map locMap = new HashMap();
        locMap.put("loc_longtitude", loclong);
        locMap.put("loc_laditude", loclad);
        Map userMap = new HashMap();
        userMap.put("Name", "Kajang River");
        //userMap.put("RGB_red", 1.253);
        //userMap.put("RGB_blue", 0.253);
        //userMap.put("RGB_green", 3.2);
        userMap.put("Manufacturer", manufacturer);
        userMap.put("Model", model);
        userMap.putAll(locMap);
        String key = mDatabase.push().getKey(); // generate id for information uploaded
        mDatabase.push().setValue(userMap);
    }

    public void getPhoneModel() {

        manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;
        Log.i(TAG, "Phone model: " + manufacturer + " " + model);
    }

    /*
    public void uploadImage() {

        if(filePath!=null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

        }
    }*/

    public void setAddress(String finaladdress) {
        this.finaladdress = finaladdress;
    }

    public static String getAddress() {
        return finaladdress;
    }

    public void getAddress(double loclad, double loclong) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        //StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geocoder.getFromLocation(loclad, loclong, 1);

            if(address!=null) {

                String city = address.get(0).getAddressLine(0);
                String state = address.get(0).getAdminArea();
                finaladdress = city + " " + state;
                setAddress(finaladdress);
                Log.w(TAG, "Address: " + finaladdress);
                addressText.setText(finaladdress);

                /*
                int maxLines = address.get(0).getMaxAddressLineIndex();
                for (int i = 0; i < maxLines; i++) {
                    String addressStr = address.get(0).getAddressLine(i);
                    builder.append(addressStr);
                    builder.append(" ");
                }

                String finaladdress = builder.toString();
                Log.w(TAG, "Location Address: " + finaladdress);
            } */
            }

            else{
                Log.w(TAG, "Address is null");
            }
        }
        catch (IOException e) { }
        catch (NullPointerException e) { }
    }

    public void setFile(String filepath) {
        f = filepath;
    }

    public static String getFile() {
        return f;
    }

    }

