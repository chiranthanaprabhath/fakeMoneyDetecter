package com.example.moneyapp.SharedFunctions;

import android.content.Context;
import android.content.Intent;

import com.example.moneyapp.FailAcitivity;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.LoginActivity;
import com.example.moneyapp.ProgressActivity;
import com.example.moneyapp.SuscessActivity;


public class SharedFunctionClass {

    public static void gotoActivityLogin(Context content){
        Intent i = new Intent(content, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        content.startActivity(i);
    }
    public static void gotoActivityHome(Context content){
        Intent i = new Intent(content, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        content.startActivity(i);
    }
    public static void gotoActivityP(Context content){
        Intent i = new Intent(content, ProgressActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        content.startActivity(i);
    }
    public static void gotoActivityF(Context content){
        Intent i = new Intent(content, FailAcitivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        content.startActivity(i);
    }
    public static void gotoActivityS(Context content){
        Intent i = new Intent(content, SuscessActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        content.startActivity(i);
    }

}
