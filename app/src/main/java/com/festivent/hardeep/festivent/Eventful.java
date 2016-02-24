package com.festivent.hardeep.festivent;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Hardeep on 2/1/2016.
 */
public class Eventful {
    private static final String TAG_EVENTS = "events";
    private static final String TAG_EVENT = "event";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_URL = "url";
    private static final String TAG_START_TIME = "start_time";
    private static final String TAG_END_TIME = "stop_time";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_IMAGE_MEDIUM = "medium";

    public static JSONObject data;
    public static ArrayList<EventInfo> eventfulEvents = new ArrayList<EventInfo>();
    public static JSONArray eventfulJSONArray = null;


    //http://api.eventful.com/rest/events/search?app_key=LCXcTmdZsCTQnhNZ&where=32.746682,-117.162741&within=10&date=2016020100-2016020200&page_size=50
    public static String getData(String latitude, String longitude, String distance, String pageSize, Calendar from, Calendar to) {
        String link = "http://api.eventful.com/json/events/search?app_key=LCXcTmdZsCTQnhNZ" +
                "&where=" + latitude + "," + longitude +
                "&within=" + distance +
                "&page_size=" + pageSize +
                "&date=" + getEventfulDate(from) + "00," + getEventfulDate(to) + "00";
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

    public static ArrayList<EventInfo> getDataArray(Context context, String apiData) {
        //Eventful Events
        try {
            data = new JSONObject(apiData);
            if (data != null) {
                //Get JSON Array node
                JSONObject x = data.getJSONObject(TAG_EVENTS);
                eventfulJSONArray = x.getJSONArray(TAG_EVENT);

                //loop through each event
                for (int i = 0; i < eventfulJSONArray.length(); i++) {
                    JSONObject e = eventfulJSONArray.getJSONObject(i);

                    //get name object from  list
                    String eventName = e.getString(TAG_TITLE);
                    String desc = e.getString(TAG_DESCRIPTION);
                    String url = e.getString(TAG_URL);
                    if (desc == null) {
                        desc = "No Description";
                    }
                    if (url == null) {
                        url = "Website not provided";
                    }

                    //get startTime
                    String startTime = e.getString(TAG_START_TIME);

                    //get endTime Object
                    String endTime = e.getString(TAG_END_TIME);

                    //get logo object
                    String imageUrl;
                    if (!e.isNull(TAG_IMAGE)) {
                        JSONObject image = e.getJSONObject(TAG_IMAGE);
                        JSONObject imageMedium = image.getJSONObject(TAG_IMAGE_MEDIUM);
                        imageUrl = imageMedium.getString(TAG_URL);
                    } else {
                        imageUrl = null;
                    }

                    //get GEO coordinates
                    String lat = e.getString(TAG_LATITUDE);
                    String log = e.getString(TAG_LONGITUDE);

                    if (!checkDuplicate(eventfulEvents, eventName)) {
                        eventfulEvents.add(new EventInfo(eventName, desc, startTime, endTime, url, imageUrl, Double.parseDouble(lat), Double.parseDouble(log), "Eventful"));
                    }
                }
            }
        } catch (final Exception e) {
            Log.e("eventful_error", e.toString());
            Toast.makeText(context, "Sorry an error have occured. Please try again.", Toast.LENGTH_SHORT).show();
        }
        return eventfulEvents;
    }

    public static boolean checkDuplicate(ArrayList<EventInfo> list, String title) {
        for (EventInfo eventInfo : list) {
            if (eventInfo.getEventName().equals(title)) {
                return true;
            }
        }
        return false;
    }

    public static String getEventfulDate(Calendar cal) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        result += sdf.format(cal.getTime());
        return result;
    }

//    public static String getEventfulDateIncrement(int increment) {
//        String result = "";
//        String current = getEventfulDate();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
//        Calendar c = Calendar.getInstance();
//        try {
//            c.setTime(sdf.parse(current));
//            c.add(Calendar.DATE, increment);
//            result += sdf.format(c.getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

}
