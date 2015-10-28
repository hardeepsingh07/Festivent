package com.example.adriene.festivent;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.example.adriene.festivent.EventInfo;

public class list2 extends AppCompatActivity {

    public double latitude;
    public double longitude;
    public SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);

        prefs = PreferenceManager.getDefaultSharedPreferences(list2.this);
        //get Shared Preferences
        try {
            latitude = Double.parseDouble(prefs.getString("latitude", ""));
            longitude = Double.parseDouble(prefs.getString("longitude", ""));
        } catch (Exception e) {
            latitude = 0.0;
            longitude = 0.0;
        }

        Toast.makeText(list2.this, latitude + "|" + longitude,Toast.LENGTH_SHORT).show();

        EventInfo[] events = {new EventInfo("Event 1"), new EventInfo("Event 2"), new EventInfo("Event 3")};


        ListAdapter listAdapter = new MyCustomAdapter(this,events);
        ListView theListView = (ListView) findViewById(R.id.listView);
        theListView.setAdapter(listAdapter);

        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view, int i, long l) {
                EventInfo event = (EventInfo) adapterView.getItemAtPosition(i);

                Toast.makeText(list2.this, event.eventName, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
