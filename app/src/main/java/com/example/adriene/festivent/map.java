package com.example.adriene.festivent;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton mFab;
    private ProgressBar pBar;
    public double latitude = 0.0, longitude = 0.0;
    private LatLng latlng;
    public LocationManager locationManager;
    public GPS gps;
    public CameraUpdate location;
    public SharedPreferences prefs;
    public boolean Run = true, dataIncoming = false;
    public HashMap<Marker, myMarker> markerHash;
    public static ArrayList<myMarker> markerArray = new ArrayList<myMarker>();
    public int radius = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //intialize the variables and hashmap
        mFab = (FloatingActionButton) findViewById(R.id.mfab);
        pBar = (ProgressBar) findViewById(R.id.pMap);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        prefs = PreferenceManager.getDefaultSharedPreferences(map.this);
        markerHash = new HashMap<Marker, myMarker>();


        //get Shared Preferences
        try {
            latitude = Double.parseDouble(prefs.getString("latitude", ""));
            longitude = Double.parseDouble(prefs.getString("longitude", ""));
            dataIncoming = true;
        } catch (Exception e) {
            latitude = 0.0;
            longitude = 0.0;
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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        markerHash = new HashMap<Marker, myMarker>();
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
            //add my current location first
            markerArray.add(new myMarker("You are here!", "You current locatoin", latitude, longitude, 0.0));
            //Show events data
            findRandomPoints(latitude, longitude, radius);
            plotMarkers();
            mMap.setOnInfoWindowClickListener(listenClick);
            mMap.animateCamera(location);
            pBar.setVisibility(View.INVISIBLE);
        }
    };

    public GoogleMap.OnInfoWindowClickListener listenClick = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Intent i = new Intent(map.this, EventPage.class);
            i.putExtra("latitude", marker.getPosition().latitude + "");
            i.putExtra("longitude", marker.getPosition().longitude + "");
            startActivity(i);
        }
    };

    //Create Random Geo Points
    public static void findRandomPoints(double lat, double log, int radius) {
        for(int i = 0; i < 6; i++) {
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

            double newLatitude = new_x + lat;
            double newLongitude = y + log;
            markerArray.add(new myMarker("Title" + random.nextInt(100), "Description" + random.nextInt(100), newLatitude, newLongitude, 0.0));
        }
    }

    //Plot the Markers using HashMap
    public void plotMarkers()  {
        mMap.clear();
        if(markerArray.size() > 0) {
            for(myMarker marker: markerArray) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(marker.getLatitude(), marker.getLongitude()));
                Marker currentMarker = mMap.addMarker(markerOptions);
                markerHash.put(currentMarker, marker);
                mMap.setInfoWindowAdapter(new MarkerAdapter(map.this, markerHash));

            }
        }
    }
}
