package com.example.socialcompass.activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.example.socialcompass.R;
import com.example.socialcompass.Utilities;
import com.example.socialcompass.compass.Time;
import com.example.socialcompass.model.LocationAPI;
import com.example.socialcompass.viewmodel.FriendViewModel;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    SharedPreferences savedLocationData;
    SharedPreferences.Editor editor;

    private FriendViewModel viewModel;

    EditText userNameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = setupViewModel();
        setContentView(R.layout.activity_register);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            Log.d("onCreate", " requestPermissions!!!!!!!!!!!!");
        }
        Time.start_timer();
        savedLocationData = getSharedPreferences("inputs", MODE_PRIVATE);
        editor = savedLocationData.edit();
        userNameET = findViewById(R.id.username);
        fillBlanksWithSharedPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean saveProfile() {
        String username = userNameET.getText().toString();
        username = username.trim();

        if (username.length() == 0) {
            Utilities.showAlert(this, "Please enter a username");
            editor.putBoolean("initialized", false);
            editor.apply();
            return false;
        }
        editor.putBoolean("initialized", true);
        editor.putString("inputtedUsername", username);
        editor.apply();

        return true;
    }

    private FriendViewModel setupViewModel() {
        return new ViewModelProvider(this).get(FriendViewModel.class);
    }

    public void fillBlanksWithSharedPreferences() {
        userNameET.setText(savedLocationData.getString("inputtedUsername", ""));
    }

    public Boolean OnSubmitClicked(View view) {
        EditText mockURL = (EditText) findViewById(R.id.mockURL);
        editor.putString("mockUrl", mockURL.getText().toString());
        editor.apply();
        LocationAPI.setURL(mockURL.getText().toString());
        Boolean saveSuccess = saveProfile();
        if (saveSuccess == false) {
            return false;
        }

        finish();

        //String publicCode = "jasontest1011";
        String publicCode = UUID.randomUUID().toString();
        String privateCode = "privateCode";
        editor.putString("publicCode", publicCode);
        editor.putString("privateCode", privateCode);
        editor.apply();

        String label = savedLocationData.getString("inputtedUsername", "");

        viewModel.createUserLocation(publicCode, privateCode, label, 0, 0);
        Intent welcomeIntent = new Intent(this, WelcomeActivity.class);
        startActivity(welcomeIntent);
        return true;
    }
}