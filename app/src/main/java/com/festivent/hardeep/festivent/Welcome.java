package com.festivent.hardeep.festivent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.adriene.festivent.R;

public class Welcome extends AppCompatActivity {

    public static final int TIME = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Timer to move to Main Activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkNetwork();
            }
        }, TIME);
    }

    public void checkNetwork() {
        final Intent i = new Intent(Welcome.this, Main.class);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnected()) {
            startActivity(i);
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(Welcome.this);
            dialog.setMessage("Festivent uses internet to retrieve data. Please connect to internet for proper functionality.")
                    .setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(i);
                        }
                    });
            AlertDialog alert = dialog.create();
            alert.show();
        }
    }
}
