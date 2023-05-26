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
public class MS2US7BDDTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    /**
     *  Scenario 1: User zooms out on map
     *  Given The map is on the 0-10 miles zoom
     *  And my friend Joe is located between 10-500 miles away
     *  And Joe is represented by a label-less dot
     *  When I click the zoom-out button
     *  Then the map should display two rings, 0-1 and 1-10 miles
     *  And Joe should be located within those two rings
     *  And Joe’s label should appear.
     */
    @Test
    public void testZoomOut() {
        ActivityScenario<CompassActivity> activityScenario = ActivityScenario.launch(CompassActivity.class);
        activityScenario.onActivity(activity -> {
            SharedPreferences savedLocationData = activity.getSharedPreferences("inputs", MODE_PRIVATE);

            // Initialize variables
            float scale = activity.getBaseContext().getResources().getDisplayMetrics().density;
            HashMap<String, ArrayList<Object>> listOfFriends = new HashMap<>();
            Button zoomOutBtn = activity.findViewById(R.id.zoomOut);
            ArrayList<Object> lof = new ArrayList<>();

            CompassLocation user = new CompassLocation(32.877992837606115, -117.23026701911897, "", null);
            FriendViewModel friendViewModel = activity.setupViewModel();
            Compass compass = new Compass(user, friendViewModel, activity, scale);

            // Set values
            lof.add(new Location("123", null, "Joe", 34.0206066, -118.7420765)); // LA
            lof.add(compass.createImageViewIcon());
            lof.add(compass.createUILabel("Joe"));
            listOfFriends.put("Joe", lof);
            compass.setMockLof(listOfFriends);

            compass.setLocationRadius();

            // Zoom level is currently at 2, or the 0-10 miles zoom
            assertEquals(2, savedLocationData.getInt("zoomLvl", 2));

            ImageView joeIm = (ImageView) listOfFriends.get("Joe").get(1);
            TextView joeLabel = (TextView) listOfFriends.get("Joe").get(2);
            ConstraintLayout.LayoutParams joeImLayoutParams = (ConstraintLayout.LayoutParams) joeIm.getLayoutParams();

            assertEquals(195 * scale + 0.5f, joeImLayoutParams.circleRadius, 5);

            // Check if the label for Joe is gone, since the icon is on the perimeter
            assertEquals(joeLabel.getVisibility(), View.GONE);

            // Zoom level goes from 2 to 3
            zoomOutBtn.performClick();

            assertEquals(3, savedLocationData.getInt("zoomLvl", 2));

            compass.setLocationRadius();

            assertEquals(170 * scale + 0.5f, joeImLayoutParams.circleRadius, 5);

            // Check if the label for Josh is gone, now that it is on the perimeter
            assertEquals(joeLabel.getVisibility(), View.VISIBLE);
        });
    }

    /**
     * Scenario 2: User is at full zoom-out
     * Given The map is on the 0-500+ miles zoom
     * When I am on the map page
     * Then the zoom-out button should be grayed out and unable to be clicked
     */
    @Test
    public void testFullZoomOut() {
        ActivityScenario<CompassActivity> activityScenario = ActivityScenario.launch(CompassActivity.class);
        activityScenario.onActivity(activity -> {
            ImageView zoom4 = activity.findViewById(R.id.compass_zoom4);
            Button zoomOutBtn = activity.findViewById(R.id.zoomOut);
            SharedPreferences savedLocationData = activity.getSharedPreferences("inputs", MODE_PRIVATE);
            SharedPreferences.Editor editor = savedLocationData.edit();
            editor.putInt("zoomLvl", 4);
            editor.apply();

            // Initialize variables
            float scale = activity.getBaseContext().getResources().getDisplayMetrics().density;
            FriendViewModel friendViewModel = activity.setupViewModel();
            CompassLocation user = new CompassLocation(32.877992837606115, -117.23026701911897, "", null);
            Compass compass = new Compass(user, friendViewModel, activity, scale);

            compass.setCompassZoom();

            assertEquals(4, savedLocationData.getInt("zoomLvl", 2));
            assertEquals(View.VISIBLE, zoom4.getVisibility());
            assertFalse(zoomOutBtn.isEnabled());
        });
    }
}

