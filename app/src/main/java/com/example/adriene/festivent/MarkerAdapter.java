package com.example.adriene.festivent;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

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

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_pause_light)
                .showImageForEmptyUri(R.drawable.ic_play_light)
                .showImageOnFail(R.drawable.ic_setting_light)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
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
        TextView markerTitle = (TextView) v.findViewById(R.id.markerTitle);
        TextView markerDescription = (TextView) v.findViewById(R.id.marketDes);
        TextView distance = (TextView) v.findViewById(R.id.markerDist);

        //markerIcon.setImageBitmap(m.getIcon());
        markerTitle.setText(e.getEventName() + "");
        markerDescription.setText(e.getDescription() + "");
        distance.setText("0.0");

        String url = e.getImageUrl();

        if (url != null) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(url, markerIcon, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    //pBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    //pBar.setVisibility(View.GONE);
                    markerIcon.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    markerIcon.setImageBitmap(bitmap);
                    //pBar.setVisibility(View.GONE);
                    markerIcon.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                    //pBar.setVisibility(View.GONE);
                }
            });
        }


        return v;
    }
}
