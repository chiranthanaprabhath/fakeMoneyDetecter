package com.example.moneyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.moneyapp.SharedFunctions.SharedFunctionClass;

public class FailAcitivity extends AppCompatActivity {
    Button ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fail_acitivity);
        ok=findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedFunctionClass.gotoActivityHome(getApplicationContext());
            }
        });
    }
}