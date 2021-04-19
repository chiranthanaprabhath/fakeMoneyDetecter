package com.example.moneyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProgressActivity extends AppCompatActivity {
    ImageView xx,xxx;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        xx=findViewById(R.id.xx);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        xxx=findViewById(R.id.xxx);
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.animationrotate);
        xx.startAnimation(anim);
        xxx.startAnimation(anim);
        mDatabase.child("UserDetails").child("M").child("status").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String data =(String) dataSnapshot.child("DamageType").getValue();
                        //Log.d("dd", data);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("dd", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

    }
}