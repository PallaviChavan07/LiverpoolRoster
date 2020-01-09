package com.example.liverpoolroster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**** WEB API CODE  ****/
public class Utils {
     private static final String API_KEY = "b223d7dab7507573caf7f607ffa85bcf";
     private static final String SEARCH_METHOD = "flickr.photos.search";
     private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter( "user_id", "158037668@N02" )
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    public static Bitmap DownloadFromFlickr(String playername) throws  IOException {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method", SEARCH_METHOD);
        uriBuilder.appendQueryParameter("text", playername);
        String urlSpec = uriBuilder.build().toString();
        StringBuilder jsonString;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlSpec);
            connection = (HttpURLConnection) url.openConnection();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())))
            {
                String line;
                jsonString = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    jsonString.append(line);
                }
            }
        } finally {
            connection.disconnect();
        }

        //read url from json object
        if (jsonString.equals("")) return null;

        String secure_url = "";
        try {
            JSONObject imageJson = new JSONObject(new String(jsonString));
            JSONObject photosJsonObject = imageJson.getJSONObject("photos");
            JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

            for (int i = 0; i < photoJsonArray.length(); i++) {
                JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
                if (!photoJsonObject.has("url_s")) {
                    continue;
                }
                secure_url = photoJsonObject.getString("url_s");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //read url from json object
        if (secure_url.equals("")) return null;

        URL url = new URL(secure_url);
        connection = (HttpURLConnection)url.openConnection();
        try {
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + secure_url);
            }

            return BitmapFactory.decodeStream(in);
        } finally {
            connection.disconnect();
        }
    }
}