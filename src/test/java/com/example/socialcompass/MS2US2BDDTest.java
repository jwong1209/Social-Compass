package com.example.socialcompass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;

import com.example.socialcompass.activity.AddFriendsActivity;
import com.example.socialcompass.activity.InputActivity;
import com.example.socialcompass.model.Location;
import com.example.socialcompass.model.LocationDatabase;
import com.example.socialcompass.model.LocationRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MS2US2BDDTest {

    /**
     * Scenario: User inputs an invalid UID.
     * Given the user is on the add friends screen
     * And the user enters an invalid UID to the entry box
     * When the user clicks “Add”
     * Then an error message pops up that says “Please enter a valid UID”
     */
    @Test
    public void test_invalid_uid() {
        ActivityScenario<AddFriendsActivity> scenario = ActivityScenario.launch(AddFriendsActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            EditText userInputET = activity.findViewById(R.id.enterUID);
            userInputET.setText("jkfvabhianjfhbghvurfnjbhgtrihefjnbghtufbgvrejvkfh");
            String userInput = userInputET.getText().toString();
            Button addButton = activity.findViewById(R.id.addButton);
            addButton.performClick();
            LocationRepository locationRepository = new LocationRepository( LocationDatabase.provide(activity).getDao());
            assertFalse(locationRepository.existsLocal(userInput));

        });
    }

    /**
     * Scenario: User shares UID to friends.
     * Given I am on the add friends screen
     * When I click on the “Copy UID” button
     * Then my UID is copied to my clipboard
     */
    @Test
    public void test_share_uid() {
        ActivityScenario<AddFriendsActivity> scenario = ActivityScenario.launch(AddFriendsActivity.class);
        scenario.onActivity(activity -> {
            Button copyButton = activity.findViewById(R.id.copyButton);
            TextView UIDDisplay = activity.findViewById(R.id.UIDDisplay);
            UIDDisplay.setText("Copied Text");
            copyButton.performClick();

            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            String copiedText = clipboard.getText().toString();
            assertEquals("Copied Text", copiedText);
        });
    }
}
