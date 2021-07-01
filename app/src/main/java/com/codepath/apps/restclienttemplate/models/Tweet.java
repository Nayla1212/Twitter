package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;

@Parcel
public class Tweet {

    public static final String TAG = "Tweet";

    public String body;
    public String createdAt;
    public User user;
    public ArrayList<String> embeddedImages;
    public String firstEmbeddedImage;
    public boolean hasMedia;

    //empty constructor needed by the Parceler library
    public Tweet(){};

    //timestamp
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        String temp = jsonObject.getString("created_at");
        tweet.createdAt = tweet.getRelativeTimeAgo(temp);
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));

        //Embedded image
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(Tweet.this, android.R.layout.simple_list_item_1, jsonObject.getJSONArray("media"));
        JSONObject entities = jsonObject.getJSONObject("entities");
        if(entities.has("media")){
            JSONArray media = entities.getJSONArray("media");
            tweet.hasMedia = true;
            tweet.embeddedImages = new ArrayList<>();
            for(int i=0; i<media.length(); i++){
                tweet.embeddedImages.add(media.getJSONObject(i).getString("media_url_https"));
            }
            tweet.firstEmbeddedImage = tweet.embeddedImages.get(0);

        }
        else{
            tweet.hasMedia = false;
            tweet.embeddedImages = new ArrayList<>();
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i=0; i<jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }

}
