package com.example.adriene.festivent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseCloud;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.app.Activity.*;

public class list2 extends AppCompatActivity {

    private static final String TAG_EVENTS = "events";
    private static final String TAG_NAME = "name";
    private static final String TAG_TEXT = "text";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_URL = "url";
    private static final String TAG_START = "start";
    private static final String TAG_LOCAL = "local";
    private static final String TAG_END = "end";
    private static final String TAG_LOGO = "logo";


    public double latitude;
    public double longitude;
    public FloatingActionButton fabFilter;
    public ListView theListView;
    public SharedPreferences prefs;
    public String zipcode;
    public HashMap<String, String> param = new HashMap<String, String>();
    public JSONObject data;
    public JSONArray events = null;
    public ProgressBar pBar;
    public ArrayList<EventInfo> myEvents = new ArrayList<EventInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);


        pBar = (ProgressBar) findViewById(R.id.pBar);
        pBar.setVisibility(View.VISIBLE);
        fabFilter = (FloatingActionButton) findViewById(R.id.filterFabList);
        prefs = PreferenceManager.getDefaultSharedPreferences(list2.this);
        theListView = (ListView) findViewById(R.id.listView);

        //get Shared Preferences
        try {
            GPS gps = new GPS(list2.this);
            latitude = Double.parseDouble(prefs.getString("latitude", ""));
            longitude = Double.parseDouble(prefs.getString("longitude", ""));
            gps.convertGEO(latitude,longitude);
            zipcode = gps.getZipcode();
            Toast.makeText(list2.this, zipcode, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            latitude = 0.0;
            longitude = 0.0;
        }

        //Prepare the Hash
        param.put("location", zipcode);
        param.put("startDate", getDate());
        param.put("endDate", getDateIncrement());
        param.put("within", "10mi");
        param.put("page", "1");

        //execute the parse call
        new MyTask().execute();

        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventInfo event = (EventInfo) adapterView.getItemAtPosition(i);
                Toast.makeText(list2.this, event.eventName, Toast.LENGTH_SHORT).show();
            }
        });

        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
    }

    public void showFilterDialog() {
        final ArrayList<String> selectedList = new ArrayList<>();
        final String [] options = {"Evenbrite", "Facebook", "Yelp", "Ticket Master"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(list2.this);
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
                        Toast.makeText(list2.this, selectedList.toString(), Toast.LENGTH_SHORT).show();
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

    public String getDate() {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        result += sdf.format(new Date());
        return result;
    }

    public String getDateIncrement() {
        String result = "";
        String current = getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(current));
            c.add(Calendar.DATE, 1);
            result += sdf.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
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

                        //get startTime object
                        JSONObject start = e.getJSONObject(TAG_START);
                        String startTime = start.getString(TAG_LOCAL);

                        //get endTime Object
                        JSONObject end = e.getJSONObject(TAG_END);
                        String endTime = end.getString(TAG_LOCAL);

                        //get logo object
                        JSONObject logo = e.getJSONObject(TAG_LOGO);
                        String imageUrl = logo.getString(TAG_URL);

                        myEvents.add(new EventInfo(eventName, desc, startTime, endTime, url, imageUrl));
                        Toast.makeText(list2.this, "Done Parsing!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (final Exception e) {
                final String s = e.toString();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(list2.this, s , Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pBar.setVisibility(View.GONE);
            ListAdapter listAdapter = new MyCustomAdapter(list2.this, myEvents);
            theListView.setAdapter(listAdapter);
            super.onPostExecute(aVoid);
        }
    }
}
