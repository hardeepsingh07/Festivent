package com.festivent.hardeep.festivent;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Hardeep Singh on 10/29/2015.
 */
public class RecentListAdapter extends ArrayAdapter<MainLocationInfo> {
    public Context context;
    public ArrayList<MainLocationInfo> myItems;

    public RecentListAdapter(Context context, int resource, ArrayList<MainLocationInfo> myItems) {
        super(context, resource, myItems);
        this.context = context;
        this.myItems = myItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = super.getView(position, convertView, parent);
        MainLocationInfo item = myItems.get(position);
        TextView textView1 = (TextView) row.findViewById(android.R.id.text1);
        textView1.setText(item.getName());
        return row;
    }

}
