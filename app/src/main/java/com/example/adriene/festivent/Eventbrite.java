package com.example.adriene.festivent;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Eventbrite {
    /**
     * Retrieves a json string from EventBrite's server.
     *
     * @param distance  Distance from the search location in miles
     * @param startDate Start date of search. Format is in (YEAR-MONTH-DAY) Ex. (2015-10-7)
     * @param endDate   End date of search. Format is in (YEAR-MONTH-DAY) Ex. (2015-10-7)
     *
     * @return
     */
    public static String getData(String latitude, String longitude, String distance, String startDate, String endDate, String page) {
        String link = "https://www.eventbriteapi.com/v3/events/search/?" +
                "location.within=" + distance +
                "&location.latitude=" + latitude +
                "&location.longitude=" + longitude +
                "&start_date.range_start=" + startDate +
                "Z&page=" + page +
                "&start_date.range_end=" + endDate +
                "Z&token=PZDYIE3MVNSM5Z6EI3B6";
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "application/json");
            conn.connect();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String output;
            String result = "";
            while ((output = br.readLine()) != null) {
                result = result + output;
            }
            conn.disconnect();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}