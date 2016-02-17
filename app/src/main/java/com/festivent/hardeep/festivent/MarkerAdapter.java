package com.festivent.hardeep.festivent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adriene.festivent.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by Hardeep Singh on 10/29/2015.
 */
public class MarkerAdapter implements GoogleMap.InfoWindowAdapter {

    public Context context;
    public HashMap<Marker, EventInfo> markerHash;

    public MarkerAdapter(Context context, HashMap<Marker, EventInfo> markerHash) {
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

        EventInfo e = markerHash.get(marker);

        final ImageView markerIcon = (ImageView) v.findViewById(R.id.markerImage);
        final TextView markerTitle = (TextView) v.findViewById(R.id.markerTitle);

        markerTitle.setText(e.getEventName() + "");
        String url = e.getImageUrl();

        if (url != null) {
            Picasso.with(context)
                    .load(url)
                    .into(markerIcon);
        }
        return v;
    }
}
