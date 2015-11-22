package com.example.adriene.festivent;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by adriene on 10/24/15.
 */
public class EventInfo implements Serializable{
    String eventName, description, startDate, endTime, url, imageUrl;
    double latitude, longitude;

    public EventInfo(String eventName, String description, String startDate,
                     String endTime, String url, String imageUrl){
        this.eventName = eventName;
        this.description = description;
        this.startDate = startDate;
        this.endTime = endTime;
        this.url = url;
        this.imageUrl = imageUrl;
        this.latitude = 0;
        this.longitude = 0;
    }


    public String getImageUrl() { return imageUrl;  }

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
