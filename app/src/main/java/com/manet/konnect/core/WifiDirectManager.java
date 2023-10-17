package com.manet.konnect.core;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WifiDirectManager {
    private WifiP2pManager wifiP2pManager;
    private WifiP2pDevice wifiP2pDevice;
    private Channel channel;
    private Context context;
    private boolean isWifiP2pEnabled;
    private List<WifiP2pDevice> deviceList;
    private String androidId;

    private String deviceName;
    private String deviceMacAddress;
    private final IntentFilter intentFilter = new IntentFilter();
    private static final String TAG = "WifiDirectManager";
    private CountDownLatch deviceInfoLatch;

    public WifiDirectManager(Context context) {

        this.context = context;

        // Initialize WiFi Direct manager and channel
        wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(context, context.getMainLooper(), null);

        deviceInfoLatch = new CountDownLatch(1);
        requestDeviceInfo();
        Log.i(TAG, "HERE!!!");

        // Initialize the intent filter
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
    }

    public void requestDeviceInfo() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            WifiP2pManager.DeviceInfoListener deviceInfoListener = new WifiP2pManager.DeviceInfoListener() {
                @Override
                public void onDeviceInfoAvailable(WifiP2pDevice device) {
                    // Process the device information
                    deviceName = device.deviceName;
                    deviceMacAddress = device.deviceAddress;
                    wifiP2pDevice = device;
                    deviceInfoLatch.countDown(); // Release the latch
                    // Do something with the device information
                    // For example, log the device info
                    Log.d(TAG, "Device Name: " + deviceName);
                    Log.d(TAG, "Device Address: " + deviceMacAddress);
                }
            };
            if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this.context, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details
                Log.i("Device", "Permissions Checked!!");
            }
            wifiP2pManager.requestDeviceInfo(channel, deviceInfoListener);

        }
    }

    public String getUniqueAndroidId() {
        androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceMacAddress() {
        return deviceMacAddress;
    }


    public void changeDeviceName(String newDeviceName) {
        try {
            Method setDeviceNameMethod = wifiP2pManager.getClass().getDeclaredMethod("setDeviceName",new Class[] { WifiP2pManager.Channel.class, String.class,WifiP2pManager.ActionListener.class });

            // Make the setDeviceName() method accessible
            setDeviceNameMethod.setAccessible(true);

            // Invoke the setDeviceName() method
            setDeviceNameMethod.invoke(wifiP2pManager, channel, newDeviceName, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "Name successfully changed");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG, "Name change failed: " + reason);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void detectAndConnectDevices() {
        // Implement device detection and connection logic here
        // This will involve using WifiP2pManager to discover and connect devices
        // For simplicity, we'll just log and show a toast for detected devices

        String message = "Detecting and connecting devices...";
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        // Log to console
        System.out.println(message);

        // Discover peers
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this.context, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                String successMessage = "Peer discovery initiated";
                Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show();
                // Log to console
                System.out.println(successMessage);
            }

            @Override
            public void onFailure(int reasonCode) {
                String failureMessage = "Peer discovery failed. Error code: " + reasonCode;
                Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show();
                // Log to console
                System.out.println(failureMessage);
            }
        });
    }

    // Register the BroadcastReceiver with the intent values to be matched
    public void registerReceiver(BroadcastReceiver receiver) {
        context.registerReceiver(receiver, intentFilter);
    }

    // Unregister the BroadcastReceiver
    public void unregisterReceiver(BroadcastReceiver receiver) {
        context.unregisterReceiver(receiver);
    }

    // Additional methods for managing WiFi Direct connections can be added here

}
