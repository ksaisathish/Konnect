package com.manet.konnect.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.manet.konnect.R;
import com.manet.konnect.core.PermissionManager;

public class SplashScreenActivity extends AppCompatActivity {

    private TextView splashScreenTitle;
    private ImageView appLogo;
    private int rotationAngle = 0;
    private Handler handler;
    private final String TAG="SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        splashScreenTitle = findViewById(R.id.SplashScreenTitle);

        appLogo = findViewById(R.id.appLogo);
        handler = new Handler();
        Log.i(TAG,"Started");

        // Start the rotation thread
        handler.postDelayed(rotationRunnable, 75);


        // Create an ObjectAnimator to animate alpha (fade in)
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(splashScreenTitle, "alpha", 0f, 1f);
        fadeInAnimator.setDuration(5000); // Animation duration in milliseconds

        // Start the animation
        fadeInAnimator.start();

        handler.postDelayed(checkPermissionsRunnable,3000);
    }

    private Runnable checkPermissionsRunnable=new Runnable(){
        @Override
        public void run() {
            checkPermissionsAndMoveToNextActivity();
            handler.postDelayed(this,5000);
        }
    };

    private Runnable rotationRunnable = new Runnable() {
        @Override
        public void run() {
            // Increment the rotation angle
            rotationAngle = (rotationAngle + 10) % 360;
            appLogo.setRotation(rotationAngle);
            // Call this runnable again after a delay (e.g., 100 milliseconds)
            handler.postDelayed(this, 75);
        }
    };

    private void checkPermissionsAndMoveToNextActivity() {
        Log.i(TAG,"Into the function");

        PermissionManager pm=new PermissionManager(this,this);
        pm.checkPermissionsAndFeatures();

        if(pm.getAllPermissionsGiven()){
            Intent intent;
            Boolean isLoggedIn;

            //Logic for Checking if User is Logged In Already.
            isLoggedIn=false;

            if(isLoggedIn){
                intent= new Intent(SplashScreenActivity.this, MainActivity.class);
            }
            else{
                intent= new Intent(SplashScreenActivity.this, GetStartedActivity.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the repeating task when the activity is destroyed\
        handler.removeCallbacks(checkPermissionsRunnable);
        handler.removeCallbacks(rotationRunnable);
        Log.i("Closed","Splash Screen Closed.");
    }
}