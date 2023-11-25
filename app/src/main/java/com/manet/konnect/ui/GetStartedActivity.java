package com.manet.konnect.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.manet.konnect.R;

public class GetStartedActivity extends AppCompatActivity {

    Button startButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        startButton= findViewById(R.id.getStarted);

        startButton.setOnClickListener(v -> {
            // Create an Intent to start the next activity
            Intent intent = new Intent(GetStartedActivity.this, RegistrationActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }
}