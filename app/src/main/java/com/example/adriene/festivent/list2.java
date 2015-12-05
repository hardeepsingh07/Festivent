package com.example.adriene.festivent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseCloud;

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


public class list2 extends ActionBarActivity implements MyAdapter.ViewHolder.ClickListener {

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
    public SharedPreferences prefs;
    public String zipcode;
    public HashMap<String, String> param = new HashMap<String, String>();
    public JSONObject data;
    public JSONArray events = null;
    public ProgressBar pBar;
    public ArrayList<EventInfo> myEvents = new ArrayList<EventInfo>();
    public ArrayList<EventInfo> sEvents = new ArrayList<EventInfo>();
    public Gson gson = new Gson();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);

        //Intialize components
        pBar = (ProgressBar) findViewById(R.id.pBar);
        pBar.setVisibility(View.VISIBLE);
        fabFilter = (FloatingActionButton) findViewById(R.id.filterFabList);
        prefs = PreferenceManager.getDefaultSharedPreferences(list2.this);


        //recyclerview for listview
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //get Shared Preferences
        try {
            GPS gps = new GPS(list2.this);
            latitude = Double.parseDouble(prefs.getString("latitude", ""));
            longitude = Double.parseDouble(prefs.getString("longitude", ""));
            gps.convertGEO(latitude, longitude);
            zipcode = gps.getZipcode();
        } catch (Exception e) {
            latitude = 0.0;
            longitude = 0.0;
        }

        //get desired date and settings
        String miles = prefs.getString("miles", "25 Miles").substring(0, 3).trim() + "mi";
        String temp = prefs.getString("time", "1 Day");
        String increment;
        if (temp.endsWith("Day") || temp.endsWith("Days")) {
            increment = temp.substring(0, 1);
        } else if (temp.endsWith("Week") || temp.endsWith("Weeks")) {
            increment = temp.substring(0, 1);
            int t = Integer.parseInt(increment);
            t *= 7;
            increment = t + "";
        } else {
            increment = temp.substring(0, 1);
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
        if (zipcode != null) {
            new MyTask().execute();
        } else {
            Toast.makeText(list2.this, "Cannot find location please try again", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(list2.this, Main.class);
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
        prefs.edit().putString("savedEvents", savedEvents).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Type type = new TypeToken<ArrayList<EventInfo>>() {
        }.getType();
        String savedEvents = prefs.getString("savedEvents", "");
        if (!savedEvents.equals("")) {
            sEvents.clear();
            sEvents = gson.fromJson(savedEvents, type);
        }
    }


    public void showFilterDialog() {
        final ArrayList<String> selectedList = new ArrayList<>();
        final String[] options = {"Evenbrite", "Facebook", "Yelp", "Ticket Master"};
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
    public void onItemClick(int position) {
        if (actionMode != null) {
            toggleSelecton(position);
        }
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public boolean onItemLongClick(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);
        return true;
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                data = new JSONObject((String) ParseCloud.callFunction("getEventbriteEvents", param));

                if (data != null) {
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
                        if (desc == null) {
                            desc = "No Description";
                        }

                        if (url == null) {
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
                        myEvents.add(new EventInfo(eventName, desc, startTime, endTime, url, imageUrl, 0.0, 0.0));
                    }
                }
            } catch (final Exception e) {
                final String s = e.toString();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(list2.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pBar.setVisibility(View.GONE);
            mAdapter = new MyAdapter(list2.this, list2.this, myEvents, sEvents, true);
            mRecyclerView.setAdapter(mAdapter);
            super.onPostExecute(aVoid);
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove:
                    Toast.makeText(list2.this, "Remove Option", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            actionMode = null;
        }
    }
}
