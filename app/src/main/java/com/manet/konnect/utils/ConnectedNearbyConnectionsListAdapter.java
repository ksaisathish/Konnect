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
import com.manet.konnect.core.Packet;

import java.util.ArrayList;
import java.util.List;

public class ConnectedNearbyConnectionsListAdapter extends BaseAdapter {
    private Context context;
    List<String> connectedDevices;
    private final String TAG="DiscoveredNearbyConnectionsListAdapter";
    private NearbyConnectionsManager nearbyConnectionsManager;

    public ConnectedNearbyConnectionsListAdapter(Context context,  List<String> connectedDevices,NearbyConnectionsManager nearbyConnectionsManager) {
        this.context = context;
        this.nearbyConnectionsManager = nearbyConnectionsManager;
        this.connectedDevices=connectedDevices;
    }

    @Override
    public int getCount() {
        return connectedDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return connectedDevices.get(position);
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
        final String endpointId = connectedDevices.get(position);

        final String endPointName=nearbyConnectionsManager.getUsernameByEndpointId(endpointId);


        nearbyDeviceButton.setText(endPointName+"("+endpointId+")");

        nearbyDeviceButton.setOnClickListener(v -> {
            if(endpointId=="No Nearby Devices Connected."){
            }
            else{
                Log.i(TAG,"Trying to chat with "+endpointId);

                String srcUsername= nearbyConnectionsManager.getLocalUserName();
                String dstUsername=nearbyConnectionsManager.getUsernameByEndpointId(endpointId);

                Packet textPacket=new Packet(srcUsername,dstUsername, 0, Packet.PACKET_TYPE_MESSAGE, Packet.DATA_TYPE_TEXT,"Hello!".getBytes());

                nearbyConnectionsManager.sendPayload(endpointId,textPacket);
                //nearbyConnectionsManager.initiateConnection(endpointId);
            }
        });
        return convertView;
    }
}
