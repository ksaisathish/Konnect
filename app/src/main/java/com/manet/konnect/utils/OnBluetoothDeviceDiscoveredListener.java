package com.manet.konnect.utils;

import android.bluetooth.BluetoothDevice;

public interface OnBluetoothDeviceDiscoveredListener {
    void onBluetoothDeviceDiscovered(String deviceName, BluetoothDevice device);
}
