package com.manet.konnect.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.manet.konnect.utils.OnWifiDirectDevicesDiscoveredListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class WifiDirectConnectionManager {
    private final Context context;
    private String wifiP2pDeviceName;
    private String wifiP2pDeviceMacAddress;
    private WifiP2pDevice wifiP2pDevice;
    private final WifiP2pManager wifiP2pManager;
    private final WifiP2pManager.Channel channel;
    private final WifiManager wifiManager;
    private final Map<String, WifiP2pDevice> discoveredDeviceMap;
    private final String TAG="WifiDirectConnectionManager";
    private CountDownLatch deviceInfoLatch;

    public interface InitializationListener {
        void onConnectionManagerInitialized();
    }

    private InitializationListener initializationListener;
    private OnWifiDirectDevicesDiscoveredListener listener;

    public WifiDirectConnectionManager(Context context, InitializationListener listener) {
        this.context = context.getApplicationContext();
        this.wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        this.channel = wifiP2pManager.initialize(context, Looper.getMainLooper(), null);
        this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.discoveredDeviceMap = new HashMap<>();

        requestDeviceInfo();
        initializationListener=listener;

    }

    public String getWifiP2pDeviceName(){
        return wifiP2pDeviceName;
    }

    public WifiP2pManager getWifiP2pManager() {
        return wifiP2pManager;
    }

    public WifiP2pManager.Channel getChannel() {
        return channel;
    }

    @SuppressLint("MissingPermission")
    public void requestDeviceInfo() {

        deviceInfoLatch = new CountDownLatch(1);
        WifiP2pManager.DeviceInfoListener deviceInfoListener = new WifiP2pManager.DeviceInfoListener() {
            @Override
            public void onDeviceInfoAvailable(WifiP2pDevice device) {
                // Process the device information
                wifiP2pDeviceName = device.deviceName;
                wifiP2pDeviceMacAddress = device.deviceAddress;
                wifiP2pDevice = device;
                deviceInfoLatch.countDown(); // Release the latch
                Log.d(TAG, "Device Name: " + wifiP2pDeviceName);
                Log.d(TAG, "Device Address: " + wifiP2pDeviceMacAddress);
                if(initializationListener!=null)
                    initializationListener.onConnectionManagerInitialized();
            }
        };
        wifiP2pManager.requestDeviceInfo(channel, deviceInfoListener);
    }

    // Method to discover available WiFi Direct devices
    @SuppressLint("MissingPermission")
    public void discoverPeers() {
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Registration of PeerListListener inside onSuccess
                registerPeerListListener();
            }

            @Override
            public void onFailure(int reason) {
                Log.i(TAG,"Discovery Failure : "+reason);
            }
        });
    }

    // Method to register the PeerListListener
    @SuppressLint("MissingPermission")
    private void registerPeerListListener() {
        wifiP2pManager.requestPeers(channel, peerList -> {
            discoveredDeviceMap.clear(); // Clear the existing device map
            for (WifiP2pDevice device : peerList.getDeviceList()) {
                discoveredDeviceMap.put(device.deviceName, device);
                Log.i(TAG,"Device : "+device.deviceName);
            }

            if(discoveredDeviceMap.size()==0){

                discoveredDeviceMap.put("No Wifi Direct Devices Discovered.",null);
                Log.i(TAG,"No Wifi Direct Devices Discovered.");
                showToast("No Wifi Direct Devices Discovered.",Toast.LENGTH_SHORT);
            }
            else{
                Log.i(TAG,"No. of Devices Discovered : "+discoveredDeviceMap.size());
            }

            if (listener != null) {
                listener.onWifiDirectDevicesDiscovered((HashMap<String, WifiP2pDevice>) discoveredDeviceMap);
            }

            // Handle the updated device map as needed
        });
    }


    // Method to make the device discoverable
    @SuppressLint("MissingPermission")
    public void makeDeviceDiscoverable(WifiP2pManager.ActionListener actionListener) {
        wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Device is now discoverable ");
                showToast("Device is now discoverable",Toast.LENGTH_SHORT);
                if (actionListener != null) {
                    actionListener.onSuccess();
                }
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "Failed to create a group: " + reason);
                showToast("Failed to start discovery. Try again in sometime!",Toast.LENGTH_SHORT);
                if(reason==2){
                    stopDeviceDiscovery(null);
                }
                if (actionListener != null) {
                    actionListener.onFailure(reason);
                }

            }
        });
    }

    public void stopDeviceDiscovery(WifiP2pManager.ActionListener actionListener){
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Device is no longer discoverable.");
                showToast("Device is no longer discoverable.",Toast.LENGTH_SHORT);
                if (actionListener != null) {
                    actionListener.onSuccess();
                }
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "Failed to make device undiscoverable : " + reason);
                showToast("Failed to make device undiscoverable. Try again in sometime.",Toast.LENGTH_SHORT);
                if (actionListener != null) {
                    actionListener.onFailure(reason);
                }
            }
        });
    }

    // Method to connect to a WiFi Direct device
    @SuppressLint("MissingPermission")
    public void connectToDevice(WifiP2pDevice device, WifiP2pManager.ActionListener actionListener) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Connection initiated with device: " + device.deviceName);
                showToast("Connection initiated with device: "+device.deviceName,Toast.LENGTH_SHORT);
                if (actionListener != null) {
                    actionListener.onSuccess();
                }
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "Failed to connect to device: " + device.deviceName + ", Reason: " + reason);
                showToast("Failed to connect to device: " + device.deviceName,Toast.LENGTH_SHORT);
                if (actionListener != null) {
                    actionListener.onFailure(reason);
                }
            }
        });
    }


    // Method to store discovered device details in the device map
    public void addDevice(String deviceName, WifiP2pDevice device) {
        discoveredDeviceMap.put(deviceName, device);
    }

    // Method to get the device map
    public Map<String, WifiP2pDevice> getDiscoveredDeviceMap() {
        return discoveredDeviceMap;
    }


    // Method to disconnect from a connected WiFi Direct device
    public void disconnect(WifiP2pManager.ActionListener actionListener) {
        wifiP2pManager.removeGroup(channel, actionListener);
    }

    // Method to check if WiFi Direct is supported on the device
    public boolean isWifiDirectSupported() {
        return wifiManager.isP2pSupported();
    }

    // Method to check if WiFi Direct is enabled
    public boolean isWifiDirectEnabled() {
        if (wifiManager != null) {
            return wifiManager.isP2pSupported() && wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
        }
        return false;
    }

    // Method to retrieve detailed information about a WiFi Direct device
    @SuppressLint("MissingPermission")
    public void requestDeviceDetails(WifiP2pDevice device, WifiP2pManager.ActionListener actionListener) {
        wifiP2pManager.requestGroupInfo(channel, group -> {
            // Extract device details from 'group' if needed
            // For example:
            if (group != null) {
                for (WifiP2pDevice peer : group.getClientList()) {
                    if (peer.deviceAddress.equals(device.deviceAddress)) {
                        wifiP2pManager.requestConnectionInfo(channel, info -> {
                            if (info != null) {
                                if (info.isGroupOwner) {
                                    // This device is the group owner
                                    // Perform required actions
                                } else {
                                    // This device is a client
                                    // Perform required actions
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void setOnWifiDirectDevicesDiscoveredListener(OnWifiDirectDevicesDiscoveredListener listener) {
        Log.i(TAG,"Finding WifI Direct Devices Listener On.");
        this.listener = listener;
    }

    private void showToast(String message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    // Additional methods for managing group owner, service discovery, etc., to be added here
}

