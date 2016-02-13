package com.example.adriene.festivent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventPage extends AppCompatActivity {

    public double latitude;
    public double longitude;
    public TextView startTime, address, website, description, titleText;
    public String title, describe, sTime, adresses, url, imageUrl,source;
    public EventInfo event;
    public ImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_event_page);


        //get intent data
        Intent i = getIntent();
        event = (EventInfo) i.getSerializableExtra("event");
        title = event.getEventName();
        describe = event.getDescription();
        sTime = event.getStartDate();
        url = event.getUrl();
        imageUrl = event.getImageUrl();
        source = event.getSource();
        latitude = event.getLatitude();
        longitude = event.getLongitude();

        //get references
        titleText = (TextView) findViewById(R.id.titleText);
        picture = (ImageView) findViewById(R.id.bgheader);
        startTime = (TextView)  findViewById(R.id.textTime);
        address = (TextView) findViewById(R.id.textAddress);
        website = (TextView) findViewById(R.id.textUrl);
        description = (TextView) findViewById(R.id.textDescription);

        //load the image from url
        loadImage();

        //set data
        GPS gps = new GPS(EventPage.this);
        titleText.setText(title);
        startTime.setText(format(sTime));
        website.setText(url);
        description.setText(describe);
        if(source.equals("EventBrite")) {
            address.setText("Not Available");
            address.setClickable(false);
        } else {
            address.setText(gps.convertGEO(latitude, longitude));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(source.equals("Eventful")) {
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?f=d&daddr=%f,%f?z=12", latitude, longitude);
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    i.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(i);
                } else {
                    Toast.makeText(EventPage.this, "Sorry, no direction information available", Toast.LENGTH_SHORT).show();
                }
            }
        });


        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                Intent i = new Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                 .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, sTime)
                                 .putExtra(CalendarContract.Events.TITLE, title)
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

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }

    public String format(String sTime) {
        String formatted;
        if(source.equals("EventBrite")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date newDate = null;
            try {
                newDate = format.parse(event.getStartDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
            formatted = format.format(newDate);
            return formatted;
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = null;
            try {
                newDate = format.parse(event.getStartDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            format = new SimpleDateFormat("MMM dd, yyyy HH:mm a");
            formatted = format.format(newDate);
            return formatted;
        }
    }

    public void loadImage() {
        Picasso.with(EventPage.this)
                .load(imageUrl)
                .resize(200,200)
                .into(picture);
    }
}
