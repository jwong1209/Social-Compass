package com.example.socialcompass;

import static android.content.Context.MODE_PRIVATE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.socialcompass.activity.CompassActivity;
import com.example.socialcompass.compass.Compass;
import com.example.socialcompass.compass.CompassLocation;
import com.example.socialcompass.model.Location;
import com.example.socialcompass.viewmodel.FriendViewModel;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.HashMap;


@RunWith(RobolectricTestRunner.class)
public class MS2US6BDDTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    /**
     * Scenario 1: User zooms in on map
     * Given the map is on the 10-500 miles zoom
     * And There is a dot labeled “Joe” on the second ring
     * And There is a dot labeled “Josh” at the left of the third ring
     * When The user clicked the zoom in button
     * Then The compass goes from three ring to two rings
     * And the "Josh" dot is on the perimeter of the second ring and no longer has a label
     */
    @Test
    public void testZoomIn() {
        ActivityScenario<CompassActivity> activityScenario = ActivityScenario.launch(CompassActivity.class);
        activityScenario.onActivity(activity -> {
            SharedPreferences savedLocationData = activity.getSharedPreferences("inputs", MODE_PRIVATE);
            SharedPreferences.Editor editor = savedLocationData.edit();

            // Set zoom in level to be on the 10-500 mile ring (zoom level 3)
            editor.putInt("zoomLvl", 3);
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
            lof.add(new Location("123", null, "Joe", 32.8628668, -117.2218572)); // UTC, 85 Degrees Bakery
            lof.add(compass.createImageViewIcon());
            lof.add(compass.createUILabel("Joe"));
            lof2.add(new Location("456", null, "Josh", 34.0206066, -118.7420765)); // LA
            lof2.add(compass.createImageViewIcon());
            lof2.add(compass.createUILabel("Josh"));
            listOfFriends.put("Joe", lof);
            listOfFriends.put("Josh", lof2);
            compass.setMockLof(listOfFriends);

            compass.setLocationRadius();

            // Zoom level is currently at 3
            assertEquals(3, savedLocationData.getInt("zoomLvl", 2));

            Location joshLoc = (Location) listOfFriends.get("Josh").get(0);
            ImageView joshIm = (ImageView) listOfFriends.get("Josh").get(1);
            TextView joshLabel = (TextView) listOfFriends.get("Josh").get(2);

            Location joeLoc = (Location) listOfFriends.get("Joe").get(0);
            ImageView joeIm = (ImageView) listOfFriends.get("Joe").get(1);
            TextView joeLabel = (TextView) listOfFriends.get("Joe").get(2);

            ConstraintLayout.LayoutParams joshImLayoutParams = (ConstraintLayout.LayoutParams) joshIm.getLayoutParams();
            ConstraintLayout.LayoutParams joeImLayoutParams = (ConstraintLayout.LayoutParams) joeIm.getLayoutParams();

            assertEquals(100 * scale + 0.5f, joeImLayoutParams.circleRadius, 5);
            assertEquals(170 * scale + 0.5f, joshImLayoutParams.circleRadius, 5);

            // Zoom level goes from 3 to 2
            zoomInBtn.performClick();

            assertEquals(2, savedLocationData.getInt("zoomLvl", 2));

            compass.setLocationRadius();

            assertEquals(170 * scale + 0.5f, joeImLayoutParams.circleRadius, 5);
            assertEquals(195 * scale + 0.5f, joshImLayoutParams.circleRadius, 5);

            // Check if the label for Josh is gone, now that it is on the perimeter
            assertEquals(View.GONE, joshLabel.getVisibility());
        });
    }

    /**
     * Scenario 2: User is at full zoom in
     * Given The map is on the 0-10 miles zoom
     * When I am on the map page
     * Then the zoom in button should be grayed out and unable to be clicked
     */
    @Test
    public void testFullZoomIn() {
        ActivityScenario<CompassActivity> activityScenario = ActivityScenario.launch(CompassActivity.class);
        activityScenario.onActivity(activity -> {
            ImageView zoom1 = activity.findViewById(R.id.compass_zoom1);
            Button zoomInBtn = activity.findViewById(R.id.zoomIn);
            SharedPreferences savedLocationData = activity.getSharedPreferences("inputs", MODE_PRIVATE);
            SharedPreferences.Editor editor = savedLocationData.edit();
            editor.putInt("zoomLvl", 1);
            editor.apply();

            // Initialize variables
            float scale = activity.getBaseContext().getResources().getDisplayMetrics().density;
            FriendViewModel friendViewModel = activity.setupViewModel();
            CompassLocation user = new CompassLocation(32.877992837606115, -117.23026701911897, "", null);
            Compass compass = new Compass(user, friendViewModel, activity, scale);

            compass.setCompassZoom();

            assertEquals(1, savedLocationData.getInt("zoomLvl", 2));
            assertEquals(View.VISIBLE, zoom1.getVisibility());
            assertFalse(zoomInBtn.isEnabled());
        });
    }
}