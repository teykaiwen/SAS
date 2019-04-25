package com.example.fawwazazrin.cleanmyriver_test;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*

   Splash screen is run right after the app is opened

 */

public class SplashActivity extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 2000;      //Splash time is in millisecond
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /*
                    Note that on some phones the IntroActivity will cause the app the crash
                    depending on the amount of RAM the phone has

                    Default setting will direct the Splash screen to the MainActivity

                 */
                    Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);  //Change MainActivity.class to IntroActivity.class to test if it works on your phone
                    startActivity(mainActivity);
                    finish();
                }

        },SPLASH_TIME_OUT);

        
    }
}
