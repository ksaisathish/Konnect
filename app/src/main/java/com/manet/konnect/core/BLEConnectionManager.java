package com.manet.konnect.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import com.manet.konnect.utils.OnBluetoothDeviceDiscoveredListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BLEConnectionManager
{
    private static final int REQUEST_ENABLE_BT = 1;
    private Context context;
    private Activity activity;
    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothLeScanner bluetoothLeScanner;

    private AdvertiseSettings bleAdSettings;
    private AdvertiseData bleAdData;
    private BluetoothLeAdvertiser bleAdvertiser;

    BluetoothGattServer bluetoothGattServer;

    private static final UUID konnectServiceUUID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");

    private static final  UUID characteristic1UUID = UUID.fromString("7fbf257e-0acd-41c4-9ae5-d73197161672");
    // Add more characteristics as needed


    private BluetoothGattService myService = new BluetoothGattService(
            konnectServiceUUID, BluetoothGattService.SERVICE_TYPE_SECONDARY
    );

    private BluetoothGattCharacteristic myCharacteristic = new BluetoothGattCharacteristic(
            characteristic1UUID,
            BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE
    );




    private String TAG="BLEConnectionManager";
    private Map<String, BluetoothDevice> blePeerDevicesMap = new HashMap<>();
    private OnBluetoothDeviceDiscoveredListener listener;

    public BLEConnectionManager(Context context,Activity activity) {
        this.context=context;
        this.activity=activity;
        bluetoothManager=(BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter=bluetoothManager.getAdapter();
        //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner=bluetoothAdapter.getBluetoothLeScanner();
        isBluetoothAdapterFound();
        enableBluetoothIfNot();
        myService.addCharacteristic(myCharacteristic);

        bleAdSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true) // Set to true if you want to allow connections
                .build();

        ParcelUuid serviceParcelUuid = new ParcelUuid(konnectServiceUUID);

        bleAdData= new AdvertiseData.Builder()
                .setIncludeDeviceName(true) // Include device name in the advertisement
                .addServiceUuid(serviceParcelUuid) // Advertise your service UUID
                .setIncludeTxPowerLevel(true)
                .build();
        //bluetoothGattServer.addService(myService);
    }

    public String getBluetoothDeviceName(){
        return bluetoothAdapter.getName();
    }

    public String getBluetoothMac(){
        return bluetoothAdapter.getAddress();
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

    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();
                    String deviceName = device.getName();
                    String deviceAddress = device.getAddress();
                    Log.i(TAG,"BLE DISC : "+deviceName+" - "+deviceAddress);
                    if(deviceName==null){
                        return;
                        //deviceName=deviceAddress;
                    }
                    blePeerDevicesMap.put(deviceName,device);
                    //Log.i(TAG,"BLE DISC : "+deviceName+" - "+deviceAddress);
                    if (listener != null) {
                        listener.onBluetoothDeviceDiscovered(deviceName, device);
                    }
                }
            };

    public Map<String, BluetoothDevice> getBLEPeerDevicesMap(){
        return blePeerDevicesMap;
    }
    public void discoverBLEDevices() {
        // Start BLE device discovery
        bluetoothLeScanner.startScan(leScanCallback);
    }

    public void discoverKonnectBLEDevices() {

        // Replace YOUR_SERVICE_UUID with the UUID of the service you want to discover
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(konnectServiceUUID))
                .build();

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // Adjust scan mode as needed
                .build();

        bluetoothLeScanner.startScan(Collections.singletonList(scanFilter), scanSettings, leScanCallback);
    }

        public void clearBluetoothPeerDeviceMap(){
        blePeerDevicesMap.clear();
    }

    public void stopDiscovery(){

        Log.i(TAG,"Stopping BLE Discovery!!");
        showToast("Stopping BLE Discovery!", Toast.LENGTH_SHORT);
        bluetoothLeScanner.stopScan(leScanCallback);
    }

    public void startGattServer(){
        bluetoothGattServer= bluetoothManager.openGattServer(context.getApplicationContext(), gattServerCallback);
        bluetoothGattServer.addService(myService);
        Log.i(TAG,"Starting Gatt Server!!");
        showToast("Gatt Server Starteds!!", Toast.LENGTH_SHORT);

    }

    public void stopGattServer(){
        if (bluetoothGattServer != null) {
            bluetoothGattServer.close();
        }
        Log.i(TAG,"Stopping Gatt Server!!");
        showToast("Stopping Gatt Server!", Toast.LENGTH_SHORT);


    }

    // Implement BluetoothGattServerCallback
    public BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            Looper.prepare();
            // Handle connection state changes
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // zted
                Log.i(TAG,"Device Connected using BLE : "+device.getName());
                showToast("Device Connected using BLE : "+device.getName(), Toast.LENGTH_SHORT);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Device disconnected
                Log.i(TAG,"Device Disconnected using BLE : "+device.getName());
                showToast("Device Disconnected using BLE : "+device.getName(), Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            // Handle read requests
            if (characteristic.getUuid().equals(myCharacteristic)) {
                // Respond to the read request for YOUR_CHARACTERISTIC_UUID
                // Retrieve the value to be read from the characteristic and send response
                // bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
            }
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            // Handle write requests
            if (characteristic.getUuid().equals(myCharacteristic)) {
                // Process the received value and perform actions based on the write request
                // Optionally, send a response to the client if responseNeeded is true
                // if (responseNeeded) {
                //     bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
                // }
            }
        }

        // Other callbacks to handle notifications, descriptors, services, etc.
        // Override the necessary methods as per your requirements
    };

    public int startBLEAdvertising(){
        bleAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (bleAdvertiser == null) {
            Log.e(TAG,"BLE Advertising Not supported!!");
            return -1;
        }
        else{
            Log.i(TAG,"BLE Advertising Supported!!");
            bleAdvertiser.startAdvertising(bleAdSettings, bleAdData, bleAdvertisingCallback);
            startGattServer();
            return 0;
        }
    }

    private AdvertiseCallback bleAdvertisingCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.i(TAG,"BLE Advertising Started!!");
            showToast("BLE Advertising Started!!", Toast.LENGTH_SHORT);

            // Advertising started successfully
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.e(TAG,"BLE Advertising Failure - " +errorCode);
            showToast("BLE Advertising Failure - " +errorCode, Toast.LENGTH_SHORT);
            // Advertising failed, handle the error
            stopBLEAdvertising();
        }
    };


    public void stopBLEAdvertising(){
        Log.i(TAG,"BLE Advertising Stopped!");
        bleAdvertiser.stopAdvertising(bleAdvertisingCallback);
        stopGattServer();
    }

    public void setOnBluetoothDeviceDiscoveredListener(OnBluetoothDeviceDiscoveredListener listener) {
        Log.i(TAG,"Bluetooth Device Discovery On.");
        this.listener = listener;
    }

    public BluetoothGatt connectToBLEDevice(BluetoothDevice device) {
        // Establish a GATT connection to the BLE device
        Log.i(TAG,"Trying to Connect to BLE Device : "+device.getName() );
        return device.connectGatt(context, false,gattCallback );
    }

    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            // Handle connection state changes
            Looper.prepare();
            if (newState == BluetoothProfile.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS) {
                // Device connected, start discovering services
                Log.i(TAG,"Connected To Device Successfully!! : ");
                showToast("Connected to BLE Device Successfully",Toast.LENGTH_SHORT);
                gatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Device disconnected, perform necessary actions
                Log.i(TAG,"Device Disconnected !!");
                showToast("Device Disconnected",Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // Handle service discovery completion
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Services discovered successfully, perform operations on characteristics
                Log.i(TAG,"Service Discovered Successfully!!");
                showToast("Service Discovered Successfully",Toast.LENGTH_SHORT);
                BluetoothGattService service = gatt.getService(konnectServiceUUID);
                if (service != null) {
                    // Characteristic operations
                    List<BluetoothGattCharacteristic> characteristic = service.getCharacteristics();
                    Log.i(TAG,"Count of Characteristics : "+characteristic.size());
                    for(int i=0;i<characteristic.size();i++){
                        Log.i(TAG,"Characteristic "+(i+1)+" "+characteristic.get(i).getUuid().toString());
                    }
                    if (characteristic.get(0) != null) {

                        Log.i(TAG,"Characteristic Discovered Successfully!!");
                        showToast("Characteristic Discovered Successfully",Toast.LENGTH_SHORT);
                        // Read or write operations, enable notifications, etc.
                    }
                    else{
                        Log.i(TAG,"Characteristic Not Discovered Successfully!!");
                        showToast("Characteristic Not Discovered Successfully",Toast.LENGTH_SHORT);
                    }
                }

            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            // Handle characteristic read response
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Read successful, retrieve data from characteristic.getValue()
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            // Handle characteristic write response
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Write successful
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // Handle characteristic notification/indication updates
            // This callback will be triggered when a subscribed characteristic value changes
        }

        // Other callback methods like onDescriptorRead, onDescriptorWrite, etc., can be implemented as needed
    };


    private void showToast(String message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

}
