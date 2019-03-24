package com.example.fawwazazrin.cleanmyriver_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class DemoActivity extends AppCompatActivity {

    Animation a1, a2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        TextView t1 = (TextView) findViewById(R.id.t1);
        TextView t2 = (TextView) findViewById(R.id.t2);

        a1 = AnimationUtils.loadAnimation(this, R.anim.anime_top_to_bottom);
        t1.setAnimation(a1);

        a2 = AnimationUtils.loadAnimation(this, R.anim.anime_bottom_to_top);
        t2.setAnimation(a2);


        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoActivity.this, ImageViewerActivity.class);
                startActivity(intent);
            }
        });

    }
}
