package com.example.adriene.festivent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hardeep Singh on 10/29/2015.
 */
public class MarkerAdapter implements GoogleMap.InfoWindowAdapter{

    public Context context;
    public HashMap<Marker, myMarker> markerHash;
    public MarkerAdapter(Context context, HashMap<Marker, myMarker> markerHash) {
        this.context = context;
        this.markerHash = markerHash;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater layout = LayoutInflater.from(context);
        View v = layout.inflate(R.layout.mapmarker, null);

        myMarker m = markerHash.get(marker);

        ImageView markerIcon = (ImageView) v.findViewById(R.id.markerImage);
        TextView markerTitle = (TextView) v.findViewById(R.id.markerTitle);
        TextView markerDescription = (TextView) v.findViewById(R.id.marketDes);
        TextView distance = (TextView) v.findViewById(R.id.markerDist);

        //markerIcon.setImageBitmap(m.getIcon());
        markerTitle.setText(m.getTitle() + "");
        markerDescription.setText(m.getDescription() + "");
        distance.setText(m.getDisitance() + "");

        return v;
    }
}
