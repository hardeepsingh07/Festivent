package com.example.adriene.festivent;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hardeep Singh on 10/29/2015.
 */
public class MarkerAdapter implements GoogleMap.InfoWindowAdapter {

    public Context context;
    public HashMap<Marker, EventInfo> markerHash;
    public DisplayImageOptions options;

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
        TextView markerDescription = (TextView) v.findViewById(R.id.marketDes);
        TextView distance = (TextView) v.findViewById(R.id.markerDist);
        final ProgressBar pBar = (ProgressBar) v.findViewById(R.id.pMarBar);
        pBar.setVisibility(View.GONE);
        markerIcon.setVisibility(View.GONE);

        //markerIcon.setImageBitmap(m.getIcon());
        markerTitle.setText(e.getEventName() + "");
        markerDescription.setText(e.getDescription() + "");
        distance.setText("0.0");

        String url = e.getImageUrl();

        if (url != null) {
            Picasso.with(context)
                    .load(url)
                    .into(markerIcon);
        }
        return v;
    }
}
