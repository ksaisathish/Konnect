package com.manet.konnect.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.manet.konnect.R;
import com.manet.konnect.core.NearbyConnectionsManager;

import java.util.List;

public class DiscoveredNearbyConnectionsListAdapter  extends BaseAdapter {
    private Context context;
    List<DiscoveredEndpointInfo> discoveredEndpoints;
    private final String TAG="DiscoveredNearbyConnectionsListAdapter";
    private NearbyConnectionsManager nearbyConnectionsManager;

    public DiscoveredNearbyConnectionsListAdapter(Context context,  List<DiscoveredEndpointInfo> discoveredEndpoints,NearbyConnectionsManager nearbyConnectionsManager) {
        this.context = context;
        this.nearbyConnectionsManager = nearbyConnectionsManager;
        this.discoveredEndpoints=discoveredEndpoints;
    }

    @Override
    public int getCount() {
        return discoveredEndpoints.size();
    }

    @Override
    public Object getItem(int position) {
        return discoveredEndpoints.get(position);
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

        Button nearbyDeviceButton = convertView.findViewById(R.id.debugButton);
        final String endpointName = discoveredEndpoints.get(position).getEndpointName();

        nearbyDeviceButton.setText(endpointName+"("+nearbyConnectionsManager.getEndpointId(endpointName)+")");

        nearbyDeviceButton.setOnClickListener(v -> {
            if(endpointName=="No Nearby Devices Discovered."){
            }
            else{
                Log.i(TAG,"Trying to connect to "+endpointName);
                nearbyConnectionsManager.initiateConnection(endpointName);
            }
        });
        return convertView;
    }
}
