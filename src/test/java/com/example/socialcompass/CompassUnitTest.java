package com.example.socialcompass;
import static android.content.Context.MODE_PRIVATE;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.robolectric.RuntimeEnvironment.getApplication;

import android.app.Application;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.test.core.app.ActivityScenario;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.test.rule.GrantPermissionRule;

import com.example.socialcompass.activity.CompassActivity;
import com.example.socialcompass.compass.Compass;
import com.example.socialcompass.compass.CompassLocation;
import com.example.socialcompass.model.Location;
import com.example.socialcompass.model.LocationAPI;
import com.example.socialcompass.model.LocationDatabase;
import com.example.socialcompass.model.LocationRepository;
import com.example.socialcompass.viewmodel.FriendViewModel;

@RunWith(RobolectricTestRunner.class)
public class CompassUnitTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void testDoesFriendExistLocally() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.onActivity(activity -> {
            float scale = activity.getBaseContext().getResources().getDisplayMetrics().density;
            CompassLocation user = new CompassLocation(32.877992837606115, -117.23026701911897, "", null);

            List<Location> locationList = new ArrayList<>();
            locationList.add(new Location("Joe", null, "Joe", 32, -117));

            Compass compass = new Compass(user, new FriendViewModel(getApplication()), activity, scale);

            HashMap<String, ArrayList<Object>> lof = new HashMap<>();
            Location friend = new Location("Joe", null, "Joe", 32, -117);
            ArrayList<Object> newArrayList = new ArrayList<>();
            newArrayList.add(friend);
            newArrayList.add(compass.createImageViewIcon());
            newArrayList.add(compass.createUILabel(friend.label));
            lof.put(friend.publicCode, newArrayList);
            compass.setMockLof(lof);

            Location dne = new Location("dne", null, "dne", 32, -117);

            assertTrue(compass.doesFriendExistLocally(friend));
            assertFalse(compass.doesFriendExistLocally(dne));
        });
    }

    @Test
    public void handleListOfLocation() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.onActivity(activity -> {
            //set up
            float scale = activity.getBaseContext().getResources().getDisplayMetrics().density;
            CompassLocation user = new CompassLocation(32.877992837606115, -117.23026701911897, "", null);

            List<Location> locationList = new ArrayList<>();
            locationList.add(new Location("Joe", null, "Joe", 32, -117));

            Compass compass = new Compass(user, new FriendViewModel(getApplication()), activity, scale);

            HashMap<String, ArrayList<Object>> lof = new HashMap<>();
            Location friend = new Location("Joe", null, "Joe", 32, -117);
            ArrayList<Object> newArrayList = new ArrayList<>();
            newArrayList.add(friend);
            newArrayList.add(compass.createImageViewIcon());
            newArrayList.add(compass.createUILabel(friend.label));
            lof.put(friend.publicCode, newArrayList);

            LocationAPI locationAPI = LocationAPI.provide();
            LocationDatabase locationDatabase = LocationDatabase.provide(activity);
            LocationRepository locationRepository = new LocationRepository(locationDatabase.getDao());
            List<LiveData<Location>> list = compass.mockHandleLocationListOfLiveData(user, lof, locationList);
            for (LiveData<Location> liveData : list) {
                assertTrue(liveData.hasObservers());
            }
        });
    }

    @Test
    public void testOnZoomInClicked() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.onActivity(activity -> {
            SharedPreferences savedLocationData = activity.getSharedPreferences("inputs", MODE_PRIVATE);

            //set up
            Button zoomIn = (Button) activity.findViewById(R.id.zoomIn);
            Button zoomOut = (Button) activity.findViewById(R.id.zoomOut);
            int zoomLvl = savedLocationData.getInt("zoomLvl", 2);

            float scale = activity.getBaseContext().getResources().getDisplayMetrics().density;
            CompassLocation user = new CompassLocation(32.877992837606115, -117.23026701911897, "", null);

            Compass compass = new Compass(user, new FriendViewModel(getApplication()), activity, scale);
            compass.onZoomInClicked();
            assertEquals(false,zoomIn.isEnabled());
            compass.onZoomOutClicked();
            assertEquals(true, zoomOut.isEnabled());

        });
    }

    @Test
    public void testOnZoomOutClicked() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.onActivity(activity -> {
            SharedPreferences savedLocationData = activity.getSharedPreferences("inputs", MODE_PRIVATE);

            //set up
            Button zoomIn = (Button) activity.findViewById(R.id.zoomIn);
            Button zoomOut = (Button) activity.findViewById(R.id.zoomOut);
            int zoomLvl = savedLocationData.getInt("zoomLvl", 2);

            float scale = activity.getBaseContext().getResources().getDisplayMetrics().density;
            CompassLocation user = new CompassLocation(32.877992837606115, -117.23026701911897, "", null);

            Compass compass = new Compass(user, new FriendViewModel(getApplication()), activity, scale);
            compass.onZoomOutClicked();
            assertEquals(true,zoomIn.isEnabled());
            assertEquals(true,zoomOut.isEnabled());
            compass.onZoomOutClicked();
            assertEquals(true, zoomIn.isEnabled());
            assertEquals(false, zoomOut.isEnabled());
        });
    }

    @Test
    public void testSetLocationRadius() {
        ActivityScenario<CompassActivity> activityScenario = ActivityScenario.launch(CompassActivity.class);
        activityScenario.onActivity(activity -> {
            SharedPreferences savedLocationData = activity.getSharedPreferences("inputs", MODE_PRIVATE);
            SharedPreferences.Editor editor = savedLocationData.edit();

            // Set zoom in level to be on the 10-500 mile ring (zoom level 3)
            editor.putInt("zoomLvl", 2);
            editor.apply();

            // Initialize variables
            HashMap<String, ArrayList<Object>> listOfFriends = new HashMap<>();
            CompassLocation user = new CompassLocation(32.877992837606115, -117.23026701911897, "", null);
            Button zoomInBtn = activity.findViewById(R.id.zoomIn);
            FriendViewModel friendViewModel = activity.setupViewModel();
            float scale = activity.getBaseContext().getResources().getDisplayMetrics().density;
            Compass compass = new Compass(user, friendViewModel, activity, scale);
            ArrayList<Object> lof = new ArrayList<>();
            ArrayList<Object> lof2 = new ArrayList<>();

            // Set values
            lof.add(new Location("123", null, "Joe", 32.8628668,-117.2218572)); // UTC, 85 Degrees Bakery
            lof.add(compass.createImageViewIcon());
            lof.add(compass.createUILabel("Joe"));
            lof2.add(new Location("456", null, "Josh", 34.0206066,-118.7420765)); // LA
            lof2.add(compass.createImageViewIcon());
            lof2.add(compass.createUILabel("Josh"));
            listOfFriends.put("Joe", lof);
            listOfFriends.put("Josh", lof2);
            compass.setMockLof(listOfFriends);

            // Zoom level is currently at 2
            ImageView joshIm = (ImageView) listOfFriends.get("Josh").get(1);
            ImageView joeIm = (ImageView) listOfFriends.get("Joe").get(1);

            ConstraintLayout.LayoutParams joshImLayoutParams = (ConstraintLayout.LayoutParams) joshIm.getLayoutParams();
            ConstraintLayout.LayoutParams joeImLayoutParams = (ConstraintLayout.LayoutParams) joeIm.getLayoutParams();


            assertEquals(savedLocationData.getInt("zoomLvl", 2), 2);
            compass.setLocationRadius();
            assertEquals(joeImLayoutParams.circleRadius, 170 * scale + 0.5f, 5);
            assertEquals(joshImLayoutParams.circleRadius, 195 * scale + 0.5f, 5);
        });
    }
}
