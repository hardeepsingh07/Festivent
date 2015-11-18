package com.example.adriene.festivent;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.text.ParseException;
import java.util.Date;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adriene.festivent.EventInfo;

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
    public MyCustomAdapter(Context context, ArrayList<EventInfo> events){
        super(context,R.layout.rowlayout,events);
        this.context = context;
    }

    public View getView(int position,View convertView, ViewGroup parent){
        EventInfo event = getItem(position);
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

        ImageView theImageView = (ImageView) convertView.findViewById(R.id.icon);
        theImageView.setImageResource(R.drawable.ic_play_light);
        return convertView;
    }
}




