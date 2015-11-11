package com.example.adriene.festivent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class EventPage extends AppCompatActivity {

    public double latitude;
    public double longitude;
    public TextView startTime, address, phoneNumber, description;
    public String title, describe, sTime, eTime, adresses, pNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Event Title");
        toolbar.setSubtitle("Event Small Description");
        setSupportActionBar(toolbar);

        startTime = (TextView)  findViewById(R.id.textTime);
        address = (TextView) findViewById(R.id.textAddress);
        phoneNumber = (TextView) findViewById(R.id.textPhone);
        description = (TextView) findViewById(R.id.textDescription);

        Intent i = getIntent();
        try {
            latitude = Double.parseDouble(i.getStringExtra("latitude"));
            longitude = Double.parseDouble(i.getStringExtra("longitude"));
        } catch (Exception e) {
            latitude = 0.0;
            longitude = 0.0;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?f=d&daddr=%f,%f?z=12", latitude, longitude);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                i.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(i);
            }
        });


        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                Intent i = new Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                 .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, sTime + "5.00")
                                 .putExtra(CalendarContract.Events.TITLE, title + "A Test Event from android app")
                                  .putExtra(CalendarContract.Events.ALL_DAY, true)
                                  .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                          startActivity(i);

            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?f=d&daddr=%f,%f?z=12", latitude, longitude);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                i.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(i);
            }
        });

        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + pNumber + "0000000000"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventPage.this, "Description was clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
