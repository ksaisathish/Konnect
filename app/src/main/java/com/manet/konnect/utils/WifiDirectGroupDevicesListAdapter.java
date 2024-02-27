package com.manet.konnect.utils;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.manet.konnect.R;
import com.manet.konnect.core.ServerThread;
import com.manet.konnect.core.WifiDirectConnectionManager;
import com.manet.konnect.utils.debug.ChatDebugActivity;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WifiDirectGroupDevicesListAdapter extends BaseAdapter {
    private Context context;
    Map<String, WifiP2pDevice> wifiDirectDeviceMap;

    private List<String> DeviceList;
    private String TAG="WifiDirectDevicesListAdapter";
    private WifiDirectConnectionManager connMngr;
    private WifiP2pInfo info;

    private ServerThread sThread;
    public WifiDirectGroupDevicesListAdapter(Context context, Map<String, WifiP2pDevice> wifiDirectDeviceMap, WifiP2pInfo info,ServerThread sThread) {
        this.context = context;
        this.sThread=sThread;
        this.connMngr= new WifiDirectConnectionManager(context,null);
        this.DeviceList = new ArrayList<>(wifiDirectDeviceMap.keySet());
        this.wifiDirectDeviceMap =wifiDirectDeviceMap;
        this.info=info;
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
                WifiP2pDevice device= wifiDirectDeviceMap.get(deviceName);
                Log.i(TAG,"Trying to chat with "+deviceName);
                Intent chatIntent = new Intent(context, ChatDebugActivity.class);
                chatIntent.putExtra("isGroupOwner", info.isGroupOwner); // Indicate if this device is a group owner
                chatIntent.putExtra("info",info);
                chatIntent.putExtra("deviceName",deviceName);
                //chatIntent.putExtra("serverThread", (Serializable) sThread);

                context.startActivity(chatIntent);

            }
        });
        return convertView;
    }
}
