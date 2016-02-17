package com.festivent.hardeep.festivent;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adriene.festivent.R;

/**
 * Created by Hardeep Singh on 12/4/2015.
 */
public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    public TextView title;
    public TextView description;
    public TextView date;
    public TextView source;
    public ImageView imageView;
    public CardView cv;

    private ClickListener clickListener;

    public ViewHolder(View v) {
        super(v);
        cv = (CardView) v.findViewById(R.id.card_view);
        title = (TextView) v.findViewById(R.id.label);
        date = (TextView) v.findViewById(R.id.dateTextView);
        source = (TextView) v.findViewById(R.id.textViewSource);
        imageView = (ImageView) v.findViewById(R.id.icon);

        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        //Pass false because single click
        clickListener.onClick(v, getPosition(), false);
    }

    @Override
    public boolean onLongClick(View v) {
        //Pass true because long click
        clickListener.onClick(v, getPosition(), true);
        return true;
    }

    //Interface which can be used by the Adapter
    public interface ClickListener {
        public void onClick(View v, int position, boolean isLongClick);
    }

}
