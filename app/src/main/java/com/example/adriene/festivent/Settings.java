package com.example.adriene.festivent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Switch mySwitch;
    String selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        mySwitch = (Switch) findViewById(R.id.switch_default);
        mySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    Toast.makeText(Settings.this, "Switch is on", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Settings.this, "Switch is off", Toast.LENGTH_SHORT).show();
                }

            }
        });


        spinnerSetup_time();
        spinnerSetup_radius();

    }

    public void spinnerSetup_time(){
        Spinner spinnerT = (Spinner) findViewById(R.id.spinner_time);
        spinnerT.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerT.setAdapter(adapter);
    }

    public void spinnerSetup_radius(){
        Spinner spinnerR = (Spinner) findViewById(R.id.spinner_radius);
        spinnerR.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.radius_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerR.setAdapter(adapter);
    }



    //Listeners For Spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        selected = parent.getItemAtPosition(pos).toString();
        Toast.makeText(Settings.this, selected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
