package com.manet.konnect.utils;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;

import java.util.HashMap;
import java.util.List;

public interface OnWifiDirectDevicesDiscoveredListener {
    void onWifiDirectDevicesDiscovered(HashMap<String, WifiP2pDevice> wifiDirectDevicesMap);

    void onWifiDirectGroupDevicesDiscovered(WifiP2pInfo info, List<WifiP2pDevice> peersList);

    void onPeerDevicesDiscovered(List<WifiP2pDevice> peersList);
}
