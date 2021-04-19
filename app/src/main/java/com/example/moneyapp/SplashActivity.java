package com.example.moneyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.moneyapp.SharedFunctions.SharedFunctionClass;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final View view = findViewById(R.id.images);
        ImageView ImageviewLogo = findViewById(R.id.imagesn);
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.animation);
        view.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                ImageviewLogo.setImageResource(R.drawable.logob);
                SharedFunctionClass.gotoActivityLogin(getApplicationContext());
            }
        });
    }
}