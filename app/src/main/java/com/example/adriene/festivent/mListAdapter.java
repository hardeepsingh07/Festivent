package com.example.adriene.festivent;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.ArrayList;

/**
 * Created by Hardeep Singh on 10/29/2015.
 */
public class mListAdapter extends ArrayAdapter<MainLocatioonInfo> {
    public Context context;
    public ArrayList<MainLocatioonInfo> myItems;

    public mListAdapter(Context context, int resource, ArrayList<MainLocatioonInfo> myItems) {
        super(context, resource, myItems);
        this.context = context;
        this.myItems = myItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = super.getView(position, convertView, parent);
        MainLocatioonInfo item = myItems.get(position);
        TextView textView1 = (TextView) row.findViewById(android.R.id.text1);
        textView1.setText(item.getName());
        return row;
    }

}
