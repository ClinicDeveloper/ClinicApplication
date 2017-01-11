package com.sanmol.clinicapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.sanmol.clinicapplication.activities.MainActivity;

public class SplashActivity extends AppCompatActivity {

    ImageView splshIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splshIV = (ImageView) findViewById(R.id.splshIV);

        Animation blinking = AnimationUtils.loadAnimation(this, R.anim.zoom);
        splshIV.startAnimation(blinking);


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5 * 1000);
    }
}
