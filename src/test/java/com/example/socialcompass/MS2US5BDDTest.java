package com.example.socialcompass;

import static com.example.socialcompass.compass.Time.checkIsOffline;
import static com.example.socialcompass.compass.Time.getStatusTimerText;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.socialcompass.activity.CompassActivity;
import com.example.socialcompass.compass.Time;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MS2US5BDDTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    /**
     * Scenario: GPS is tracking
     * Given that I am online
     * When I view the map
     * Then I should see a green circle with no text indicating that my GPS is connected
     */
    @Test
    public void testOnlineDisplay() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.onActivity(activity -> {
            int noHrs = 0;
            int noMin = 0;
            int noSec = 0;
            
            ImageView offlineDisplay = activity.findViewById(R.id.gps_offline_icon);
            ImageView onlineDisplay = activity.findViewById(R.id.gps_online_icon);

            // display for currently online
            Time.setMockTime(noHrs, noMin, noSec);
            Time.checkIsOffline();
            assertEquals(offlineDisplay.getVisibility(), View.GONE);
            assertEquals(onlineDisplay.getVisibility(), View.VISIBLE);
        });
    }

    /**
     * Scenario: GPS is tracking
     * Given that I am offline
     * When I view the map
     * Then I should see a red circle indicating that my GPS is not connected
     */
    @Test
    public void testOfflineDisplay() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.onActivity(activity -> {
            int hrs = 2;
            int min = 5;
            int sec = 10;

            ImageView offlineDisplay = activity.findViewById(R.id.gps_offline_icon);
            ImageView onlineDisplay = activity.findViewById(R.id.gps_online_icon);

            // display for offline
            Time.setMockTime(hrs, min, sec);
            Time.checkIsOffline();
            assertEquals(View.VISIBLE, offlineDisplay.getVisibility());
            assertEquals(View.INVISIBLE, onlineDisplay.getVisibility());
        });
    }

    /**
     * Scenario: Offline and GPS is not tracking
     * Given that I am offline
     * And the GPS has not been tracking me for 4hrs and 5 minutes
     * When I view the map
     *Then I should see a red circle with the text “4h 5m ”
     */
    @Test
    public void testDisplayOfflineTime() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.onActivity(activity -> {
            TextView offlineTimeText = activity.findViewById(R.id.gps_time_text);
            int hrs = 4;
            int min = 5;
            int sec = 10;
            Time.setMockTime(hrs, min, sec);
            checkIsOffline();
            getStatusTimerText();
            String offlineTimeStr = offlineTimeText.getText().toString();
            assertEquals("4h 5m ", offlineTimeStr);

            int noHrs = 0;
            int noMin = 0;
            int noSec = 0;
            Time.setMockTime(noHrs, noMin, noSec);
            checkIsOffline();
            getStatusTimerText();
            offlineTimeStr = offlineTimeText.getText().toString();
            assertEquals("", offlineTimeStr);
        });
    }

    /**
     * test for proper recording of offline time
     */
    @Test
    public void testRecordOfflineTime() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.onActivity(activity -> {
            int hrs = 2;
            int min = 5;
            int sec = 10;

            // user is  offline
            Time.setMockTime(hrs, min, sec);
            String actualTime = getStatusTimerText();
            assertEquals("2h 5m ", actualTime);

            int noHrs = 0;
            int noMin = 0;
            int noSec = 0;
            // user is online
            Time.setMockTime(noHrs, noMin, noSec);
            actualTime = getStatusTimerText();
            assertEquals("", actualTime);
        });
    }
}
