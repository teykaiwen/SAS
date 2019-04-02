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
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.util.Base64.DEFAULT;
import static java.lang.Character.isUpperCase;

public class ImageViewerActivity extends MainActivity {

    public int REQUEST_CODE = 20;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private String manufacturer;
    private String model;
    private static String TAG = "MyActivity";
    private String mCurrentPath;
    private Uri photoURI;
    byte[] baos;
    byte[] pdfbyte;
    String receivedimg;
    String imgString;
    String URL = "http://192.168.137.1:5000/upload";
    JSONObject SSC;
    String ssc_value;
    TextView sscvalue;
    TextView home;
    ImageView img;
    Animation a1, a2;


    /*
    private ProgressBar progressBar;
    private Handler mhandler = new Handler();
    private int mProgressbar = 0;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview2);

        CardView ssccard = (CardView) findViewById(R.id.ssccard);
        sscvalue = (TextView) findViewById(R.id.sscvalue);
        home = (TextView) findViewById(R.id.homebutton);
        img = (ImageView) findViewById(R.id.img);

        takePhoto();

        a1 = AnimationUtils.loadAnimation(this, R.anim.anime_top_to_bottom);
        a2 = AnimationUtils.loadAnimation(this, R.anim.anime_bottom_to_top);

        ssccard.setAnimation(a1);
        home.setAnimation(a2);
        img.setAnimation(a1);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageViewerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        //coordinates = (TextView) findViewById(R.id.coordinates);
        //addressText = (TextView) findViewById(R.id.addrTextView);


    }

    //onActivityResult() functions right after image is taken
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                //BitmapFactory to decode the image path into bitmap
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                Bitmap bmp = BitmapFactory.decodeFile(mCurrentPath, bmpFactoryOptions);
                //imageView = findViewById(R.id.imageView);
                //imageView.setImageBitmap(bmp);
                Log.i("LOCATION","Address: " + finaladdress);
                Log.i("LOCATION", "latitude: " + loc_lat);
                Log.i("LOCATION", "longitude: " + loc_long);

                getPhoneModel();

                try {
                    getBytesFromBitmap(bmp);
                    sendHttpRequest();

                    //Log.i("PDF", receivedimg);
                    //pdfbyte = Base64.decode(receivedimg, Base64.DEFAULT);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    //method to launch the camera
    public void onLaunchCamera() throws Exception {

        Log.i("ONLAUNCHCAMERA", "function working");
        //intent to launch phone's camera
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photofile = null;
        try {
            //createFile() returns File
            photofile = createFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (photofile!=null) {
            photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", createFile());
            Log.i(TAG, "photofile not null");

            //get file path for debugging purposes
            String photoString = photoURI.getPath();
            Log.i(TAG, "Photo String: " + photoString);

            //write it on storage
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    //method to create file name for image
    public File createFile() throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(imageFileName,  /* prefix */".jpg",         /* suffix */storageDir);    /* directory */

        mCurrentPath = image.getAbsolutePath();
        Log.i(TAG, "Name of file: " + mCurrentPath);
        return image;
    }


    //method to get phone model
    public void getPhoneModel() {

        manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;
        Log.i(TAG, "Phone model: " + manufacturer + " " + model);
    }

    //method to get the address from the latitude and longitude retrieved by the phone's GPS

    //method to request permission to access/write information to storage
    public void takePhoto() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
            try {
                Log.i(TAG, "Permission is OK");
                //launch camera if permission is ok
                onLaunchCamera();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //if no permission is available, request from user
        else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            }
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);
        }
    }

    public void getBytesFromBitmap(Bitmap bitmap) throws JSONException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        baos = stream.toByteArray();
        imgString = Base64.encodeToString(baos, Base64.NO_WRAP);
        Log.i(TAG, "Byte: " + imgString);

    }

    public void sendHttpRequest() {

        finaladdress = getAddress(loc_lat, loc_long);
        Log.i("ADDRESS", finaladdress);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone_brand", manufacturer);
            jsonObject.put("location", finaladdress);
            jsonObject.put("latitude", loc_lat);
            jsonObject.put("longitude", loc_long);
            jsonObject.put("image", imgString);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("VOLLEY", response.toString());
                    SSC = response;
                    Log.i("VOLLEYRESPONSE", SSC.toString());


                    try {
                        receivedimg = SSC.getString("pdf_img");
                        ssc_value = SSC.getString("ssc");
                        Log.i("PDF", receivedimg);
                    }
                    catch (JSONException j) {
                        j.printStackTrace();
                    }

                    Log.i("RESPONSE", ssc_value);
                    Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anime_bottom_to_top);
                    sscvalue.setAnimation(a);
                    sscvalue.setText(ssc_value);

                    try {
                        pdfbyte = Base64.decode(receivedimg, DEFAULT);
                        Bitmap receivedimgbitmap = BitmapFactory.decodeByteArray(pdfbyte, 0, pdfbyte.length);
                        img.setImageBitmap(receivedimgbitmap);

                    }

                    catch (NullPointerException e) {
                        e.printStackTrace();
                    }


                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.i("RESPONSE ERROR", error.toString());
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Log.i("VOLLEY", jsonObjectRequest.toString());
            Log.i("VOLLEY", "Object: " + jsonObject.toString());

            Volley.newRequestQueue(this).add(jsonObjectRequest);


        } catch (JSONException j) {
            Log.i("JSON", j.toString());
        }

        Log.i("VOLLEY", "Request OK");
    }


}

