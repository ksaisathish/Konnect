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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressLint("MissingPermission")
public class ConnectionManager {
    private static final int REQUEST_ENABLE_BT = 1;
    private Context context;
    private Activity activity;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private String TAG="ConnectionManager";

    private Map<String, String> bluetoothPeerDevicesMap = new HashMap<>();

    private OnBluetoothDeviceDiscoveredListener listener;

    public ConnectionManager(Context context,Activity activity) {
        this.activity=activity;
        this.context=context;
        TAG+=context.getClass().getSimpleName();
        bluetoothManager = context.getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        isBluetoothAdapterFound();
        enableBluetoothIfNot();
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
    
    public  Map<String, String> getBluetoothPairedDevicesMap(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        Map<String, String> bluetoothPairedDevicesMap = new HashMap<>();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i(TAG,deviceName+" - "+deviceHardwareAddress);
                bluetoothPairedDevicesMap.put(deviceName,deviceHardwareAddress);
            }
        }
        else{
            Log.i(TAG,"There are no paired Bluetooth Devices.");
            bluetoothPairedDevicesMap.put("No Paired Devices.","-");
        }
        return bluetoothPairedDevicesMap;
    }


    public  Map<String, String> getBluetoothPeerDevicesMap(){
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
                    bluetoothPeerDevicesMap.put(deviceName, deviceAddress);
                    Log.i(TAG,"BT DISC : "+deviceName+" - "+deviceAddress);
                    if (listener != null) {
                        listener.onBluetoothDeviceDiscovered(deviceName, deviceAddress);
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
        // Establish a Bluetooth connection to the selected device
    }

    public void sendData(byte[] data) {
        // Send data over the Bluetooth connection
    }

    public void stopConnection() {
        // Close the Bluetooth socket and release resources
    }
}
