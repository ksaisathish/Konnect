package com.manet.konnect.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.manet.konnect.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DevicesListAdapter extends BaseAdapter {
    private Context context;
    private List<String> pairedDeviceList;
    private Map<String, String> bluetoothPairedDevicesMap;
    private String TAG="PairedDevicesListAdapter";

    public DevicesListAdapter(Context context, Map<String, String> bluetoothPairedDevicesMap) {
        this.context = context;
        this.bluetoothPairedDevicesMap= bluetoothPairedDevicesMap;
        this.pairedDeviceList= new ArrayList<>(bluetoothPairedDevicesMap.keySet());
    }

    @Override
    public int getCount() {
        return pairedDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return pairedDeviceList.get(position);
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
        final String deviceName = pairedDeviceList.get(position);
        bluetoothDeviceButton.setText(deviceName);

        bluetoothDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deviceName=="No Paired Devices."){

                }
                else{

                    //Logic to Connect to the given bluetooth device.
                }
            }
        });

        return convertView;
    }
}