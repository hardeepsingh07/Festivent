package com.festivent.hardeep.festivent;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.adriene.festivent.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class Mapss extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton mFab, filterFab;
    private ProgressBar pBar;
    public double latitude = 0.0, longitude = 0.0;
    private LatLng latlng;
    public LocationManager locationManager;
    public GPS gps;
    public CameraUpdate location;
    public SharedPreferences prefs;
    public String zipcode;
    public boolean Run = true, dataIncoming = false;
    public HashMap<Marker, EventInfo> markerHash;
    public ArrayList<EventInfo> eventbriteEvents = new ArrayList<EventInfo>();
    public ArrayList<EventInfo> eventfulEvents = new ArrayList<EventInfo>();
    public ArrayList<EventInfo> myEvents = new ArrayList<EventInfo>();
    public static double newLatitude, newLongitude;
    public String miles, increment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //intialize the variables and hashmap
        filterFab = (FloatingActionButton) findViewById(R.id.filterFab);
        mFab = (FloatingActionButton) findViewById(R.id.mfab);
        pBar = (ProgressBar) findViewById(R.id.pMap);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        prefs = PreferenceManager.getDefaultSharedPreferences(Mapss.this);
        markerHash = new HashMap<Marker, EventInfo>();


        //get Shared Preferences
        try {
            GPS gps = new GPS(Mapss.this);
            latitude = Double.parseDouble(prefs.getString("latitude", ""));
            longitude = Double.parseDouble(prefs.getString("longitude", ""));
            gps.convertGEO(latitude,longitude);
            zipcode = gps.getZipcode();
            dataIncoming = true;
        } catch (Exception e) {
            Toast.makeText(Mapss.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            latitude = 0.0;
            longitude = 0.0;
        }

        //get desired date and settings
        miles = prefs.getString("miles", "25 Miles").substring(0,3).trim() + "mi";
        String temp = prefs.getString("time", "1 Day");
        if(temp.endsWith("Day") || temp.endsWith("Days")) {
            increment = temp.substring(0,1);
        } else if(temp.endsWith("Week") || temp.endsWith("Weeks")) {
            increment = temp.substring(0,1);
            int t = Integer.parseInt(increment);
            t *= 7;
            increment = t + "";
        } else {
            increment = temp.substring(0,1);
            int t = Integer.parseInt(increment);
            t *= 30;
            increment = t + "";
        }

        //execute the parse call
        if(zipcode != null) {
            new MyTask().execute();
        } else {
            Toast.makeText(Mapss.this, "Cannot find location please try again", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Mapss.this, Main.class);
            startActivity(i);
            finish();
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    gps = new GPS(Mapss.this);
                    gps.showSettingsAlert();
                    dataIncoming = false;
                }
                Run = true;
                dataIncoming = false;
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(Mapss.this);
            }
        });

        filterFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        // Obtain the SupportMapFragment and get notified when the MyMap is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        markerHash.clear();
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        if(!dataIncoming) {
            mMap.setOnMyLocationChangeListener(update);
        } else {
            latlng = new LatLng(latitude, longitude);
            location = CameraUpdateFactory.newLatLngZoom(latlng, 12);
            mMap.setOnMapLoadedCallback(mapLoaded);
        }
    }

    //Call the update with data
    public GoogleMap.OnMyLocationChangeListener update = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location locale) {
            if(Run) {
                latitude = locale.getLatitude();
                longitude = locale.getLongitude();
                latlng = new LatLng(latitude, longitude);
                location = CameraUpdateFactory.newLatLngZoom(latlng, 12);
                mMap.setOnMapLoadedCallback(mapLoaded);
                Run = false;
            }
        }
    };

    //once have the data populate the Mapss
    public GoogleMap.OnMapLoadedCallback mapLoaded = new GoogleMap.OnMapLoadedCallback() {
        @Override
        public void onMapLoaded() {
            //clean the array from previous values
            myEvents.clear();
            //Show events data
            mMap.setOnInfoWindowClickListener(listenClick);
            mMap.animateCamera(location);
            pBar.setVisibility(View.INVISIBLE);
        }
    };

    public GoogleMap.OnInfoWindowClickListener listenClick = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            EventInfo event = markerHash.get(marker);
            Intent j = new Intent(Mapss.this, EventPage.class);
            j.putExtra("event", event);
            startActivity(j);
        }
    };

    //Create Random Geo Points
//    public static void findRandomPoints(double lat, double log, int radius) {
//            Random random = new Random();
//            // Convert radius from meters to degrees
//            double radiusInDegrees = radius / 111000f;
//            double u = random.nextDouble();
//            double v = random.nextDouble();
//            double w = radiusInDegrees * Math.sqrt(u);
//            double t = 2 * Math.PI * v;
//            double x = w * Math.cos(t);
//            double y = w * Math.sin(t);
//
//            // Adjust the x-coordinate for the shrinking of the east-west distances
//            double new_x = x / Math.cos(log);
//            newLatitude = new_x + lat;
//            newLongitude = y + log;
//    }

    //Plot the Markers using HashMap
    public void plotMarkers()  {
        mMap.clear();
        if(myEvents.size() > 0) {
            for(EventInfo event: myEvents) {
                if(event.getSource().equals("Eventful")) {
                    Log.d("co-ordinate", event.getLatitude() + "," + event.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(event.getLatitude(), event.getLongitude()));
                    Marker currentMarker = mMap.addMarker(markerOptions);
                    markerHash.put(currentMarker, event);
                    mMap.setInfoWindowAdapter(new MarkerAdapter(Mapss.this, markerHash));
                }
            }
        }
    }

    public void showFilterDialog() {
        final ArrayList<String> selectedList = new ArrayList<>();
        final String [] options = {"Evenbrite", "Eventful"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(Mapss.this);
        dialog.setTitle("Filter Results");
        dialog.setMultiChoiceItems(options, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            selectedList.add(options[indexSelected]);
                        } else if (selectedList.contains(options[indexSelected])) {
                            selectedList.remove(options[indexSelected]);
                        }
                    }
                })
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(Mapss.this, selectedList.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        dialog.create().show();
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //Get Eventbrite Events
            String apiData = Eventbrite.getData(latitude + "", longitude + "", miles, "1", Integer.parseInt(increment));
            eventbriteEvents.clear();
            eventbriteEvents = Eventbrite.getDateArray(apiData);

            //Get Eventful events
            String apiData1 = Eventful.getData(latitude + "", longitude + "", miles, "25", Integer.parseInt(increment));
            eventfulEvents.clear();
            eventfulEvents = Eventful.getDataArray(apiData1);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            myEvents.clear();
            myEvents.addAll(eventbriteEvents);
            myEvents.addAll(eventfulEvents);
            pBar.setVisibility(View.GONE);
            plotMarkers();
            super.onPostExecute(aVoid);
        }
    }

    public boolean checkDuplicate(ArrayList<EventInfo> list,  String title) {
        for(EventInfo eventInfo: list) {
            if (eventInfo.getEventName().equals(title)) {
                return true;
            }
        }
        return  false;
    }
}
