package com.example.socialcompass;
import static org.junit.Assert.assertEquals;

import com.example.socialcompass.compass.RelativeAngle;

import org.junit.Test;
import org.junit.*;

public class RelativeAngleUnitTest {



    @BeforeClass
    public static void setUp(){

    }

    @AfterClass
    public static void done(){

    }

    @Test
    public void test_allZeros(){
        double user_lat = 0;
        double user_long = 0;
        double loc_lat = 0;
        double loc_long = 0;
        double res = RelativeAngle.relative_angle(user_lat,user_long,loc_lat,loc_long);
        assertEquals(0.0, res, 0.01);
    }

    @Test
    public void test_userLatLongNotZero(){
        double user_lat = 1;
        double user_long = 1;
        double loc_lat = 0;
        double loc_long = 0;
        double res = RelativeAngle.relative_angle(user_lat,user_long,loc_lat,loc_long);
        assertEquals(-135.0, res, 0.05);
    }

    @Test
    public void test_posLatNegLong() {
        // https://www.igismap.com/formula-to-find-bearing-or-heading-angle-between-two-points-latitude-longitude/
        // Google Maps: https://www.google.com/maps/dir/39.099912,-94.581213/(38.627089,-90.200203)/@39.1515904,-94.1456837,7.51z/data=!4m7!4m6!1m0!1m3!2m2!1d-90.200203!2d38.627089!3e3
        double user_lat = 39.099912;
        double user_long = -94.581213;
        double loc_lat = 38.627089;
        double loc_long = -90.200203;
        double res = RelativeAngle.relative_angle(user_lat,user_long,loc_lat,loc_long);
        assertEquals(96.51, res, 0.05);
    }


}
