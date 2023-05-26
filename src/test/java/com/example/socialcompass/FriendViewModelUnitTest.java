package com.example.socialcompass;

import static com.example.socialcompass.activity.InputActivity.Validity.ALLEMPTYERROR;
import static com.example.socialcompass.activity.InputActivity.Validity.ALLFILLED;
import static com.example.socialcompass.activity.InputActivity.Validity.FILLEDNOLABEL;
import static com.example.socialcompass.activity.InputActivity.Validity.INPUTERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;

import com.example.socialcompass.activity.InputActivity;
import com.example.socialcompass.activity.RegisterActivity;
import com.example.socialcompass.model.Location;
import com.example.socialcompass.model.LocationDatabase;
import com.example.socialcompass.model.LocationRepository;
import com.example.socialcompass.viewmodel.FriendViewModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
public class FriendViewModelUnitTest {

    @Test
    public void test_createUserLocation() {
        ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(RegisterActivity.class);
        scenario.onActivity(activity -> {
            String publicCode = "mockPublicCode";
            String privateCode = "mockPrivateCode";
            String label = "mockPrivateCode";
            double latitude = 230;
            double longitude = 120;

            FriendViewModel viewModel = new ViewModelProvider(activity).get(FriendViewModel.class);

            viewModel.createUserLocation(publicCode, privateCode, label, latitude, longitude);

            assertTrue(viewModel.getRepo().existsLocal(publicCode));
            assertTrue(viewModel.getRepo().getRemote(publicCode) != null);

        });
    }

    @Test
    public void test_upsertUserLocation() {
        ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(RegisterActivity.class);
        scenario.onActivity(activity -> {
            String publicCode = "mockPublicCode";
            String privateCode = "mockPrivateCode";
            String label = "mockPrivateCode";
            double latitude = 230;
            double longitude = 120;

            FriendViewModel viewModel = new ViewModelProvider(activity).get(FriendViewModel.class);

            viewModel.upsertUserLocation(publicCode, privateCode, label, latitude+1, longitude+1);

            assertTrue(viewModel.getRepo().existsLocal(publicCode));
            assertTrue(viewModel.getRepo().getRemote(publicCode) != null);
        });
    }
}
