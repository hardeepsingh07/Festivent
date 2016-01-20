package com.example.adriene.festivent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Eventbrite {
    /**
     * Retrieves a json string from EventBrite's server.
     *
     * @param location  location to search
     * @param distance  Distance from the search location in miles
     * @param startDate Start date of search. Format is in (YEAR-MONTH-DAY) Ex. (2015-10-7)
     * @param endDate   End date of search. Format is in (YEAR-MONTH-DAY) Ex. (2015-10-7)
     * @return
     */
    public static String getData(String location, String distance, String startDate, String endDate, String page) {
        String loc = location;
        String dis = distance;
        String sd = startDate;
        String ed = endDate;
        String url = "https://www.eventbriteapi.com/v3/events/search/?location.address=" + loc + "&location.within=" + dis + "mi&start_date.range_start=" + sd + "T00:00:00Z&page=" + page + "&start_date.range_end=" + ed + "T00:00:00Z&token=LN5JA5P7UQXZG26SVTMC";
        System.out.println(url);
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet(url);
            getRequest.addHeader("accept", "application/json");

            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String output;
            String result = "";
            while ((output = br.readLine()) != null) {
                result = result + output;
            }
            httpClient.getConnectionManager().shutdown();
            return result;

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}