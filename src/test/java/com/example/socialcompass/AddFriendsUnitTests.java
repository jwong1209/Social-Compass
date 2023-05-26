package com.example.socialcompass;

import static com.example.socialcompass.activity.InputActivity.Validity.ALLEMPTYERROR;
import static com.example.socialcompass.activity.InputActivity.Validity.ALLFILLED;
import static com.example.socialcompass.activity.InputActivity.Validity.FILLEDNOLABEL;
import static com.example.socialcompass.activity.InputActivity.Validity.INPUTERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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
import com.example.socialcompass.model.LocationAPI;
import com.example.socialcompass.model.LocationDatabase;
import com.example.socialcompass.model.LocationRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AddFriendsUnitTests {
    @Test
    public void test_copy_button() {
        ActivityScenario<AddFriendsActivity> scenario = ActivityScenario.launch(AddFriendsActivity.class);
        scenario.onActivity(activity -> {
            Button copyButton = activity.findViewById(R.id.copyButton);
            TextView UIDDisplay = activity.findViewById(R.id.UIDDisplay);
            UIDDisplay.setText("Test Text");
            copyButton.performClick();

            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            String copiedText = clipboard.getText().toString();
            assertEquals("Test Text", copiedText);

        });
    }

    @Test
    public void test_setObserve() {
        ActivityScenario<AddFriendsActivity> scenario = ActivityScenario.launch(AddFriendsActivity.class);
        scenario.onActivity(activity -> {
            LocationRepository locationRepository = new LocationRepository( LocationDatabase.provide(activity).getDao());

            LiveData<Location> testLocation = new MutableLiveData<>();
            ((MutableLiveData<Location>) testLocation).setValue(new Location("test14723", "test", "test", 10,10));
            activity.setObserve(testLocation, locationRepository, "test14723", activity);
            ((MutableLiveData<Location>) testLocation).setValue(new Location("test14723", "test", "testing", 10,10));

            assertEquals( true,locationRepository.existsLocal("test14723"));
        });
    }

    @Test
    public void test_checkUIDValid() {
        ActivityScenario<AddFriendsActivity> scenario = ActivityScenario.launch(AddFriendsActivity.class);
        scenario.onActivity(activity -> {
            assertTrue(activity.checkUIDValid(new Location("test14723", "test", "test", 10,10)));
            assertFalse(activity.checkUIDValid(null));
        });
    }
}