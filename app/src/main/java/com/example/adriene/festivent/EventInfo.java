package com.example.adriene.festivent;

import android.graphics.Bitmap;

/**
 * Created by adriene on 10/24/15.
 */
public class EventInfo {
    Bitmap image;
    String eventName, description;
    String startDate, eventTime;
    double latitude, longitude;

    public EventInfo(){
        this.image = null;
        this.eventName = "";
        this.description = "";
        this.startDate = "";
        this.eventTime = "";
        this.latitude = 0;
        this.longitude = 0;
    }

    public EventInfo(String eventName){
        this.image = null;
        this.eventName = eventName;
        this.description = "";
        this.startDate = "";
        this.eventTime = "";
        this.latitude = 0;
        this.longitude = 0;
    }


    //Setters
    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    //Custom Setters
    public void setEventPosition(double longitude, double latitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setWhen(String startDate, String eventTime){
        this.startDate = startDate;
        this.eventTime = eventTime;
    }

    //Getters
    public Bitmap getImage() {
        return image;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
