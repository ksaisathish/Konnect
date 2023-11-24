package com.manet.konnect.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.manet.konnect.utils.OnBluetoothDeviceDiscoveredListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BLTConnectionManager {
    private static final int REQUEST_ENABLE_BT = 1;
    private Context context;
    private Activity activity;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private String TAG="BLTConnectionManager";

    private Map<String, BluetoothDevice> bluetoothPeerDevicesMap = new HashMap<>();

    private OnBluetoothDeviceDiscoveredListener listener;

    private final UUID myUUID;

    public BLTConnectionManager(Context context, Activity activity) {
        myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        this.activity=activity;
        this.context=context;

        bluetoothManager = context.getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        isBluetoothAdapterFound();
        enableBluetoothIfNot();
    }

    public BluetoothAdapter getBluetoothAdapter(){
        return bluetoothAdapter;
    }

    
    private void enableBluetoothIfNot() {
        if (!bluetoothAdapter.isEnabled()) {
            Log.i(TAG,"Bluetooth Not Enabled. Sending Request.");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else{
            Log.i(TAG,"Bluetooth is Enabled.");
        }
    }

    public Boolean isBluetoothAdapterFound(){
        if (bluetoothAdapter == null) {
            Log.i(TAG,"BluetoothAdapter Not Found.");
            return false;
        }
        else{
            Log.i(TAG,"BluetoothAdapter Found.");
            return true;
        }
    }
    
    public String getBluetoothDeviceName(){
        return bluetoothAdapter.getName();
    }
    
    public  Map<String, BluetoothDevice> getBluetoothPairedDevicesMap(){
        Log.i(TAG,"Getting BLT Paired Devices.");
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        Map<String, BluetoothDevice> bluetoothPairedDevicesMap = new HashMap<>();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i(TAG,deviceName+" - "+deviceHardwareAddress);
                bluetoothPairedDevicesMap.put(deviceName,device);
            }
        }
        else{
            Log.i(TAG,"There are no paired Bluetooth Devices.");
            bluetoothPairedDevicesMap.put("No Paired Devices.",null);
        }
        return bluetoothPairedDevicesMap;
    }


    public  Map<String, BluetoothDevice> getBluetoothPeerDevicesMap(){
        return bluetoothPeerDevicesMap;
    }

    public void clearBluetoothPeerDeviceMap(){
        bluetoothPeerDevicesMap.clear();
    }
    
    public void startBluetoothDiscovery() {
        Log.i(TAG,"Started Bluetooth Discovery");
        // Register a BroadcastReceiver for device discovery
        context.registerReceiver(discoveryReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        // Start device discovery
        bluetoothAdapter.startDiscovery();
    }

    public void setOnBluetoothDeviceDiscoveredListener(OnBluetoothDeviceDiscoveredListener listener) {
        Log.i(TAG,"Bluetooth Device Discovery On.");
        this.listener = listener;
    }
    
    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Device found during discovery
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();

                if (deviceName != null && deviceAddress != null) {
                    // Add the device to the map
                    bluetoothPeerDevicesMap.put(deviceName, device);
                    Log.i(TAG,"BLT DISC : "+deviceName+" - "+deviceAddress);
                    if (listener != null) {
                        listener.onBluetoothDeviceDiscovered(deviceName, device);
                    }
                }
            }
        }
    };

    
    public void stopBluetoothDiscovery() {
        Log.i(TAG,"Stopped Bluetooth Discovery");

        context.unregisterReceiver(discoveryReceiver);
        // Stop device discovery
        bluetoothAdapter.cancelDiscovery();
    }

    
    public void makeBluetoothDeviceDiscoverable(){
        int requestCode = 1;
        Intent discoverableIntent =new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 100);
        activity.startActivityForResult(discoverableIntent, requestCode);
    }

    public void connectToDevice(BluetoothDevice device) {
        BluetoothSocket socket = null;

        try {
            // Create a BluetoothSocket for the device using the UUID
            socket = device.createRfcommSocketToServiceRecord(myUUID);

            // Connect to the remote device
            socket.connect();

            Log.d(TAG+"BluetoothManager", "Connected to " + device.getName());
        } catch (IOException e) {
            // Handle connection errors
            Log.e(TAG+"BluetoothManager", "Connection error: " + e.getMessage());

            try {
                // Close the socket on error
                socket.close();
            } catch (IOException closeException) {
                Log.e(TAG+"BluetoothManager", "Error closing socket: " + closeException.getMessage());
            }
        }

    }

    public void sendData(byte[] data) {
        // Send data over the Bluetooth connection
    }

    public void stopConnection() {
        // Close the Bluetooth socket and release resources
    }
}
