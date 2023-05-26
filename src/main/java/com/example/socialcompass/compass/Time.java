package com.example.socialcompass.compass;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Time {
    static int hours;
    static int minutes;
    static int seconds;
    static boolean isRunning;
    static ImageView onlineIm;
    static ImageView offlineIm;
    static TextView text;
    static Activity activity;

    Time() {
        this.hours = 0;
        this.minutes = 0;
        this.seconds = 0;
        this.isRunning = false;
        this.onlineIm = null;
        this.offlineIm = null;
        this.text = null;
    }
    public static void setMockTime(int h, int m, int s) {
        hours = h;
        minutes = m;
        seconds = s;
    }

    public static ImageView getOnlineIm() {
        return onlineIm;
    }

    public static void setImageAndText(ImageView online, ImageView offline, TextView tx) {
        onlineIm = online;
        offlineIm = offline;
        text = tx;
    }
    public static void setActivity(Activity act){
        activity = act;
    }
    public static void addSecond() {
        seconds += 1;
        readjustTime();
        checkIsOffline();
    }
    public static void checkIsOffline() {
        if (text != null && activity != null) {
            if (minutes > 0 || hours > 0) {
                text.setText(getStatusTimerText());
                onlineIm.setVisibility(View.INVISIBLE);
                offlineIm.setVisibility(View.VISIBLE);

            } else {
                text.setText("");
                onlineIm.setVisibility(View.VISIBLE);
                offlineIm.setVisibility(View.GONE);
            }
        }
    }
    public static void readjustTime() {
        if (seconds > 60) {
            minutes += 1;
            seconds = 0;
        }
        if (minutes > 60) {
            hours += 1;
            minutes = 0;
        }
    }

    public static String getStatusTimerText() {
        String output = "";
        if (hours != 0) {
            output += hours + "h ";
        }
        if (minutes != 0) {
            output += minutes + "m ";
        }
        return output;
    }
    public static String makeString() {
        return hours + "h " + minutes + "m " + seconds + "s ";
    }
    public static void resetTime() {
        minutes = 0;
        seconds = 0;
        hours = 0;
    }
    public static void start_timer() {
        if (!isRunning) {
            isRunning = true;
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1000);
            executor.scheduleAtFixedRate(() -> {
                if (activity == null) {
                    Time.addSecond();
                } else {
                    activity.runOnUiThread(() -> {
                        Time.addSecond();
                    });
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }
}
