package com.example.adriene.festivent;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adriene.festivent.EventInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by adriene on 10/24/15.
 */

//Code implementation reference here
//http://www.newthinktank.com/2014/06/make-android-apps-3/


class MyCustomAdapter extends ArrayAdapter<EventInfo>{
    public Context context;
    public DisplayImageOptions options;
    public MyCustomAdapter(Context context, ArrayList<EventInfo> events){
        super(context,R.layout.rowlayout,events);
        this.context = context;

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));

        options = new DisplayImageOptions.Builder()
        				.showImageOnLoading(R.drawable.ic_pause_light)
         					.showImageForEmptyUri(R.drawable.ic_play_light)
         					.showImageOnFail(R.drawable.ic_setting_light)
          				    .cacheInMemory(true)
         					.cacheOnDisk(true)
         					.considerExifParams(true)
         					.build();

    }

    public View getView(int position,View convertView, ViewGroup parent){
        final EventInfo event = getItem(position);
        LayoutInflater theInflater = LayoutInflater.from(getContext());
        convertView = theInflater.inflate(R.layout.rowlayout, parent, false);
        TextView title = (TextView) convertView.findViewById(R.id.label);
        title.setText(event.getEventName());

        TextView description = (TextView) convertView.findViewById(R.id.DescriptionTextView);
        description.setText(event.getDescription());

        TextView date = (TextView) convertView.findViewById(R.id.dateTextView);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(event.getStartDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        String formatted = format.format(newDate);
        date.setText(formatted);

        final ProgressBar pBar = (ProgressBar) convertView.findViewById(R.id.pBarList);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        imageView.setVisibility(View.GONE);
        pBar.setVisibility(View.GONE);
        String url = event.getImageUrl();

        if (url != null) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(url, imageView, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    pBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_light));
                    pBar.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                    pBar.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                    pBar.setVisibility(View.GONE);
                }
            });
        }
        return convertView;
    }
}




