package com.example.adriene.festivent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private static ArrayList<EventInfo> myEvents;
    public DisplayImageOptions options;
    public static Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public TextView description;
        public TextView date;
        public ImageView imageView;
        public CardView cv;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            cv = (CardView) v.findViewById(R.id.card_view);
            title = (TextView) v.findViewById(R.id.label);
            description = (TextView) v.findViewById(R.id.descriptionTextView);
            date = (TextView) v.findViewById(R.id.dateTextView);
            imageView = (ImageView) v.findViewById(R.id.icon);
        }

        @Override
        public void onClick(View v) {
            EventInfo event = (EventInfo) myEvents.get(getPosition());
            Intent j = new Intent(context, EventPage.class);
            j.putExtra("event", event);
            context.startActivity(j);
        }
    }

    public MyAdapter(Context context, ArrayList<EventInfo> myEvents) {
        this.myEvents = myEvents;
        this.context = context;
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
        holder.title.setText(myEvents.get(position).getEventName());
        holder.description.setText(myEvents.get(position).getDescription());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(myEvents.get(position).getStartDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        String formatted = format.format(newDate);
        holder.date.setText(formatted);


        holder.imageView.setVisibility(View.GONE);
        //pBar.setVisibility(View.GONE);
        String url = myEvents.get(position).getImageUrl();

        if (url != null) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(url, holder.imageView, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    //pBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    //pBar.setVisibility(View.GONE);
                    holder.imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    holder.imageView.setImageBitmap(bitmap);
                    //pBar.setVisibility(View.GONE);
                    holder.imageView.setVisibility(View.VISIBLE);
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