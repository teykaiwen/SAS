package com.example.fawwazazrin.cleanmyriver_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    Animation a1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView home_help = (TextView) findViewById(R.id.home_help);
        home_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        a1 = AnimationUtils.loadAnimation(this, R.anim.anime_bottom_to_top);
        home_help.setAnimation(a1);

    }



}