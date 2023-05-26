package com.example.socialcompass.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialcompass.compass.Compass;
import com.example.socialcompass.compass.LocationService;
import com.example.socialcompass.compass.CompassLocation;
import com.example.socialcompass.compass.OrientationService;
import com.example.socialcompass.R;
import com.example.socialcompass.compass.RelativeAngle;
import com.example.socialcompass.compass.Time;
import com.example.socialcompass.model.Location;
import com.example.socialcompass.model.LocationAPI;
import com.example.socialcompass.model.LocationDatabase;
import com.example.socialcompass.model.LocationRepository;
import com.example.socialcompass.viewmodel.FriendViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class CompassActivity extends AppCompatActivity {
    private OrientationService orientationService;
    private LocationService locationService;
    private SharedPreferences savedLocationData;
    private SharedPreferences.Editor editor;
    private FriendViewModel viewModel;
    private CompassLocation user;
    private boolean hasObserved = false;
    private LocationDatabase locationDatabase;
    private Compass c;

    private float scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        scale = this.getBaseContext().getResources().getDisplayMetrics().density;
        savedLocationData = getSharedPreferences("inputs", MODE_PRIVATE);
        editor = savedLocationData.edit();
        viewModel = setupViewModel();


        orientationService = new OrientationService(this);
        locationService = new LocationService(this);
        HashMap<String, ArrayList<Object>> listOfFriends = new HashMap<>();

        savedLocationData = getSharedPreferences("inputs", MODE_PRIVATE);
        LocationAPI.setURL(savedLocationData.getString("mockURL", "https://socialcompass.goto.ucsd.edu/location/"));
        user = new CompassLocation(32.877992837606115, -117.23026701911897, "", null);
        c = new Compass(user,viewModel,this, scale);

        c.start_friend_tracking();
        start_orientation_tracking();
        start_location_tracking();
        Time.setImageAndText(findViewById(R.id.gps_online_icon), findViewById(R.id.gps_offline_icon), findViewById(R.id.gps_time_text));
        Time.setActivity(this);
        Time.start_timer();
    }

    public void start_location_tracking() {
        SharedPreferences savedLocationData = getSharedPreferences("inputs", MODE_PRIVATE);
        String publicCode = savedLocationData.getString("publicCode", "");
        String privateCode = savedLocationData.getString("privateCode", "");
        String label = savedLocationData.getString("inputtedUsername", "");

        locationService.getLocation().observeForever(new Observer<Pair<Double, Double>>() {
            @Override
            public void onChanged(Pair<Double, Double> loc) {
                //constantly updates user location on the server and locally
                viewModel.upsertUserLocation(publicCode, privateCode, label, loc.first, loc.second);
                user.setLatitude(loc.first);
                user.setLongitude(loc.second);
                Time.resetTime();
            }
        });
    }

    public FriendViewModel setupViewModel() {
        return new ViewModelProvider(this).get(FriendViewModel.class);
    }

    public void start_orientation_tracking() {
        orientationService.getOrientation().observe(this, x -> {
            this.user.setAngleFacing(x * 180 / Math.PI);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }

    public void onAddFriendClicked(View view) {
        finish();
        Intent inputActivityIntent = new Intent(this, AddFriendsActivity.class);
        startActivity(inputActivityIntent);
    }

    public void onZoomInClicked(View view) {
        c.onZoomInClicked();
    }

    public void onZoomOutClicked(View view) {
        c.onZoomOutClicked();
    }
}