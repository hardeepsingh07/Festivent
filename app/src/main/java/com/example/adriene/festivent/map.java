package com.example.adriene.festivent;


import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseCloud;

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

public class map extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG_EVENTS = "events";
    private static final String TAG_NAME = "name";
    private static final String TAG_TEXT = "text";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_URL = "url";
    private static final String TAG_START = "start";
    private static final String TAG_LOCAL = "local";
    private static final String TAG_END = "end";
    private static final String TAG_LOGO = "logo";

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
    public HashMap<String, String> param = new HashMap<String, String>();
    public JSONObject data;
    public JSONArray events = null;
    public boolean Run = true, dataIncoming = false;
    public HashMap<Marker, EventInfo> markerHash;
    public static ArrayList<EventInfo> myEvents = new ArrayList<EventInfo>();
    public int radius = 1000;
    public static double newLatitude, newLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //intialize the variables and hashmap
        filterFab = (FloatingActionButton) findViewById(R.id.filterFab);
        mFab = (FloatingActionButton) findViewById(R.id.mfab);
        pBar = (ProgressBar) findViewById(R.id.pMap);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        prefs = PreferenceManager.getDefaultSharedPreferences(map.this);
        markerHash = new HashMap<Marker, EventInfo>();


        //get Shared Preferences
        try {
            GPS gps = new GPS(map.this);
            latitude = Double.parseDouble(prefs.getString("latitude", ""));
            longitude = Double.parseDouble(prefs.getString("longitude", ""));
            gps.convertGEO(latitude,longitude);
            zipcode = gps.getZipcode();
            dataIncoming = true;
        } catch (Exception e) {
            Toast.makeText(map.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            latitude = 0.0;
            longitude = 0.0;
        }

        //get desired date and settings
        String miles = prefs.getString("miles", "25 Miles").substring(0,3).trim() + "mi";
        String temp = prefs.getString("time", "1 Day");
        String increment;
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

        //Prepare the Hash
        param.put("location", zipcode);
        param.put("startDate", getDate());
        param.put("endDate", getDateIncrement(Integer.parseInt(increment)));
        param.put("within", miles);
        param.put("page", "1");

        //execute the parse call
        if(zipcode != null) {
            new MyTask().execute();
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(map.this, "Cannot find location please try again", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(map.this, Main.class);
            startActivity(i);
            finish();
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    gps = new GPS(map.this);
                    gps.showSettingsAlert();
                    dataIncoming = false;
                }
                Run = true;
                dataIncoming = false;
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(map.this);
            }
        });

        filterFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

    }

    public String getDate() {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        result += sdf.format(new Date());
        return result;
    }

    public String getDateIncrement(int increment) {
        String result = "";
        String current = getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(current));
            c.add(Calendar.DATE, increment);
            result += sdf.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
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

    //once have the data populate the map
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
            Intent j = new Intent(map.this, EventPage.class);
            j.putExtra("event", event);
            startActivity(j);
        }
    };

    //Create Random Geo Points
    public static void findRandomPoints(double lat, double log, int radius) {
            Random random = new Random();
            // Convert radius from meters to degrees
            double radiusInDegrees = radius / 111000f;
            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(log);
            newLatitude = new_x + lat;
            newLongitude = y + log;
    }

    //Plot the Markers using HashMap
    public void plotMarkers()  {
        mMap.clear();
        if(myEvents.size() > 0) {
            for(EventInfo event: myEvents) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(event.getLatitude(), event.getLongitude()));
                Marker currentMarker = mMap.addMarker(markerOptions);
                markerHash.put(currentMarker, event);
                mMap.setInfoWindowAdapter(new MarkerAdapter(map.this, markerHash));
            }
        }
    }

    public void showFilterDialog() {
        final ArrayList<String> selectedList = new ArrayList<>();
        final String [] options = {"Evenbrite", "Facebook", "Yelp", "Ticket Master"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(map.this);
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
                        Toast.makeText(map.this, selectedList.toString(), Toast.LENGTH_SHORT).show();
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
            try {
                data = new JSONObject((String) ParseCloud.callFunction("getEventbriteEvents", param));

                if(data != null) {
                    //Get JSON Array node
                    events = data.getJSONArray(TAG_EVENTS);

                    //loop through each event
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject e = events.getJSONObject(i);

                        //get name object from  list
                        JSONObject name = e.getJSONObject(TAG_NAME);
                        String eventName = name.getString(TAG_TEXT);

                        JSONObject description = e.getJSONObject(TAG_DESCRIPTION);
                        String desc = description.getString(TAG_TEXT);
                        String url = e.getString(TAG_URL);
                        if(desc == null) {
                            desc = "No Description";
                        }

                        if(url == null) {
                            url = "Website not provided";
                        }

                        //get startTime object
                        JSONObject start = e.getJSONObject(TAG_START);
                        String startTime = start.getString(TAG_LOCAL);

                        //get endTime Object
                        JSONObject end = e.getJSONObject(TAG_END);
                        String endTime = end.getString(TAG_LOCAL);

                        //get logo object
                        String imageUrl;
                        if (!e.isNull("logo")) {
                            JSONObject logo = e.getJSONObject(TAG_LOGO);
                            imageUrl = logo.getString(TAG_URL);
                        } else {
                            imageUrl = null;
                        }
                        findRandomPoints(latitude, longitude, radius);
                        myEvents.add(new EventInfo(eventName, desc, startTime, endTime, url, imageUrl, newLatitude, newLongitude));
                    }
                }
            } catch (final Exception e) {
                final String s = e.toString();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(map.this, s , Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pBar.setVisibility(View.GONE);
            plotMarkers();
            super.onPostExecute(aVoid);
        }
    }
}
