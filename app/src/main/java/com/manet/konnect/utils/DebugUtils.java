package com.manet.konnect.utils;


import com.manet.konnect.utils.debug.BluetoothDebugSettingsFragment;
import com.manet.konnect.utils.debug.WifiDirectDebugSettingsFragment;

import java.util.HashMap;
import java.util.Map;

public class DebugUtils {
    public static Map<String, String> getDebugSettingsMap() {

        Map<String, String> debugSettingsMap = new HashMap<>();
        debugSettingsMap.put("Bluetooth Debug Settings", BluetoothDebugSettingsFragment.class.getName());
        debugSettingsMap.put("Wi-Fi Direct Debug Settings", WifiDirectDebugSettingsFragment.class.getName());
        // Add other debug settings and corresponding fragment class names

        // Add other debug settings
        return debugSettingsMap;
    }
}
