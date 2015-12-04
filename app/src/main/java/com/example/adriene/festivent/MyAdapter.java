package com.example.adriene.festivent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
    public ArrayList<EventInfo> myEvents;
    public DisplayImageOptions options;
    public ArrayList<EventInfo> savedEvents;
    public static Context context;
    public boolean call;

    public MyAdapter(Context context, ArrayList<EventInfo> myEvents, ArrayList<EventInfo> savedEvents, boolean call) {
        this.myEvents = myEvents;
        this.savedEvents = savedEvents;
        this.context = context;
        //true for List2.java call and false for SavedEvent.java call
        this.call = call;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
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
        holder.setClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position, boolean isLongClick) {
                final EventInfo event = (EventInfo) myEvents.get(position);
                if(call) {
                    if (isLongClick) {
                        saveEventDialog(event);
                    } else {
                        Intent j = new Intent(context, EventPage.class);
                        j.putExtra("event", event);
                        context.startActivity(j);
                    }
                } else {
                    if (isLongClick) {
                        deleteEventDialog(event, position);
                    } else {
                        Intent j = new Intent(context, EventPage.class);
                        j.putExtra("event", event);
                        context.startActivity(j);
                    }
                }
            }
        });

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

        String url = myEvents.get(position).getImageUrl();

        if (url != null) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(url, holder.imageView, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    holder.imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    holder.imageView.setImageBitmap(bitmap);
                    holder.imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
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

    public void addItem(int position, EventInfo data) {
        myEvents.add(position, data);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        myEvents.remove(position);
        notifyItemRemoved(position);
    }

    public void saveEventDialog(final EventInfo event) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Would you like to save this event?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savedEvents.add(event);
                Toast.makeText(context, "Events Saved!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.create().show();
    }

    public void deleteEventDialog(final EventInfo event, final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Are you sure you want to remove this event?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeItem(position);
                Toast.makeText(context, "Event Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.create().show();
    }
}