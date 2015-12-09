package com.example.adriene.festivent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

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
















//To write to a file
        /*  File sdCard = Environment.getExternalStorageDirectory();
                        File directory = new File (sdCard.getAbsolutePath() + "/Testing");
                        directory.mkdirs();

                        //Now create the file in the above directory and write the contents into it
                        File file = new File(directory, "data.txt");
                        FileOutputStream fOut = null;
                        try {
                            fOut = new FileOutputStream(file);
                            OutputStreamWriter osw = new OutputStreamWriter(fOut);
                            osw.write(o.toString());
                            osw.flush();
                            osw.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
          */


/*ParseCloud.callFunctionInBackground("getEventbriteEvents", Mapss, new FunctionCallback<Object>() {
            @Override
            public void done(Object object, ParseException e) {
                if(e == null) {
                    try {
                        JSONObject o = new JSONObject((String) object);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Toast.makeText(Welcome.this, e.toString(), Toast.LENGTH_LONG).show();
                    System.out.print(e.toString());
                }
            }
        });*/