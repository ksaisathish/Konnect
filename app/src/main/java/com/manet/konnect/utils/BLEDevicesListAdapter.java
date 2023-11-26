package com.manet.konnect.utils;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.manet.konnect.R;
import com.manet.konnect.core.BLEConnectionManager;
import com.manet.konnect.core.BLTConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BLEDevicesListAdapter extends BaseAdapter {
    private Context context;
    private List<String> DeviceList;
    private Map<String, BluetoothDevice> bluetoothDevicesMap;
    private String TAG="BLEDevicesListAdapter";
    private BLEConnectionManager connMngr;

    public BLEDevicesListAdapter(Context context, Activity activity, Map<String, BluetoothDevice> bluetoothPairedDevicesMap,BLEConnectionManager connMngr) {
        this.context = context;
        this.bluetoothDevicesMap = bluetoothPairedDevicesMap;
        this.DeviceList = new ArrayList<>(bluetoothPairedDevicesMap.keySet());
        this.connMngr=connMngr;
    }

    @Override
    public int getCount() {
        return DeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return DeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.bluetooth_device_list_item_layout, null);
        }

        Button bluetoothDeviceButton = convertView.findViewById(R.id.bluetoothDeviceButton);
        final String deviceName = DeviceList.get(position);
        bluetoothDeviceButton.setText(deviceName);

        bluetoothDeviceButton.setOnClickListener(v -> {
            if(deviceName=="No Paired Devices."){

            }
            else{
                //BluetoothDevice device=connMngr.getBluetoothAdapter().getRemoteDevice(bluetoothDevicesMap.get(deviceName));
                connMngr.connectToBLEDevice(bluetoothDevicesMap.get(deviceName));
                //Logic to Connect to the given bluetooth device.
            }
        });

        return convertView;
    }
}