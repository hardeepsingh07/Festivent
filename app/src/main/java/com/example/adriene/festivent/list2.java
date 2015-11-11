package com.example.adriene.festivent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.example.adriene.festivent.EventInfo;

import java.util.ArrayList;

public class list2 extends AppCompatActivity {

    public double latitude;
    public double longitude;
    public FloatingActionButton fabFilter;
    public SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);

        EventInfo[] events = {new EventInfo("Event 1"), new EventInfo("Event 2"), new EventInfo("Event 3")};

        fabFilter = (FloatingActionButton) findViewById(R.id.filterFabList);
        prefs = PreferenceManager.getDefaultSharedPreferences(list2.this);
        ListAdapter listAdapter = new MyCustomAdapter(this,events);
        ListView theListView = (ListView) findViewById(R.id.listView);
        theListView.setAdapter(listAdapter);

        //get Shared Preferences
        try {
            latitude = Double.parseDouble(prefs.getString("latitude", ""));
            longitude = Double.parseDouble(prefs.getString("longitude", ""));
        } catch (Exception e) {
            latitude = 0.0;
            longitude = 0.0;
        }

        Toast.makeText(list2.this, latitude + "|" + longitude,Toast.LENGTH_SHORT).show();

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
}
