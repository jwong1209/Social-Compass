package com.example.socialcompass.model;

import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.WorkerThread;

import com.google.gson.Gson;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LocationAPI {

    private volatile static LocationAPI instance = null;

    private OkHttpClient client;

    private static String url = "https://socialcompass.goto.ucsd.edu/location/";
    public LocationAPI() {
        this.client = new OkHttpClient();
    }

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    public static LocationAPI provide() {
        if (instance == null) {
            instance = new LocationAPI();
        }
        return instance;
    }
    public static void setURL(String s) {
        url = s;
        System.out.println("URL IS THIS:"+url);
    }

    @WorkerThread
    public Location getLocation(String publicCode) {

        var request = new Request.Builder()
                .url(url + publicCode)
                .method("GET", null)
                .build();

        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var json = response.body().string();
            Log.i("GET", json);
            return Location.fromJSON(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @WorkerThread
    public String putLocation(Location location) throws Exception {

        String json = location.toJSON();
        String publicCode = location.publicCode;
        RequestBody body = RequestBody.create(json, JSON);

        publicCode = publicCode.replace(" ", "%20");
        var request = new Request.Builder()
                .url(url + publicCode)
                .method("PUT", body)
                .build();

        try (var response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * Updates existing location on the server with new lat and long
     */
    @WorkerThread
    public String updateLocation(Location location) throws Exception {

        String json = "{\"private_code\":\"" + location.privateCode + "\","
                + "\"latitude\":" + location.latitude + ","
                + "\"longitude\":" + location.longitude
                + "}";

        String publicCode = location.publicCode;
        RequestBody body = RequestBody.create(json, JSON);

        publicCode = publicCode.replace(" ", "%20");
        var request = new Request.Builder()
                .url(url + publicCode)
                .method("PATCH", body)
                .build();

        try (var response = client.newCall(request).execute()) {
            //Log.d("UpdateLocation", response.body().string());
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return "";
    }
}
