package com.example.adriene.festivent;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adriene.festivent.EventInfo;
/**
 * Created by adriene on 10/24/15.
 */

//Code implementation reference here
//http://www.newthinktank.com/2014/06/make-android-apps-3/


class MyCustomAdapter extends ArrayAdapter<EventInfo>{
    public MyCustomAdapter(Context context, EventInfo[] events){
        super(context,R.layout.rowlayout,events);
    }

    public View getView(int position,View convertView, ViewGroup parent){
        LayoutInflater theInflater = LayoutInflater.from(getContext());
        View theView = theInflater.inflate(R.layout.rowlayout, parent, false);
        String eventName = getItem(position).getEventName();
        TextView theTextView = (TextView) theView.findViewById(R.id.label);
        theTextView.setText(eventName);

        // Get the ImageView in the layout

        ImageView theImageView = (ImageView) theView.findViewById(R.id.icon);
        theImageView.setImageResource(R.drawable.ic_play_light);
        return theView;
    }
}




