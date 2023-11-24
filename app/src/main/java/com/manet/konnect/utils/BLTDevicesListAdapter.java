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
import com.manet.konnect.core.BLTConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BLTDevicesListAdapter extends BaseAdapter {
    private Context context;
    private List<String> DeviceList;
    private Map<String, BluetoothDevice> bluetoothDevicesMap;
    private String TAG="PairedDevicesListAdapter";
    private BLTConnectionManager connMngr;

    public BLTDevicesListAdapter(Context context, Activity activity, Map<String, BluetoothDevice> bluetoothPairedDevicesMap) {
        this.context = context;
        this.bluetoothDevicesMap = bluetoothPairedDevicesMap;
        this.DeviceList = new ArrayList<>(bluetoothPairedDevicesMap.keySet());
        this.connMngr=new BLTConnectionManager(context,activity);
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

        bluetoothDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deviceName=="No Paired Devices."){

                }
                else{
                    //BluetoothDevice device=connMngr.getBluetoothAdapter().getRemoteDevice(bluetoothDevicesMap.get(deviceName));
                    BluetoothDevice device=bluetoothDevicesMap.get(deviceName);
                    connMngr.connectToDevice(device);
                    //Logic to Connect to the given bluetooth device.
                }
            }
        });

        return convertView;
    }
}