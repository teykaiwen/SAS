package com.example.fawwazazrin.cleanmyriver_test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.util.Base64.DEFAULT;

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
    static String receivedimg;
    String imgString;

    /*
        HTTP URL
     */
    String URL = "http://192.168.137.1:5000/upload";

    JSONObject SSC; //gets the JSON file from server
    String ssc_value;   //stores the ssc value
    TextView sscvalue;
    TextView home;
    ImageView img;
    Animation a1, a2;
    static String parse_ssc;    //to parse the ssc to ResultActivity
    String category;    //stores the category received from server
    static String parse_category;   //to parse the category to ResultActivity
    TextView result_show;
    ConstraintLayout layout;
    TextView valueclick;    //TextView when clicked, direct user to ResultActivity

    public ImageViewerActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview2);

        CardView ssccard = (CardView) findViewById(R.id.ssccard);
        sscvalue = (TextView) findViewById(R.id.sscvalue);
        home = (TextView) findViewById(R.id.homebutton);
        img = (ImageView) findViewById(R.id.img);
        result_show = (TextView) findViewById(R.id.result_show);
        layout = (ConstraintLayout) findViewById(R.id.bg);
        valueclick = (TextView) findViewById(R.id.valueclick);

        valueclick.setVisibility(View.INVISIBLE);


        /*
            To start the camera intent
         */
        takePhoto();

        a1 = AnimationUtils.loadAnimation(this, R.anim.anime_top_to_bottom);
        a2 = AnimationUtils.loadAnimation(this, R.anim.anime_bottom_to_top);

        ssccard.setAnimation(a1);
        home.setAnimation(a2);
        //img.setAnimation(a1);
        result_show.setAnimation(a2);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageViewerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ssccard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageViewerActivity.this, ResultActivity.class);
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

        //when photo file is not null
        if (photofile!=null) {

            //get file URI from image
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

        //gets image file path
        mCurrentPath = image.getAbsolutePath();
        Log.i(TAG, "Name of  file: " + mCurrentPath);
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

    /*
        Method to convert the image into a 64 byte string array
     */
    public void getBytesFromBitmap(Bitmap bitmap) throws JSONException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        baos = stream.toByteArray();
        imgString = Base64.encodeToString(baos, Base64.NO_WRAP);
        Log.i(TAG, "Byte: " + imgString);

    }

    /*
        Method to send http request
     */

    public void sendHttpRequest() {

        //gets the address from the address generator method
        finaladdress = getAddress(loc_lat, loc_long);

        //if finaladdress is not found, return address not found
        if(finaladdress == null) {
            finaladdress = "Address not found";
        }
        Log.i("ADDRESS", finaladdress);

        /*
            Encapsulate the relevant information to a JSON file
         */
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone_brand", manufacturer);
            jsonObject.put("location", finaladdress);
            jsonObject.put("latitude", loc_lat);
            jsonObject.put("longitude", loc_long);
            jsonObject.put("image", imgString);

            //send an object request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("VOLLEY", response.toString());
                    SSC = response;
                    Log.i("VOLLEYRESPONSE", SSC.toString());    //logs the response to the logcat

                    /*
                        Try to get the string from the JSON file sent by the server
                     */

                    try {
                        receivedimg = SSC.getString("pdf_img");
                        ssc_value = SSC.getString("ssc");
                        category = SSC.getString("cat");
                        Log.i("PDF", receivedimg);
                        Log.i("CATEGORY", category);
                    }
                    catch (JSONException j) {
                        j.printStackTrace();
                    }

                    Log.i("RESPONSE", ssc_value);   //logs the response
                    Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anime_bottom_to_top);
                    sscvalue.setAnimation(a);
                    sscvalue.setText(ssc_value);
                    result_show.setText(category);
                    valueclick.setVisibility(View.VISIBLE);
                    categoryCompare();
                    setSSC(ssc_value);
                    setCategory(category);

                    try {
                        pdfbyte = Base64.decode(receivedimg, DEFAULT);
                        Bitmap receivedimgbitmap = BitmapFactory.decodeByteArray(pdfbyte, 0, pdfbyte.length);
                        //img.setImageBitmap(receivedimgbitmap);

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

            //gets json request
            Volley.newRequestQueue(this).add(jsonObjectRequest);


        } catch (JSONException j) {
            Log.i("JSON", j.toString());
        }

        Log.i("VOLLEY", "Request OK");
    }

    /*
        method to set the SSC value obtained from server
     */
    public final void setSSC(String ssc_value) {
        parse_ssc = ssc_value;
        Log.i(TAG, parse_ssc);
    }

    /*
        method to set the category parsed from server
     */
    public final void setCategory(String category) {
        parse_category = category;
        Log.i(TAG, "parse category: " + parse_category);
    }

    /*
        Returns the SSC value
     */
    public static String getSSC() {
        return parse_ssc;

    }

    /*
        Returns the category
     */
    public String getCategory() {
        return parse_category;
    }

    /*
        Returns the received image
     */
    public String getIMG() {
        return receivedimg;
    }

    /*
        Methods to compare category received from server
     */
    public void categoryCompare() {
        if(category.equals("Dirty")) {
            Log.i("CATEGORY COMPARE", "it is dirty");
            layout.setBackgroundResource(R.drawable.red_gradient);
        }

        else if(category.equals("Average")) {
            Log.i("CATEGORY COMPARE", "it is average");
            layout.setBackgroundResource(R.drawable.orangegradient);
        }

        else if(category.equals("Clean")) {
            Log.i("CATEGORY COMPARE", "it is clean");
            layout.setBackgroundResource(R.drawable.green_gradient);
        }
    }


}

