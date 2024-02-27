package com.manet.konnect.utils;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;

import java.util.HashMap;
import java.util.List;

public interface OnNearbyConnectionDevicesDiscoveredListener {
    void onNearbyConnectionDevicesDiscovered(List<DiscoveredEndpointInfo> discoveredEndpoints);

    void onNearbyConnectionDevicesDevicesConnected(List<String> connectedDevices);
}
