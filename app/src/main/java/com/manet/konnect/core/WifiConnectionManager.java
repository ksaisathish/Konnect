package com.manet.konnect.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;

import java.util.List;

public class WifiConnectionManager {
    private final WifiManager wifiManager;
    private Context context;
    private final String TAG="WifiConnectionManager";

    public WifiConnectionManager(Context context) {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.context=context;
    }

    // Method to retrieve a list of available Wi-Fi networks
    @SuppressLint("MissingPermission")
    public List<ScanResult> getAvailableNetworks() {
        return wifiManager.getScanResults();
    }

    public void connectToWifi2(String ssid, String password) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WifiNetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                    .setSsid(ssid)
                    .setWpa2Passphrase(password)
                    .build();

            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .setNetworkSpecifier(specifier)
                    .build();

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                connectivityManager.requestNetwork(networkRequest, new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        connectivityManager.bindProcessToNetwork(network);
                        // Don't forget to release the network when done:
                        // connectivityManager.bindProcessToNetwork(null);
                    }

                    @Override
                    public void onUnavailable() {
                        // Connection failed
                    }
                });
            }
        }
    }

    public void disconnectFromWifi2() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                        // Specify SSID, etc., if needed
                        .build();

                NetworkRequest networkRequest = new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .setNetworkSpecifier(specifier)
                        .build();

                connectivityManager.unregisterNetworkCallback(new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        super.onAvailable(network);
                        // Release the network when available
                        connectivityManager.bindProcessToNetwork(null);
                        connectivityManager.unregisterNetworkCallback(this);
                    }
                });
            }
        }
    }


    public void connectToWifi(String ssid, String password){
        WifiUtils.withContext(context)
                .connectWith(ssid, password)
                .setTimeout(40000)
                .onConnectionResult(new ConnectionSuccessListener() {
                    @Override
                    public void success() {
                        Log.i(TAG,"Wifi Connected Successfully.");
                    }

                    @Override
                    public void failed(@NonNull ConnectionErrorCode errorCode) {
                        Log.i(TAG,"Wifi Connection Failure - "+errorCode.toString());
                    }
                })
                .start();
    }
}
