package com.example.adriene.festivent;

import android.graphics.Bitmap;

/**
 * Created by Hardeep Singh on 11/1/2015.
 */
public class myMarker {

    private String title;
    private String description;
    private double disitance;
    private Bitmap icon;
    private double latitude;
    private double longitude;

    public myMarker(String title, String description, double latitude, double longitude, double disitance) {
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.icon = null;
        this.disitance = disitance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDisitance() {
        return disitance;
    }

    public void setDisitance(double disitance) {
        this.disitance = disitance;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
