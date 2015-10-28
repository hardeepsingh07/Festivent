package com.example.adriene.festivent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.*;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton mFab;
    private ProgressBar pBar;
    public double latitude = 0.0, longitude = 0.0;
    private LatLng latlng;
    public LocationManager locationManager;
    public GPS gps;
    public SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mFab = (FloatingActionButton) findViewById(R.id.mfab);
        pBar = (ProgressBar) findViewById(R.id.pMap);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        prefs = PreferenceManager.getDefaultSharedPreferences(map.this);

        //get Shared Preferences
        try {
            latitude = Double.parseDouble(prefs.getString("latitude", ""));
            longitude = Double.parseDouble(prefs.getString("longitude", ""));
        } catch (Exception e) {
            latitude = 0.0;
            longitude = 0.0;
        }
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(map.this, "Button under construction", Toast.LENGTH_SHORT).show();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        latlng = new LatLng(latitude, longitude);
        mMap.setOnMapLoadedCallback(callback);
    }


    //Animate the Map
    public GoogleMap.OnMapLoadedCallback callback = new GoogleMap.OnMapLoadedCallback() {
        @Override
        public void onMapLoaded() {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));
            mMap.addMarker(new MarkerOptions().position(latlng).title("Your Location"));
            pBar.setVisibility(View.GONE);
        }
    };

 /*
  //Watch location and follows it, not needed it right now
  public GoogleMap.OnMyLocationChangeListener update = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            latlng = new LatLng(latitude, longitude);
            mMap.setOnMapLoadedCallback(callback);
        }
    };
*/

}
