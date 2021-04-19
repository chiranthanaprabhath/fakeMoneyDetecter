package com.example.moneyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.moneyapp.SharedFunctions.SharedFunctionClass;

public class LoginActivity extends AppCompatActivity {
    LinearLayout homepage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        homepage=findViewById(R.id.login);
        homepage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedFunctionClass.gotoActivityHome(getApplicationContext());
            }
        });
    }
}