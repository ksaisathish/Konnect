package com.manet.konnect.utils.debug;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.manet.konnect.R;
import com.manet.konnect.core.ConnectionManager;
import com.manet.konnect.utils.DevicesListAdapter;
import com.manet.konnect.utils.OnBluetoothDeviceDiscoveredListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BluetoothDebugSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BluetoothDebugSettingsFragment extends Fragment implements OnBluetoothDeviceDiscoveredListener {
    TextView deviceName;
    Button makeBluetoothDeviceDiscoverable;
    Button discoverBluetoothDevices;

    Map<String, String> bluetoothPeerDevicesMap;
    DevicesListAdapter peerDevicesAdapter;
    ListView peerDevicesListView;

    private String TAG="BluetoothDebugSettingsFragment";
    public BluetoothDebugSettingsFragment() {
        // Required empty public constructor
    }


    public static BluetoothDebugSettingsFragment newInstance(String param1, String param2) {
        BluetoothDebugSettingsFragment fragment = new BluetoothDebugSettingsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_bluetooth_debug_settings, container, false);
        ConnectionManager connMngr=new ConnectionManager(this.getContext(),this.getActivity());
        deviceName=rootView.findViewById(R.id.bluetoothDeviceNameText);
        deviceName.append(connMngr.getBluetoothDeviceName());

        ListView pairedDevicesListView = rootView.findViewById(R.id.bluetoothPairedDevicesList);
        Map<String, String> bluetoothPairedDevicesMap=connMngr.getBluetoothPairedDevicesMap();
        List<String> bluetoothPairedDevicesList = new ArrayList<>(bluetoothPairedDevicesMap.keySet());
        DevicesListAdapter pairedDevicesAdapter =new DevicesListAdapter(requireContext(),bluetoothPairedDevicesMap);
        pairedDevicesListView.setAdapter(pairedDevicesAdapter);

        makeBluetoothDeviceDiscoverable=rootView.findViewById(R.id.makeBluetoothDeviceDiscoverable);

        makeBluetoothDeviceDiscoverable.setOnClickListener(v -> {
            connMngr.makeBluetoothDeviceDiscoverable();
        });

        discoverBluetoothDevices=rootView.findViewById(R.id.discoverBluetoothDevices);
        discoverBluetoothDevices.setText("Discover Peers");

        peerDevicesListView = rootView.findViewById(R.id.bluetoothPeerDevicesList);
        bluetoothPeerDevicesMap=connMngr.getBluetoothPeerDevicesMap();
        peerDevicesAdapter =new DevicesListAdapter(requireContext(),bluetoothPeerDevicesMap);
        peerDevicesListView.setAdapter(peerDevicesAdapter);

        discoverBluetoothDevices.setOnClickListener(v->{
            if(discoverBluetoothDevices.getText()=="Discover Peers"){
                bluetoothPeerDevicesMap.clear();
                peerDevicesAdapter = new DevicesListAdapter(requireContext(), bluetoothPeerDevicesMap);
                peerDevicesListView.setAdapter(peerDevicesAdapter);
                discoverBluetoothDevices.setText("Stop Discovery");
                connMngr.startBluetoothDiscovery();
                connMngr.setOnBluetoothDeviceDiscoveredListener(this);
                connMngr.clearBluetoothPeerDeviceMap();
            }
            else{
                discoverBluetoothDevices.setText("Discover Peers");
                connMngr.stopBluetoothDiscovery();
            }

        });

        return rootView;
    }

    @Override
    public void onBluetoothDeviceDiscovered(String deviceName, String deviceAddress) {
        Log.i(TAG,"OnDeviceDiscovered");

        // Update the UI with the discovered device
        bluetoothPeerDevicesMap.put(deviceName, deviceAddress);
        Log.i(TAG, String.valueOf(bluetoothPeerDevicesMap.size()));
        peerDevicesAdapter = new DevicesListAdapter(requireContext(), bluetoothPeerDevicesMap);
        peerDevicesListView.setAdapter(peerDevicesAdapter);

    }
}