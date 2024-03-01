package com.manet.konnect.utils;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.manet.konnect.R;
import com.manet.konnect.core.BLTConnectionManager;
import com.manet.konnect.core.WifiConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WifiApsListAdapter extends BaseAdapter {
    private Context context;
    List<ScanResult> wifiAps;
    private String TAG="WifiApsListAdapter";
    private WifiConnectionManager connMngr;

    public WifiApsListAdapter(Context context, List<ScanResult> wifiAps) {
        this.context = context;
        this.connMngr=new WifiConnectionManager(context);
        this.wifiAps=wifiAps;
    }

    @Override
    public int getCount() {
        return wifiAps.size();
    }

    @Override
    public Object getItem(int position) {
        return wifiAps.get(position);
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

        Button wifiAPButton = convertView.findViewById(R.id.debugButton);
        final ScanResult wifiAP = wifiAps.get(position);
        wifiAPButton.setText(wifiAP.SSID);

        wifiAPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wifiAP.SSID.startsWith("Konnect_")){
                    Log.i(TAG,"Trying to connect to "+wifiAP.SSID);
                    connMngr.connectToWifi(wifiAP.SSID,"password");
                }
            }
        });

        return convertView;
    }
}