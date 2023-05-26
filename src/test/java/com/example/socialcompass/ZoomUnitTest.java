package com.example.socialcompass;

import static android.content.Context.MODE_PRIVATE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.socialcompass.activity.CompassActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ZoomUnitTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void zoomInTest() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.onActivity(activity -> {
            SharedPreferences savedLocationData = activity.getSharedPreferences("inputs", MODE_PRIVATE);

            ImageView zoom1 = activity.findViewById(R.id.compass_zoom1);
            ImageView zoom2 = activity.findViewById(R.id.compass_zoom2);
            ImageView zoom3 = activity.findViewById(R.id.compass_zoom3);
            ImageView zoom4 = activity.findViewById(R.id.compass_zoom4);

            Button zoomInBtn = activity.findViewById(R.id.zoomIn);
            Button zoomOutBtn = activity.findViewById(R.id.zoomOut);

            zoomInBtn.performClick();

            // Default zoom level is 2, it should be at level 1 after clicking the zoom in button
            assertEquals(savedLocationData.getInt("zoomLvl", 2), 1);
            assertEquals(zoom1.getVisibility(), View.VISIBLE);
            assertEquals(zoom2.getVisibility(), View.INVISIBLE);
            assertEquals(zoom3.getVisibility(), View.INVISIBLE);
            assertEquals(zoom4.getVisibility(), View.INVISIBLE);

            // Zoom in button should be disabled
            assertFalse(zoomInBtn.isEnabled());
            assertTrue(zoomOutBtn.isEnabled());
        });
    }

    @Test
    public void zoomOutTest() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SharedPreferences savedLocationData = activity.getSharedPreferences("inputs", MODE_PRIVATE);

            ImageView zoom1 = activity.findViewById(R.id.compass_zoom1);
            ImageView zoom2 = activity.findViewById(R.id.compass_zoom2);
            ImageView zoom3 = activity.findViewById(R.id.compass_zoom3);
            ImageView zoom4 = activity.findViewById(R.id.compass_zoom4);
            Button zoomOutBtn = activity.findViewById(R.id.zoomOut);
            Button zoomInBtn = activity.findViewById(R.id.zoomIn);
            zoomOutBtn.performClick();

            // Default zoom level is 2, it should be at level 3 after clicking the zoom out button
            assertEquals(savedLocationData.getInt("zoomLvl", 2), 3);
            assertEquals(zoom1.getVisibility(), View.INVISIBLE);
            assertEquals(zoom2.getVisibility(), View.INVISIBLE);
            assertEquals(zoom3.getVisibility(), View.VISIBLE);
            assertEquals(zoom4.getVisibility(), View.INVISIBLE);

            // Zoom out button should be enabled still
            assertTrue(zoomOutBtn.isEnabled());
            assertTrue(zoomInBtn.isEnabled());

            zoomOutBtn.performClick();
            assertEquals(savedLocationData.getInt("zoomLvl", 2), 4);
            assertEquals(zoom1.getVisibility(), View.INVISIBLE);
            assertEquals(zoom2.getVisibility(), View.INVISIBLE);
            assertEquals(zoom3.getVisibility(), View.INVISIBLE);
            assertEquals(zoom4.getVisibility(), View.VISIBLE);

            // Zoom out button should be disabled
            assertFalse(zoomOutBtn.isEnabled());
            assertTrue(zoomInBtn.isEnabled());
        });
    }

}
