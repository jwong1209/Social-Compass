package com.example.socialcompass.compass;
import java.lang.Math;

public class RelativeDistance {
    public static double calculate_relative_distance(double latitude, double longitude, double latitudeTwo, double longitudeTwo) {
        final double R = 3959; // Earth's mean radius in miles
        double latDistance = Math.toRadians(latitudeTwo - latitude);
        double lonDistance = Math.toRadians(longitudeTwo - longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(latitudeTwo))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return distance;
    }
}