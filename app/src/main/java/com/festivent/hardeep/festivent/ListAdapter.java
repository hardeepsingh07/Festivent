package com.festivent.hardeep.festivent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.adriene.festivent.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Hardeep Singh on 11/25/2015.
 */
public class ListAdapter extends RecyclerView.Adapter<ViewHolder> {
    public ArrayList<EventInfo> myEvents;
    public ArrayList<EventInfo> savedEvents;
    public static Context context;
    public boolean call;

    public ListAdapter(Context context, ArrayList<EventInfo> myEvents, ArrayList<EventInfo> savedEvents, boolean call) {
        this.myEvents = myEvents;
        this.savedEvents = savedEvents;
        this.context = context;
        //true for List2.java call and false for SavedEvent.java call
        this.call = call;
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
                if (call) {
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

        String formatted;
        if(myEvents.get(position).getSource().equals("EventBrite")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date newDate = null;
            try {
                newDate = format.parse(myEvents.get(position).getStartDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
            formatted = format.format(newDate);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = null;
            try {
                newDate = format.parse(myEvents.get(position).getStartDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
            formatted = format.format(newDate);
        }
        holder.date.setText(formatted);
        holder.source.setText(myEvents.get(position).getSource());
        String url = myEvents.get(position).getImageUrl();

        if (url != null) {
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.img)
                    .error(R.drawable.img)
                    .resize(300, 180)
                    .into(holder.imageView);
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
        final CharSequence [] options = new CharSequence []{"Save Event", "Share"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Options:");
        dialog.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Save Event")) {
                    savedEvents.add(event);
                    Toast.makeText(context, "Events Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, event.getEventName() + ": " + event.getUrl());
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
                }
            }
        });
        dialog.create().show();
    }

    public void deleteEventDialog(final EventInfo event, final int position) {
        final CharSequence [] options = new CharSequence []{"Delete Event", "Share"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Options:");
        dialog.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(options[which].equals("Delete Event")) {
                    removeItem(position);
                    Toast.makeText(context, "Event Deleted!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, event.getEventName() + ": " + event.getUrl());
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
                }
            }
        });
        dialog.create().show();
    }
}
