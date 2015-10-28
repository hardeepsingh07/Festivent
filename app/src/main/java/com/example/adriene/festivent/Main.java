package com.example.adriene.festivent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    public static AutoCompleteTextView ac;
    public static Button search;
    public static FloatingActionButton fab;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    public double latitude, longitude;
    public String wordLocation;
    public GPS gps;
    public SharedPreferences prefs;
    private static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get preference manager
        prefs = PreferenceManager.getDefaultSharedPreferences(Main.this);
        //setup place lookup client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        //Set up a toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Intialize the varibale and views
        fab = (FloatingActionButton) findViewById(R.id.fab);
        ac = (AutoCompleteTextView) findViewById(R.id.ac);
        search = (Button) findViewById(R.id.sMain);

        ac.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AutocompletePrediction item = mAdapter.getItem(position);
                final String placeId = item.getPlaceId();
                final CharSequence primaryText = item.getPrimaryText(null);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

                Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // call adapter and set to AutoCompleteView
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS,
                        null);
        ac.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Enable GPS", Snackbar.LENGTH_LONG).setAction("Lets Go!", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gps = new GPS(Main.this);
                        if(gps.canGetLocation) {
                            //get latitude and longitude then turn into address for AutoComplete Text
                            wordLocation = gps.convertGEO(gps.getLatitude(), gps.getLongitude());
                            ac.setText(wordLocation);
                        } else {
                            gps.showSettingsAlert();
                        }
                    }
                }).show();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            final CharSequence[] option = {"Remeber my Choice?"};
            @Override
            public void onClick(View v) {
                //put data in prefs
                prefs.edit().putString("latitude", latitude + "").commit();
                prefs.edit().putString("longitude", longitude + "").commit();

                //Ask for type of view
                AlertDialog.Builder dialog = new AlertDialog.Builder(Main.this);
                dialog.setTitle("View");
                dialog.setMessage("How would you like to see result?");
                dialog.setCancelable(true);
                dialog.setPositiveButton("Map View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       Intent i = new Intent(Main.this, map.class);
                        startActivity(i);
                    }
                });
                dialog.setNegativeButton("List View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Main.this, list2.class);
                        startActivity(i);
                    }
                });
                dialog.create().show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            Toast.makeText(Main.this, "Show Map Touched", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Main.this, map.class);
            startActivity(i);
        } else if (id == R.id.nav_list) {
            Toast.makeText(Main.this, "Show List Touched", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Main.this, list2.class);
            startActivity(i);
        } else if (id == R.id.nav_settings) {
            Toast.makeText(Main.this, "filler1 touched", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Main.this, Settings.class);
            startActivity(i);
        } else if (id == R.id.nav_filler2) {
            Toast.makeText(Main.this, "filler2 Touched", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            Toast.makeText(Main.this, "share Touched", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            Toast.makeText(Main.this, "send touched", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
        }
    };

    //Take care of the results
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            final Place place = places.get(0);
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            places.release();
        }
    };

    //Handle Connection Failed
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

}
