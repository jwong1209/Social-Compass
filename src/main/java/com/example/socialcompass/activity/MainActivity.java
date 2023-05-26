package com.example.socialcompass.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;

import com.example.socialcompass.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (getSharedPreferences("inputs", MODE_PRIVATE).getBoolean("initialized", false) == false) {
            Intent intent = new Intent(this, RegisterActivity.class);
            Log.d("onCreate", " RegisterActivity!!!!!!!!!!!!");
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, CompassActivity.class);
            startActivity(intent);
        }
    }
}