package com.example.socialcompass.activity;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.socialcompass.R;

public class WelcomeActivity extends AppCompatActivity {

    SharedPreferences savedLocationData;
    TextView uidTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        savedLocationData = getSharedPreferences("inputs", MODE_PRIVATE);
        uidTV = findViewById(R.id.welcomeUIDLabel);

        uidTV.setText(savedLocationData.getString("publicCode", ""));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public Boolean OnSubmitClicked(View view) {
        Intent testSaved = new Intent(this, CompassActivity.class);
        startActivity(testSaved);
        return true;
    }
}