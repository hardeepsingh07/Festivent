package com.festivent.hardeep.festivent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.adriene.festivent.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class List extends AppCompatActivity {

    public double latitude;
    public double longitude;
    public FloatingActionButton fabFilter;
    public SharedPreferences prefs;
    public String zipcode;
    public ProgressBar pBar;
    public ArrayList<EventInfo> eventbriteEvents = new ArrayList<EventInfo>();
    public ArrayList<EventInfo> eventfulEvents = new ArrayList<EventInfo>();
    public ArrayList<EventInfo> myEvents = new ArrayList<EventInfo>();
    public ArrayList<EventInfo> sEvents = new ArrayList<EventInfo>();
    public Gson gson = new Gson();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public String miles, increment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);

        //Intialize components
        pBar = (ProgressBar) findViewById(R.id.pBar);
        pBar.setVisibility(View.VISIBLE);
        fabFilter = (FloatingActionButton) findViewById(R.id.filterFabList);
        prefs = PreferenceManager.getDefaultSharedPreferences(List.this);


        //recyclerview for listview
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //get Shared Preferences
        try {
            GPS gps = new GPS(List.this);
            latitude = Double.parseDouble(prefs.getString("latitude", ""));
            longitude = Double.parseDouble(prefs.getString("longitude", ""));
            gps.convertGEO(latitude,longitude);
            zipcode = gps.getZipcode();
        } catch (Exception e) {
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
            Toast.makeText(List.this, "Cannot find location please try again", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(List.this, Main.class);
            startActivity(i);
            finish();
        }

        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        String savedEvents = gson.toJson(sEvents);
        prefs.edit().putString("savedEvents", savedEvents).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Type type = new TypeToken<ArrayList<EventInfo>>(){}.getType();
        String savedEvents = prefs.getString("savedEvents", "");
        if(!savedEvents.equals("")) {
            sEvents.clear();
            sEvents = gson.fromJson(savedEvents, type);
        }
    }


    public void showFilterDialog() {
        final ArrayList<String> selectedList = new ArrayList<>();
        final String [] options = {"Evenbrite", "Eventful"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(List.this);
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
                        Toast.makeText(List.this, selectedList.toString(), Toast.LENGTH_SHORT).show();
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
            //Get EventBrite Events
            String apiData = Eventbrite.getData(latitude + "", longitude + "", miles, "1", Integer.parseInt(increment));
            eventbriteEvents.clear();
            eventbriteEvents = Eventbrite.getDateArray(apiData);

            //Get Eventful Events
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
            mAdapter = new ListAdapter(List.this, myEvents, sEvents, true);
            mRecyclerView.setAdapter(mAdapter);
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
