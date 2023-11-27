package com.manet.konnect.core;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private Context context;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       Context context) {
        super();
        this.wifiP2pManager = manager;
        this.channel = channel;
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check if Wi-Fi P2P is supported/enabled
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wi-Fi Direct is enabled, perform appropriate actions
                Log.d("WiFiDirect", "Wi-Fi Direct is enabled");
                // Perform actions like initiating a connection, discovery, etc.
                // Example: wifiP2pManager.discoverPeers(channel, new ActionListener() { ... });
            } else {
                // Wi-Fi Direct is not enabled
                Log.d("WiFiDirect", "Wi-Fi Direct is not enabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Peer list has changed
            // Update available peers
            if (wifiP2pManager != null) {
                wifiP2pManager.requestPeers(channel, peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Connection state changed, i.e., connected or disconnected
            if (wifiP2pManager == null) {
                return;
            }

            // Get current connection status
            wifiP2pManager.requestConnectionInfo(channel, connectionInfoListener);
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // This device details have changed
            // Update device info
            WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            // Handle device info change as needed
        }
    }

    // Implement PeerListListener for handling available peers
    private WifiP2pManager.PeerListListener peerListListener = peerList -> {
        // Handle the list of available peers
        // For example: iterate through peerList to get device details
    };

    // Implement ConnectionInfoListener to handle connection details
    private WifiP2pManager.ConnectionInfoListener connectionInfoListener =
            info -> {
                // Get connection info and perform necessary actions
                if (info != null) {
                    // Connection details available, log or process them
                    Log.d("WiFiDirect", "Connection Info: " + info.toString());
                }
            };
}
