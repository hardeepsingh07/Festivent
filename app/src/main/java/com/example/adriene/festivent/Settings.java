package com.example.adriene.festivent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    private Switch mySwitch;
    public SharedPreferences prefs;
    public boolean switchState;
    public TextView clearEvent;
    public String indexMiles, indexTime;
    public String[] times = {"1 Day", "2 Days", "3 Days", "4 Days", "1 Week",
            "2 Weeks", "1 Month", "2 Months"};
    public String[] radius = {"10 Miles", "25 Miles", "50 Miles", "100 Miles"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        prefs = PreferenceManager.getDefaultSharedPreferences(Settings.this);
        mySwitch = (Switch) findViewById(R.id.switch_default);
        clearEvent = (TextView) findViewById(R.id.clearEvents);

        clearEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes I'm Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prefs.edit().remove("savedEvents").apply();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        if (prefs.getString("dialogChoice", "") == "map") {
            mySwitch.setChecked(true);
        } else if (prefs.getString("dialogChoice", "") == "list") {
            mySwitch.setChecked(false);
        }

        mySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    prefs.edit().putString("dialogChoice", "map").apply();
                } else {
                    prefs.edit().putString("dialogChoice", "list").apply();
                }

            }
        });
        final Spinner spinnerT = (Spinner) findViewById(R.id.spinner_time);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, times);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerT.setAdapter(adapter);

        spinnerT.setSelection(prefs.getInt("timeSelection", 0));
        spinnerT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = spinnerT.getSelectedItemPosition();
                prefs.edit().putInt("timeSelection", index).apply();
                String selected = parent.getItemAtPosition(position).toString();
                prefs.edit().putString("time", selected).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner spinnerR = (Spinner) findViewById(R.id.spinner_radius);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, radius);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerR.setAdapter(adapter1);

        spinnerR.setSelection(prefs.getInt("radiusSelection", 1));
        spinnerR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = spinnerR.getSelectedItemPosition();
                prefs.edit().putInt("radiusSelection", index).apply();
                String selected = parent.getItemAtPosition(position).toString();
                prefs.edit().putString("miles", selected).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
