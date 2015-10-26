package com.example.adriene.festivent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Address;
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

import java.io.IOException;
import java.util.List;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static AutoCompleteTextView ac;
    public static Button search;
    public static FloatingActionButton fab;
    public static String result;
    public double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        ac = (AutoCompleteTextView) findViewById(R.id.ac);
        search = (Button) findViewById(R.id.sMain);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Click Me For Test", Snackbar.LENGTH_LONG).setAction("Open", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Main.this, "It Works!", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationToLL(result);
                AlertDialog.Builder dialog = new AlertDialog.Builder(Main.this);
                dialog.setTitle("View");
                dialog.setMessage("How would you like to see result?");
                dialog.setCancelable(true);
                dialog.setPositiveButton("Map View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Main.this, map.class);
                        i.putExtra("latitude", latitude);
                        i.putExtra("longitude", longitude);
                        startActivity(i);
                    }
                });
                dialog.setNegativeButton("List View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Main.this, list2.class);
                        i.putExtra("latitude", latitude);
                        i.putExtra("longitude", longitude);
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

    public void locationToLL(String input) {
       Geocoder coder = new Geocoder(Main.this);
        List<Address> address;
        try {
            address = coder.getFromLocationName(input, 5);
            Address location = address.get(0);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } catch (Exception e) {
            Toast.makeText(Main.this, "Error have occured in name conversion", Toast.LENGTH_SHORT).show();
        }
    }
}
