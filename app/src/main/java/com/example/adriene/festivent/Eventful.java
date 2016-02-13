package com.example.adriene.festivent;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Hardeep on 2/1/2016.
 */
public class Eventful {
    /**
     * Retrieves a json string from Eventful's server.
     *
     * @param distance  Distance from the search location in miles
     * @param startDate Start date of search. Format is in (YEAR-MONTH-DAY) Ex. (2015-10-7)
     * @param endDate   End date of search. Format is in (YEAR-MONTH-DAY) Ex. (2015-10-7)
     *
     * @return
     */


    //http://api.eventful.com/rest/events/search?app_key=LCXcTmdZsCTQnhNZ&where=32.746682,-117.162741&within=10&date=2016020100-2016020200&page_size=50
    public static String getData(String latitude, String longitude, String distance, String startDate, String endDate, String pageSize) {
        String link = "http://api.eventful.com/json/events/search?app_key=LCXcTmdZsCTQnhNZ" +
                "&where=" + latitude + "," + longitude +
                "&within=" + distance +
                "&page_size=" + pageSize +
                "&date=" + startDate + "00," + endDate + "00";
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
                Log.d("eventful", output);
            }
            conn.disconnect();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
