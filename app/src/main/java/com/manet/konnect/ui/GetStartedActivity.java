package com.manet.konnect.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.manet.konnect.R;
import com.manet.konnect.db.DatabaseUtils;

public class GetStartedActivity extends AppCompatActivity {

    Button startButton;
    private DatabaseUtils databaseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        startButton= findViewById(R.id.getStarted);


        databaseUtils = DatabaseUtils.getInstance(this);

        startButton.setOnClickListener(v -> {
            Intent intent;
            databaseUtils.checkIfProfileExists(exists -> {
                if (exists) {
                    // Profile exists, navigate to MainActivity
                    startActivity(new Intent(GetStartedActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish(); // Finish the splash activity
                } else {
                    // Profile doesn't exist, navigate to RegistrationActivity
                    startActivity(new Intent(GetStartedActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                    //startActivity(new Intent(GetStartedActivity.this, RegistrationActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });


        });
    }
}