package com.example.socialcompass.activity;

import static com.example.socialcompass.activity.InputActivity.Validity.ALLEMPTYERROR;
import static com.example.socialcompass.activity.InputActivity.Validity.INPUTERROR;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.socialcompass.R;
import com.example.socialcompass.Utilities;

public class InputActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    EditText currentHomeLabelET, currentHomeLongET, currentHomeLatET, familyHomeLabelET, familyHomeLongET, familyHomeLatET, friendHomeLabelET, friendHomeLongET, friendHomeLatET, mockPhoneDirection;

    public enum Validity {
        INPUTERROR(-1), FILLEDNOLABEL(0), ALLFILLED(1), ALLEMPTYERROR(2);
        private int value;

        private Validity(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_screen);
        preferences = getSharedPreferences("inputs", MODE_PRIVATE);
        editor = preferences.edit();
        currentHomeLabelET = findViewById(R.id.homeLabel);
        currentHomeLongET = findViewById(R.id.currentHomeLongitude);
        currentHomeLatET = findViewById(R.id.currentHomeLatitude);
        familyHomeLabelET = findViewById(R.id.familyLabel);
        familyHomeLongET = findViewById(R.id.familyHomeLongitude);
        familyHomeLatET = findViewById(R.id.familyHomeLatitude);
        friendHomeLabelET = findViewById(R.id.friendLabel);
        friendHomeLongET = findViewById(R.id.friendHomeLongitude);
        friendHomeLatET = findViewById(R.id.friendHomeLatitude);
        mockPhoneDirection = findViewById(R.id.mock_phone_direction);
        fillBlanksWithSharedPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public Validity checkValid(String label, String longitude, String latitude) {
        // Checking if label, longitude, and latitude are all empty
        if (label.length() == 0 && longitude.length() == 0 && latitude.length() == 0) {
            return ALLEMPTYERROR;
        }
        // checking if everything is filled in
        else if (label.length() != 0 && longitude.length() != 0 && latitude.length() != 0) {
            if (Math.abs(Float.parseFloat(longitude)) > 180 || Math.abs(Float.parseFloat(latitude)) > 90) {
                return INPUTERROR;
            }
            return Validity.ALLFILLED;
        }
        // label not filled in, longitude and latitude filled in
        else if (label.length() == 0 && longitude.length() != 0 && latitude.length() != 0) {
            if (Math.abs(Float.parseFloat(longitude)) > 180 || Math.abs(Float.parseFloat(latitude)) > 90) {
                return INPUTERROR;
            }
            return Validity.FILLEDNOLABEL;
        }
        // Error
        return INPUTERROR;
    }

    public Boolean saveProfile() {
        editor.clear();
        editor.apply();
        //S
        String homeLabel = currentHomeLabelET.getText().toString();
        String familyLabel = familyHomeLabelET.getText().toString();
        String friendLabel = friendHomeLabelET.getText().toString();

        homeLabel = homeLabel.trim();
        familyLabel = familyLabel.trim();
        friendLabel = friendLabel.trim();

        //get long
        String currentHomeLongString = currentHomeLongET.getText().toString();
        String familyHomeLongString = familyHomeLongET.getText().toString();
        String friendHomeLongString = friendHomeLongET.getText().toString();

        //get lat
        String currentHomeLatString = currentHomeLatET.getText().toString();
        String familyHomeLatString = familyHomeLatET.getText().toString();
        String friendHomeLatString = friendHomeLatET.getText().toString();

        //check for invalid inputs
        Validity homeValid = checkValid(homeLabel, currentHomeLongString, currentHomeLatString);
        Validity familyValid = checkValid(familyLabel, familyHomeLongString, familyHomeLatString);
        Validity friendValid = checkValid(friendLabel, friendHomeLongString, friendHomeLatString);

        //If any fields has invalid input, display error message
        if (homeValid.equals(INPUTERROR) || familyValid.equals(INPUTERROR) || friendValid.equals(INPUTERROR)) {
            Utilities.showAlert(this, "Invalid Input(s)");
            editor.putBoolean("initialized", false);
            editor.apply();
            return false;
        } else if (!(homeValid.equals(ALLEMPTYERROR) && familyValid.equals(ALLEMPTYERROR) && friendValid.equals(ALLEMPTYERROR))) {
            // At least one of the field is empty
            if (!homeValid.equals(ALLEMPTYERROR)) {
                Float currentHomeLong = Float.parseFloat(currentHomeLongString);
                Float currentHomeLat = Float.parseFloat(currentHomeLatString);

                if (homeLabel.length() == 0) {
                    homeLabel = "Current Home";
                }
                editor.putString("currentHomeLabel", homeLabel);
                editor.putFloat("currentHomeLong", currentHomeLong);
                editor.putFloat("currentHomeLat", currentHomeLat);
            }

            if (!familyValid.equals(ALLEMPTYERROR)) {
                Float familyHomeLong = Float.parseFloat(familyHomeLongString);
                Float familyHomeLat = Float.parseFloat(familyHomeLatString);

                if (familyLabel.length() == 0) {
                    familyLabel = "Family Home";
                }
                editor.putString("familyHomeLabel", familyLabel);
                editor.putFloat("familyHomeLong", familyHomeLong);
                editor.putFloat("familyHomeLat", familyHomeLat);
            }

            if (!friendValid.equals(ALLEMPTYERROR)) {
                Float friendHomeLong = Float.parseFloat(friendHomeLongString);
                Float friendHomeLat = Float.parseFloat(friendHomeLatString);

                if (friendLabel.length() == 0) {
                    friendLabel = "Friend Home";
                }
                editor.putString("friendHomeLabel", friendLabel);
                editor.putFloat("friendHomeLong", friendHomeLong);
                editor.putFloat("friendHomeLat", friendHomeLat);
            }
            if (mockPhoneDirection.getText().toString().trim().length() != 0) {
                editor.putFloat("mockPhoneDirectionValue", Float.parseFloat(mockPhoneDirection.getText().toString().trim()));
            } else {
                editor.putFloat("mockPhoneDirectionValue", 360);
            }
            editor.putBoolean("initialized", true);
            editor.apply();
            return true;
        } else {
            //No location is given
            editor.putBoolean("initialized", false);
            editor.apply();
            Utilities.showAlert(this, "Please enter at least one location");
            return false;
        }

    }

    public void fillBlanksWithSharedPreferences() {
        currentHomeLabelET.setText(preferences.getString("currentHomeLabel", ""));
        if (preferences.getFloat("currentHomeLong", 181) == 181) {
            currentHomeLongET.setText("");
        } else {
            currentHomeLongET.setText(String.valueOf(preferences.getFloat("currentHomeLong", 181)));
        }
        if (preferences.getFloat("currentHomeLat", 91) == 91) {
            currentHomeLatET.setText("");
        } else {
            currentHomeLatET.setText(String.valueOf(preferences.getFloat("currentHomeLat", 181)));
        }
        familyHomeLabelET.setText(preferences.getString("familyHomeLabel", ""));
        if (preferences.getFloat("familyHomeLong", 181) == 181) {
            familyHomeLongET.setText("");
        } else {
            familyHomeLongET.setText(String.valueOf(preferences.getFloat("familyHomeLong", 181)));
        }
        if (preferences.getFloat("familyHomeLat", 91) == 91) {
            familyHomeLatET.setText("");
        } else {
            familyHomeLatET.setText(String.valueOf(preferences.getFloat("familyHomeLat", 181)));
        }
        friendHomeLabelET.setText(preferences.getString("friendHomeLabel", ""));
        if (preferences.getFloat("friendHomeLong", 181) == 181) {
            friendHomeLongET.setText("");
        } else {
            friendHomeLongET.setText(String.valueOf(preferences.getFloat("friendHomeLong", 181)));
        }
        if (preferences.getFloat("friendHomeLat", 91) == 91) {
            friendHomeLatET.setText("");
        } else {
            friendHomeLatET.setText(String.valueOf(preferences.getFloat("friendHomeLat", 181)));
        }

        if (preferences.getFloat("mockPhoneDirectionValue", 360) == 360) {
            mockPhoneDirection.setText("");
        } else {
            mockPhoneDirection.setText(String.valueOf(preferences.getFloat("mockPhoneDirectionValue", 360)));
        }
    }

    public Boolean OnSubmitClicked(View view) {
        Boolean saveSuccess = saveProfile();
        if (saveSuccess == false) {
            return false;
        }
        finish();
        Intent compassIntent = new Intent(this, CompassActivity.class);
        Log.d("Compass", "Compass Screen");
        startActivity(compassIntent);
        return true;
    }
}