package com.example.adriene.festivent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.parse.Parse;

public class Welcome extends AppCompatActivity {

    public static final int TIME = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Parse.initialize(this, "yfu0Rc9ON0WmTPczXt82CLnqsrtVIDVctnUGAgkI", "o7ohwJj1zOorCsdgkqVKcNr3GPuupYPRj730I59C");

        //Timer to move to Main Activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Welcome.this, Main.class);
                startActivity(i);
                finish();
            }
        }, TIME);
    }
}
