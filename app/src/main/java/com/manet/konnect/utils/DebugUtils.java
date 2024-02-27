package com.manet.konnect.utils;


import com.manet.konnect.utils.debug.BluetoothDebugSettingsFragment;
import com.manet.konnect.utils.debug.BluetoothLEDebugSettingsFragment;
import com.manet.konnect.utils.debug.NearbyConnectionsDebugSettingsFragment;
import com.manet.konnect.utils.debug.NotificationDebugSettingsFragment;
import com.manet.konnect.utils.debug.WifiDebugSettingsFragment;
import com.manet.konnect.utils.debug.WifiDirectDebugSettingsFragment;

import java.util.HashMap;
import java.util.Map;

public class DebugUtils {
    public static Map<String, String> getDebugSettingsMap() {

        Map<String, String> debugSettingsMap = new HashMap<>();
        debugSettingsMap.put("Bluetooth Debug Settings", BluetoothDebugSettingsFragment.class.getName());
        debugSettingsMap.put("Bluetooth LE Debug Settings", BluetoothLEDebugSettingsFragment.class.getName());
        debugSettingsMap.put("Wi-FI Debug Settings", WifiDebugSettingsFragment.class.getName());
        debugSettingsMap.put("Wi-Fi Direct Debug Settings", WifiDirectDebugSettingsFragment.class.getName());
        debugSettingsMap.put("Notifications Debug Settings", NotificationDebugSettingsFragment.class.getName());
        debugSettingsMap.put("Nearby Connections Debug Settings", NearbyConnectionsDebugSettingsFragment.class.getName());
        // Add other debug settings and corresponding fragment class names

        // Add other debug settings
        return debugSettingsMap;
    }
}
