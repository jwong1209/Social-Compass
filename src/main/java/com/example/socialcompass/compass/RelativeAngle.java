package com.example.socialcompass.compass;
import java.lang.Math;

public class RelativeAngle {

    /*
     * Process input latitude/longitude from Google Maps
     * Return angle between North (0 degrees) and inputted location,
     * using user location as origin/center of compass
     * 
     * For now pass in dummy user input
     * 
     * Return bearing:
     * https://stackoverflow.com/questions/3932502/calculate-angle-between-two-latitude-longitude-points
     * https://www.igismap.com/formula-to-find-bearing-or-heading-angle-between-two-points-latitude-longitude/
     * 
     * Input Lat/Long format (Decimal Degree):
     * 32.880506, -117.235892
     */
    public static double relative_angle(double user_lat, double user_long,
                            double loc_lat, double loc_long) {

        double X = Math.cos(Math.toRadians(loc_lat)) * Math.sin(Math.toRadians(loc_long - user_long));
        double Y = ( Math.cos(Math.toRadians(user_lat)) * Math.sin(Math.toRadians(loc_lat)) ) -
                ( Math.sin(Math.toRadians(user_lat)) * Math.cos(Math.toRadians(loc_lat)) * Math.cos(Math.toRadians(loc_long - user_long)) );

        // in radians
        double b = Math.atan2(X,Y);
        // convert radians to degrees
        double bearing = Math.toDegrees(b);

        return bearing;
    }
}