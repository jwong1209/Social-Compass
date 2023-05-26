package com.example.socialcompass.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import com.example.socialcompass.R;
import com.example.socialcompass.Utilities;
import com.example.socialcompass.model.Location;
import com.example.socialcompass.model.LocationDao;
import com.example.socialcompass.model.LocationDatabase;
import com.example.socialcompass.model.LocationRepository;
import com.example.socialcompass.viewmodel.FriendViewModel;

import java.time.Instant;

public class AddFriendsActivity extends AppCompatActivity {
    LocationDatabase locationDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        setupUIDDisplay();
    }

    public void setupUIDDisplay() {
        TextView UIDDisplay = findViewById(R.id.UIDDisplay);
        String UID;
        SharedPreferences preferences = getSharedPreferences("inputs", MODE_PRIVATE);
        UID = preferences.getString("publicCode", "XXXXX");
        UIDDisplay.setText(UID);
    }

    public void OnCopyClicked(View view) {
        TextView UIDDisplay = findViewById(R.id.UIDDisplay);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String UIDLabel = "UID:";
        String copiedText = UIDDisplay.getText().toString();
        ClipData clip = ClipData.newPlainText(UIDLabel, copiedText);
        clipboard.setPrimaryClip(clip);
    }

    public void OnAddClicked(View view) {
        System.out.println("Click Pressed");
        EditText userInputET = findViewById(R.id.enterUID);
        String userInput = userInputET.getText().toString();
        locationDatabase = LocationDatabase.provide(this);
        LocationRepository locationRepository = new LocationRepository(locationDatabase.getDao());
        LiveData<Location> friendLocationLiveData = locationRepository.getRemote(userInput);
        Log.v("Remote friend: ", friendLocationLiveData.toString());
        Activity activity = this;
        setObserve(friendLocationLiveData,locationRepository, userInput, activity);
    }

    public boolean checkUIDValid(Location friendLocation) {
        if (friendLocation == null || friendLocation.toJSON().length() == 32) {
            Log.v("Head down here", "here");
            Utilities.showAlert(this, "Please enter a valid UID");
            return false;
        } else {
            return true;
        }
    }

    public void setObserve(LiveData<Location> friendLocationLiveData, LocationRepository locationRepository, String userInput, Activity activity) {
        friendLocationLiveData.observe(this, new Observer<Location> () {
            @Override
            public void onChanged(Location friendLocation) {
                if (!checkUIDValid(friendLocation)) {
                    friendLocationLiveData.removeObserver(this);
                    return;
                }
                locationRepository.upsertLocal(friendLocation);
                locationRepository.getSynced(userInput);
                finish();
                Intent compassIntent = new Intent(activity, CompassActivity.class);
                startActivity(compassIntent);
                friendLocationLiveData.removeObserver(this);
            }
        });

        friendLocationLiveData.observeForever(new Observer<Location>() {
            @Override
            public void onChanged(Location friendLocation) {
                if (friendLocation == null || friendLocation.toJSON().length() == 32) {
                    friendLocationLiveData.removeObserver(this);
                    return;
                }
                Log.v("foreverObserve", "foreverObserve");
                locationRepository.getSynced(friendLocation.publicCode);
            }
        });
    }
}