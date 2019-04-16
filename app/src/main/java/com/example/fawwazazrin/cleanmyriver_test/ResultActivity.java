package com.example.fawwazazrin.cleanmyriver_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import static android.util.Base64.DEFAULT;

public class ResultActivity extends AppCompatActivity {

    private String sscvalue;
    private static float value;
    TextView ssc_show;
    TextView category_text;
    String category;
    CardView cardView;
    ConstraintLayout layout;
    Context context;
    static String pdfimg;
    ImageView img;
    byte[] pdfbyte;
    Animation a1, a2;
    CardView category_container;
    TextView result_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ssc_show = (TextView) findViewById(R.id.result_show);
        category_text = (TextView) findViewById(R.id.category);
        cardView = (CardView) findViewById(R.id.container_category);
        layout = (ConstraintLayout) findViewById(R.id.bg);
        img = (ImageView) findViewById(R.id.img);
        category_container = (CardView) findViewById(R.id.container_category);
        result_show = (TextView) findViewById(R.id.result_show);


        a1 = AnimationUtils.loadAnimation(this, R.anim.anime_top_to_bottom);
        a2 = AnimationUtils.loadAnimation(this, R.anim.anime_bottom_to_top);
        img.setAnimation(a1);
        category_container.setAnimation(a2);
        result_show.setAnimation(a2);


        ImageViewerActivity i = new ImageViewerActivity();
        sscvalue = i.getSSC();
        category = i.getCategory();
        pdfimg = i.getIMG();

        try {
            pdfbyte = Base64.decode(pdfimg, DEFAULT);
            Bitmap receivedimgbitmap = BitmapFactory.decodeByteArray(pdfbyte, 0, pdfbyte.length);
            img.setImageBitmap(receivedimgbitmap);

        }

        catch (NullPointerException e) {
            e.printStackTrace();
        }

        categoryCompare();


        try {
            Log.i("RESULT ACTIVITY", sscvalue);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        ssc_show.setText(sscvalue);
        category_text.setText(category);
        convertString2Float();


    }

    public void convertString2Float() {
        value = Float.parseFloat(sscvalue);
        //categoryMap();


    }

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

    /*
    public void categoryMap() {

        if(value >= 2) {
            category = "Not Healthy";
            category_text.setText(category);
            cardView.setCardBackgroundColor(Color.RED);
            category_text.setTextColor(Color.WHITE);

        }

        if(value < 0) {
            category = "Not Valid";
            category_text.setText(category);
            category_text.setTextColor(Color.BLACK);
        }

        if(value >= 0 && value < 2) {
            category = "Healthy";
            category_text.setText(category);
            cardView.setCardBackgroundColor(Color.GREEN);
            category_text.setTextColor(Color.WHITE);

        }

    } */
}
