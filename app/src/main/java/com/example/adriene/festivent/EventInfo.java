package com.example.adriene.festivent;

import android.graphics.Bitmap;

/**
 * Created by adriene on 10/24/15.
 */
public class EventInfo {
    Bitmap image;
    String eventName, description, startDate, endTime, url, imageUrl;
    double latitude, longitude;

    public EventInfo(String eventName, String description, String startDate,
                     String endTime, String url, String imageUrl){
        this.image = null;
        this.eventName = eventName;
        this.description = description;
        this.startDate = startDate;
        this.endTime = endTime;
        this.url = url;
        this.imageUrl = imageUrl;
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

    public void setUrl(String url)  {   this.url = url; }

    public void setEndTime(String eventTime) {
        this.endTime = endTime;
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
        this.endTime = endTime;
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

    public String getEndTime() {
        return endTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUrl() { return url; }
}
