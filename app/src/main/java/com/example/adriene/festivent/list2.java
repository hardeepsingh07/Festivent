package com.example.adriene.festivent;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;

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
        AlertDialog dialog;
        final CharSequence[] items = {" Remember my choice "};
        // arraylist to keep the selected items
        final ArrayList seletedItems=new ArrayList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select The Difficulty Level");
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {

                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        dialog = builder.create();//AlertDialog dialog; create like this outside onClick
        dialog.show();


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
