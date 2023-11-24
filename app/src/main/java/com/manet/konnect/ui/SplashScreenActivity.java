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


        // Create an ObjectAnimator to animate alpha (fade in)
        ObjectAnimator textFadeInAnimator = ObjectAnimator.ofFloat(splashScreenTitle, "alpha", 0f, 1f);
        textFadeInAnimator.setDuration(5000); // Animation duration in milliseconds

        ObjectAnimator logoFadeInAnimator = ObjectAnimator.ofFloat(appLogo, "alpha", 0f, 1f);
        logoFadeInAnimator.setDuration(3000); // Animation duration in milliseconds

        // Start the animation
        textFadeInAnimator.start();
        logoFadeInAnimator.start();

        handler.postDelayed(checkPermissionsRunnable,1000);
    }

    private Runnable checkPermissionsRunnable=new Runnable(){
        @Override
        public void run() {
            checkPermissionsAndMoveToNextActivity();
            handler.postDelayed(this,3000);
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
        Log.i("Closed","Splash Screen Closed.");
    }
}