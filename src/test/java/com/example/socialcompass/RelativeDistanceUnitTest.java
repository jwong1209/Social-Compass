package com.example.socialcompass;
import static org.junit.Assert.assertEquals;
import com.example.socialcompass.compass.RelativeDistance;
import org.junit.Test;
import org.junit.*;

public class RelativeDistanceUnitTest {

    @BeforeClass
    public static void setUp(){

    }
    
    @AfterClass
    public static void done(){

    }

    public void test_same_positions(){
        double user_lat = 0;
        double user_long = 0;
        double loc_lat = 0;
        double loc_long = 0;
        double res = RelativeDistance.calculate_relative_distance(user_lat, user_long, loc_lat, loc_long);
        assertEquals(0.0, res, 0.01);
    }

    @Test
    public void test_different_positions(){
        double user_lat = 100;
        double user_long = 50;
        double loc_lat = 0;
        double loc_long = 0;
        double res = RelativeDistance.calculate_relative_distance(user_lat,user_long,loc_lat,loc_long);
        assertEquals(6662, res, 1);
    }
}
