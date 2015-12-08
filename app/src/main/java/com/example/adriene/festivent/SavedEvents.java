package com.example.adriene.festivent;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SavedEvents extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ProgressBar progressBar;
    public TextView noEventsMessage;
    public ArrayList<EventInfo> sSEvents = new ArrayList<EventInfo>();
    public Gson gson = new Gson();
    public SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_events);

        prefs = PreferenceManager.getDefaultSharedPreferences(SavedEvents.this);
        progressBar = (ProgressBar) findViewById(R.id.savedPBar);
        noEventsMessage = (TextView) findViewById(R.id.noEvents);
        noEventsMessage.setVisibility(View.GONE);

        //recyclerview for listview
        mRecyclerView = (RecyclerView) findViewById(R.id.saved_recycleView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onPause() {
        super.onPause();
        String savedEvents = gson.toJson(sSEvents);
        prefs.edit().putString("savedEvents", savedEvents).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Type type = new TypeToken<ArrayList<EventInfo>>(){}.getType();
        String savedEvents = prefs.getString("savedEvents", "");
        if(!savedEvents.equals("")) {
            sSEvents.clear();
            sSEvents = gson.fromJson(savedEvents, type);
            mAdapter = new MyAdapter(SavedEvents.this, sSEvents, null, false);
            mRecyclerView.setAdapter(mAdapter);
            progressBar.setVisibility(View.GONE);
            if(sSEvents.isEmpty()) {
                noEventsMessage.setVisibility(View.VISIBLE);
            }
        } else {
            noEventsMessage.setVisibility(View.VISIBLE);
        }
    }
}
