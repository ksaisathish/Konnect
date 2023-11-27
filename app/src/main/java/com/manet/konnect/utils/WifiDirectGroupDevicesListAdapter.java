package com.manet.konnect.utils;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.manet.konnect.R;
import com.manet.konnect.core.WifiDirectConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WifiDirectGroupDevicesListAdapter extends BaseAdapter {
    private Context context;
    Map<String, WifiP2pDevice> wifiDirectDeviceMap;

    private List<String> DeviceList;
    private String TAG="WifiDirectDevicesListAdapter";
    private WifiDirectConnectionManager connMngr;

    public WifiDirectGroupDevicesListAdapter(Context context, Map<String, WifiP2pDevice> wifiDirectDeviceMap) {
        this.context = context;
        this.connMngr= new WifiDirectConnectionManager(context,null);
        this.DeviceList = new ArrayList<>(wifiDirectDeviceMap.keySet());
        this.wifiDirectDeviceMap =wifiDirectDeviceMap;
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
            convertView = inflater.inflate(R.layout.debug_list_item_layout, null);
        }

        Button wifiDirectDeviceButton = convertView.findViewById(R.id.debugButton);
        final String deviceName = DeviceList.get(position);
        wifiDirectDeviceButton.setText(deviceName);

        wifiDirectDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deviceName=="No Wifi Direct Devices Discovered."){

                }
                else{
                    WifiP2pDevice device= wifiDirectDeviceMap.get(deviceName);
                    Log.i(TAG,"Trying to connect to "+device.deviceAddress);
                    //connMngr.connectToDevice(device,null);
                }
            }
        });
        return convertView;
    }
}
