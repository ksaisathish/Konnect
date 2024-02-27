package com.manet.konnect.utils.debug;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.manet.konnect.R;
import com.manet.konnect.core.NearbyConnectionsManager;
import com.manet.konnect.utils.ConnectedNearbyConnectionsListAdapter;
import com.manet.konnect.utils.DiscoveredNearbyConnectionsListAdapter;
import com.manet.konnect.utils.OnNearbyConnectionDevicesDiscoveredListener;
import com.manet.konnect.utils.WifiDirectDevicesListAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NearbyConnectionsDebugSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyConnectionsDebugSettingsFragment extends Fragment implements OnNearbyConnectionDevicesDiscoveredListener {

    private String TAG="NearbyConnectionsDebugSettingsFragment";
    private TextView localUserName;
    private ListView discoveredDevicesListView,connectedDevicesListView;
    private Button discoverDevicesButton,startAdvertisingButton;

    private NearbyConnectionsManager nearbyConnectionsManager;
    private ConnectedNearbyConnectionsListAdapter connectedNearbyConnectionsListAdapter;
    DiscoveredNearbyConnectionsListAdapter discoveredNearbyConnectionsListAdapter;


    public NearbyConnectionsDebugSettingsFragment() {
        // Required empty public constructor
    }


    public static NearbyConnectionsDebugSettingsFragment newInstance(String param1, String param2) {
        NearbyConnectionsDebugSettingsFragment fragment = new NearbyConnectionsDebugSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_nearby_connections_debug_settings, container, false);

        nearbyConnectionsManager = new NearbyConnectionsManager(requireContext());
        localUserName = rootView.findViewById(R.id.localUserName);
        discoveredDevicesListView = rootView.findViewById(R.id.nearbyConnectionsDiscoveredDevicesList);
        connectedDevicesListView = rootView.findViewById(R.id.nearbyConnectionsConnectedDevicesList);
        discoverDevicesButton = rootView.findViewById(R.id.discoverNearbyConnectionsDevices);
        startAdvertisingButton = rootView.findViewById(R.id.startNearbyConnectionsAdvertising);

        discoverDevicesButton.setText("Discover Nearby \nConnection Devices");
        startAdvertisingButton.setText("Start Nearby Connections \nAdvertising");


//        ArrayAdapter<String> discoveredDevicesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
//        discoveredDevicesListView.setAdapter(discoveredDevicesAdapter);
//
//        ArrayAdapter<String> connectedDevicesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
//        connectedDevicesListView.setAdapter(connectedDevicesAdapter);


        localUserName.setText("Local User Name: " + nearbyConnectionsManager.getLocalUserName());
        nearbyConnectionsManager.setNearbyConnectionDevicesDiscoveredListener(this);

        discoverDevicesButton.setOnClickListener(v -> {
            if(discoverDevicesButton.getText()=="Discover Nearby \nConnection Devices"){
                nearbyConnectionsManager.startDiscovery();
                discoverDevicesButton.setText("Stop Nearby Connection \nDiscovery!");
            }
            else{
                nearbyConnectionsManager.stopDiscovery();
                discoverDevicesButton.setText("Discover Nearby \nConnection Devices");
            }
        });

        startAdvertisingButton.setOnClickListener(v -> {
            if(startAdvertisingButton.getText()=="Start Nearby Connections \nAdvertising"){
                nearbyConnectionsManager.startAdvertising();
                startAdvertisingButton.setText("Stop Nearby Connection \nAdvertising!");
            }
            else{
                nearbyConnectionsManager.stopAdvertising();
                startAdvertisingButton.setText("Start Nearby Connections \nAdvertising");

            }
        });


        return rootView;
    }

    @Override
    public void onNearbyConnectionDevicesDiscovered(List<DiscoveredEndpointInfo> discoveredEndpoints) {
        Log.i(TAG,"onNearbyConnectionDevicesDiscovered");
        discoveredNearbyConnectionsListAdapter=new DiscoveredNearbyConnectionsListAdapter(requireContext(),discoveredEndpoints,nearbyConnectionsManager);
        discoveredDevicesListView.setAdapter(discoveredNearbyConnectionsListAdapter);
    }

    @Override
    public void onNearbyConnectionDevicesDevicesConnected(List<String> connectedDevices) {
        Log.i(TAG,"onNearbyConnectionDevicesDevicesConnected");
        connectedNearbyConnectionsListAdapter=new ConnectedNearbyConnectionsListAdapter(requireContext(),connectedDevices,nearbyConnectionsManager);
        connectedDevicesListView.setAdapter(connectedNearbyConnectionsListAdapter);
    }


}