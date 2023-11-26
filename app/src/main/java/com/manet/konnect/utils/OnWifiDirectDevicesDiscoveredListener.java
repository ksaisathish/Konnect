package com.manet.konnect.utils;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.HashMap;

public interface OnWifiDirectDevicesDiscoveredListener {
    void onWifiDirectDevicesDiscovered(HashMap<String, WifiP2pDevice> wifiDirectDevicesMap);
}
