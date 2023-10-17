package com.manet.konnect.core;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.HashMap;
import java.util.Map;

public class PermissionManager extends AppCompatActivity {

    HashMap<String,Integer> permissionRequestCodes;
    Context context;
    Activity activity;
    String TAG="PermissionManager";

    Boolean allPermissionsGiven =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public PermissionManager(Context context,Activity activity){
        allPermissionsGiven=false;
        this.context=context;
        this.activity=activity;
        initPermissionRequestCodes();
    }



    private void initPermissionRequestCodes() {
        permissionRequestCodes = new HashMap<>();
        // Map each permission to its respective request code
        permissionRequestCodes.put(Manifest.permission.ACCESS_FINE_LOCATION, 101);
        permissionRequestCodes.put(Manifest.permission.ACCESS_COARSE_LOCATION, 102);
        permissionRequestCodes.put(Manifest.permission.ACCESS_WIFI_STATE, 103);
        permissionRequestCodes.put(Manifest.permission.CHANGE_WIFI_STATE, 104);
        permissionRequestCodes.put(Manifest.permission.BLUETOOTH, 105);
        permissionRequestCodes.put(Manifest.permission.BLUETOOTH_ADMIN, 106);
        // Add more permissions and request codes as needed
    }

    private boolean isWiFiEnabled() {
        Log.i(TAG,"Checking if Wifi is Enabled.");

        WifiManager wifiMgr = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        return wifiMgr.isWifiEnabled();
    }

    private boolean isBluetoothEnabled() {
        Log.i(TAG,"Checking if Bluetooth is Enabled.");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }


    private void redirectToSettings(String action) {
        Intent intent = new Intent(action);
        activity.startActivity(intent);
        //finish();
    }

    private void redirectToAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }


    public void checkPermissionsAndFeatures() {
        for (Map.Entry<String, Integer> entry : permissionRequestCodes.entrySet()) {
            String permission = entry.getKey();
            int requestCode = entry.getValue();
            Log.i(TAG,"Checking Settings for"+permission);
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                return; // Wait for onRequestPermissionsResult
            }
        }

        // All permissions are granted, check and enable features if needed
        checkAndEnableFeatures();
    }

    private void checkAndEnableFeatures() {
        // Check if Wi-Fi is enabled
        if (!isWiFiEnabled()) {
            redirectToSettings(Settings.Panel.ACTION_WIFI);
            return;
        }

        // Check if Bluetooth is enabled
        if (!isBluetoothEnabled()) {
            redirectToSettings(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            return;
        }

        allPermissionsGiven=true;
        //Move to the next activity
        //moveToNextActivity();
    }

    public boolean getAllPermissionsGiven(){
        return allPermissionsGiven;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG,"Inside Here!");

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, check permissions and features again
            checkPermissionsAndFeatures();
        } else {
            // Permission denied, redirect to settings
            Toast.makeText(this, "Permission denied. Redirecting to settings.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Permission denied. Redirecting to settings.");
            redirectToSettings(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            checkPermissionsAndFeatures();
        }
    }

}
