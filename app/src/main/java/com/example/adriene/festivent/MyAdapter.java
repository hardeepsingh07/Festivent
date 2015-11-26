package com.example.adriene.festivent;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Hardeep Singh on 11/25/2015.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<EventInfo> myEvents;
    public DisplayImageOptions options;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView date;
        public ImageView imageView;
        public CardView cv;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView) v.findViewById(R.id.card_view);
            title = (TextView) v.findViewById(R.id.label);
            description = (TextView) v.findViewById(R.id.descriptionTextView);
            date = (TextView) v.findViewById(R.id.dateTextView);
            imageView = (ImageView) v.findViewById(R.id.icon);
        }
    }

    public MyAdapter(Context context, ArrayList<EventInfo> myEvents) {
        this.myEvents = myEvents;
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
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rowlayout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ViewHolder holder1 = holder;
        holder1.title.setText(myEvents.get(position).getEventName());
        holder1.description.setText(myEvents.get(position).getDescription());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(myEvents.get(position).getStartDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        String formatted = format.format(newDate);
        holder1.date.setText(formatted);


        holder1.imageView.setVisibility(View.GONE);
        //pBar.setVisibility(View.GONE);
        String url = myEvents.get(position).getImageUrl();

        if (url != null) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(url, holder1.imageView, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    //pBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    //pBar.setVisibility(View.GONE);
                    holder1.imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    holder1.imageView.setImageBitmap(bitmap);
                    //pBar.setVisibility(View.GONE);
                    holder1.imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                    //pBar.setVisibility(View.GONE);
                }
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myEvents.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}