package com.manet.konnect.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.manet.konnect.R;
import com.manet.konnect.core.NearbyConnectionsManager;
import com.manet.konnect.core.Packet;
import com.manet.konnect.core.RoutingTableEntry;
import com.manet.konnect.utils.debug.NearbyChatDebugActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class AllConnectedNearbyDevicesListAdapter extends BaseAdapter {
    private Context context;
    private NearbyConnectionsManager nearbyConnectionsManager;
    private Map<String, RoutingTableEntry> table;

    private final String TAG = "AllConnectedNearbyDevicesListAdapter";

    public AllConnectedNearbyDevicesListAdapter(Context context, NearbyConnectionsManager nearbyConnectionsManager) {
        this.context = context;
        this.nearbyConnectionsManager = nearbyConnectionsManager;
        this.table = nearbyConnectionsManager.getRoutingManager().getRoutingTable().getTable();
    }

    @Override
    public int getCount() {
        return table.size();
    }

    @Override
    public Object getItem(int position) {
        // Convert Map values to a List for indexing
        List<RoutingTableEntry> entries = new ArrayList<>(table.values());
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        // Use position directly as the item ID
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.debug_list_item_layout, null);
        }

        Button nearbyDeviceButton = convertView.findViewById(R.id.debugButton);
        TextView unreadMessageCount = convertView.findViewById(R.id.unreadMessageCount);

        // Convert Map values to a List for indexing
        List<RoutingTableEntry> entries = new ArrayList<>(table.values());

        final RoutingTableEntry entry = entries.get(position);
        final String endpointId = entry.getEndpointId();

        nearbyDeviceButton.setText(entry.getUsername() + "(" + endpointId + ")");

        if(entry.getUnreadMessageCount()==1){
            unreadMessageCount.setText(entry.getUnreadMessageCount()+" Unread Message");
            unreadMessageCount.setVisibility(View.VISIBLE);
        }
        else if(entry.getUnreadMessageCount()>1){
            unreadMessageCount.setText(entry.getUnreadMessageCount()+" Unread Messages");
            unreadMessageCount.setVisibility(View.VISIBLE);
        }
        else{
            unreadMessageCount.setText("");
            unreadMessageCount.setVisibility(View.GONE);
        }

        nearbyDeviceButton.setOnClickListener(v -> {
            if (endpointId.equals("No Nearby Devices Connected.")) {
                // Handle case when no nearby devices are connected
            } else {
                Log.i(TAG, "Trying to chat with " +entry.getUsername()+"("+ endpointId+")");

                Intent intent = new Intent(context, NearbyChatDebugActivity.class);
                intent.putExtra("username", entry.getUsername());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    // Function to update the table with current routing table entries
    public void refreshList() {
        notifyDataSetChanged();
    }
}
