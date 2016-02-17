package com.festivent.hardeep.festivent;

/**
 * Created by Hardeep Singh on 10/29/2015.
 */
public class MainLocationInfo {
    public String name;
    public double latitude;
    public double longitude;

    public MainLocationInfo(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude;  }

    public double getLongitude() {
        return longitude;
    }
}
