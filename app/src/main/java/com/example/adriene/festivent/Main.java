package com.example.adriene.festivent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 420;
    public static AutoCompleteTextView ac;
    public static ImageButton search;
    public static FloatingActionButton fab;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    public double latitude, longitude;
    public String wordLocation;
    public GPS gps;
    public boolean trigger = false;
    public SharedPreferences prefs;
    public CharSequence primaryText = "";
    public CharSequence secondaryText = "";
    private ArrayList<MainLocatioonInfo> recentItems = new ArrayList<>();
    public ListView mListView;
    public ArrayAdapter<MainLocatioonInfo> adapterForList;
    public Gson gson = new Gson();
    private static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get Preference Manager
        prefs = PreferenceManager.getDefaultSharedPreferences(Main.this);


        //setup place lookup client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        //Set up a toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Intialize the varibale and views
        fab = (FloatingActionButton) findViewById(R.id.fab);
        ac = (AutoCompleteTextView) findViewById(R.id.ac);
        search = (ImageButton) findViewById(R.id.sIButton);
        mListView = (ListView) findViewById(R.id.mListView);
        TextView header = new TextView(Main.this);
        header.setText("Recent Searches: ");
        mListView.addHeaderView(header);

        //list to click on ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainLocatioonInfo item = recentItems.get(position - 1);
                //put data in prefs
                prefs.edit().putString("latitude", item.getLatitude() + "").commit();
                prefs.edit().putString("longitude", item.getLongitude() + "").commit();

                //trigger the swtich dialog
                switchDialog();
            }
        });

        //listen to auto-complete option click
        ac.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //hide keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(
                        ac.getWindowToken(), 0);

                final AutocompletePrediction item = mAdapter.getItem(position);
                final String placeId = item.getPlaceId();
                primaryText = item.getPrimaryText(null);
                secondaryText = item.getSecondaryText(null);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        });

        // call adapter and set to AutoCompleteView
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS,
                        null);
        ac.setAdapter(mAdapter);


        //listen to fab button click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Enable GPS", Snackbar.LENGTH_LONG).setAction("Lets Go!", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gps = new GPS(Main.this);
                        if(gps.canGetLocation) {
                            //get latitude and longitude then turn into address for AutoComplete Text
                            wordLocation = gps.convertGEO(gps.getLatitude(), gps.getLongitude());
                            ac.setText(wordLocation);
                        } else {
                            gps.showSettingsAlert();
                        }
                    }
                }).show();
            }
        });

        //listen to search button click
        search.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(ac.getText().toString().trim())) {
                    //put data in prefs
                    prefs.edit().putString("latitude", latitude + "").commit();
                    prefs.edit().putString("longitude", longitude + "").commit();

                    //show dialog to pick activity to view results
                    switchDialog();

                    //add to ArrayList Appropriately
                    addToRecentItems(primaryText, secondaryText);

                    ac.setText("");
                } else {
                    Toast.makeText(Main.this, "Please select a location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        search.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(checkVoiceRecognition()) {
                    Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                    //i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say name of a place...");
                    startActivityForResult(i, VOICE_RECOGNITION_REQUEST_CODE);
                }
                return true;
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
    protected void onPause() {
        super.onPause();
        String jsonRecentItems = gson.toJson(recentItems);
        prefs.edit().putString("recentItems", jsonRecentItems).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Type type = new TypeToken<ArrayList<MainLocatioonInfo>>(){}.getType();
        String jsonRecentItems = prefs.getString("recentItems", "");
        if(!jsonRecentItems.equals("")) {
            recentItems.clear();
            recentItems = gson.fromJson(jsonRecentItems, type);
        }
        adapterForList = new mListAdapter(Main.this, android.R.layout.simple_list_item_1, recentItems);
        mListView.setAdapter(adapterForList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && data != null) {
            ArrayList<String> temp = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(temp != null) {
                ac.setText(temp.get(0));
            } else {
                Toast.makeText(Main.this, "Please try again", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
            Toast.makeText(Main.this, "Audio Error", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
            Toast.makeText(Main.this, "Client Error", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
            Toast.makeText(Main.this, "Network Error", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
            Toast.makeText(Main.this, "No Match Error", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
            Toast.makeText(Main.this, "Server Error", Toast.LENGTH_SHORT).show();
        }

    }

    private void addToRecentItems(CharSequence primaryText, CharSequence secondaryText) {
        String item = primaryText.toString() + " " + secondaryText.toString();
        int size = recentItems.size();
        boolean check = contains(item);
        if(!check) {
            if (size >= 5) {
                recentItems.remove(size - 1);
                recentItems.add(0, new MainLocatioonInfo(item, latitude, longitude));
            } else {
                recentItems.add(0, new MainLocatioonInfo(item, latitude, longitude));
            }
            adapterForList.notifyDataSetChanged();
        }
    }

    public boolean contains(String name) {
        for (MainLocatioonInfo item : recentItems) {
            if (item.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            Intent i = new Intent(Main.this, map.class);
            startActivity(i);
        } else if (id == R.id.nav_list) {
            Intent i = new Intent(Main.this, list2.class);
            startActivity(i);
        } else if (id == R.id.nav_settings) {
            Intent i = new Intent(Main.this, Settings.class);
            startActivity(i);
        } else if (id == R.id.nav_save) {
            Intent i = new Intent(Main.this, SavedEvents.class);
            startActivity(i);
        } else if (id == R.id.nav_share) {
            Toast.makeText(Main.this, "Under Construction", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Take care of the results
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            final Place place = places.get(0);
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            places.release();
        }
    };

    //Handle Connection Failed
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    public void switchDialog() {
        Intent i;
        //Ask for type of view
        switch(prefs.getString("dialogChoice", "")) {
            case "map":
                i = new Intent(Main.this, map.class);
                startActivity(i);
                break;
            case "list":
                i = new Intent(Main.this, list2.class);
                startActivity(i);
                break;
            case "":
                AlertDialog.Builder dialog = new AlertDialog.Builder(Main.this);
                dialog.setTitle("How would you like to see your result?");
                dialog.setMultiChoiceItems(new CharSequence[]{"Remember my choice!"}, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected,
                                                boolean isChecked) {
                                if (isChecked) {
                                    trigger = true;
                                }
                            }
                        })
                        .setPositiveButton("Map", new DialogInterface.OnClickListener() {
                            @SuppressLint("CommitPrefEdits")
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (trigger) {
                                    prefs.edit().putString("dialogChoice", "map").commit();
                                }
                                Intent i = new Intent(Main.this, map.class);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("List", new DialogInterface.OnClickListener() {
                            @SuppressLint("CommitPrefEdits")
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (trigger) {
                                    prefs.edit().putString("dialogChoice", "list").commit();
                                }
                                Intent i = new Intent(Main.this, list2.class);
                                startActivity(i);
                            }
                        });
                dialog.create().show();
                break;
        }
    }

    public boolean checkVoiceRecognition() {
        // Check if voice recognition is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            Toast.makeText(Main.this, "Voice recognizer not avaliable/present",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
