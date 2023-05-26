package com.example.socialcompass;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.socialcompass.activity.CompassActivity;
import com.example.socialcompass.activity.MainActivity;
import com.example.socialcompass.activity.RegisterActivity;
import com.example.socialcompass.activity.WelcomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.Objects;

@RunWith(RobolectricTestRunner.class)
public class MS2US1BDDTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);


    /**
     * Scenario 1: User enters a username
     * Given The user launched the app for the first time
     * And entered the name “Jason”
     * When The user clicked Submit
     * Then the app goes into the Welcome screen which displays the user’s UID and prompts the user to share the UID with friends
     */
    @Test
    public void usernameEnteredTest() {
        ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(RegisterActivity.class);

        scenario.onActivity(activity -> {
            Intent expectedIntent = new Intent(activity, WelcomeActivity.class);
            EditText username = activity.findViewById(R.id.username);
            Button submitButton = activity.findViewById(R.id.registerButton);

            username.setText("Jason");
            submitButton.performClick();

            ShadowActivity shadowActivity = Shadows.shadowOf(activity);
            Intent actualIntent = shadowActivity.getNextStartedActivity();

            assertTrue(actualIntent.filterEquals(expectedIntent));
        });
    }

    /**
     * Scenario 2: User doesn’t enter a username
     * Given The user launched the app for the first time
     * And doesn’t enter a name
     * When The user clicked Submit
     * Then the app shows an error message “Please enter a username”
     * And remains on the input screen
     */
    @Test
    public void noUsernameEnteredTest() {
        ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(RegisterActivity.class);

        scenario.onActivity(activity -> {
            Button submitButton = activity.findViewById(R.id.registerButton);

            submitButton.performClick();

            AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();

            ShadowActivity shadowActivity = Shadows.shadowOf(activity);
            Intent intent = shadowActivity.getNextStartedActivity();

            // "Please enter a username" alert displays
            assertNotNull(alert);
            assertTrue(alert.isShowing());

            // Check if the user is still on the Register screen
            assertNull(intent);
        });
    }

    /**
     * Scenario 3: User already entered a username
     * Given The user already entered a username during the previous app launches
     * When The user open the app
     * Then the app opens the compass screen
     */
    @Test
    public void usernameAlreadyEnteredTest() {
        var scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            SharedPreferences prefs = activity.getSharedPreferences("inputs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("initialized", true);
            editor.apply();
        });

        // Close app
        scenario.close();

        // Relaunch app
        scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            Intent expectedIntent = new Intent(activity,CompassActivity.class);

            ShadowActivity shadowActivity = Shadows.shadowOf(activity);
            Intent actualIntent = shadowActivity.getNextStartedActivity();

            assertTrue(actualIntent.filterEquals(expectedIntent));
        });
    }
}
