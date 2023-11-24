package com.manet.konnect.utils.debug;

import android.bluetooth.BluetoothDevice;
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
import com.manet.konnect.core.BLEConnectionManager;
import com.manet.konnect.utils.BLEDevicesListAdapter;
import com.manet.konnect.utils.OnBluetoothDeviceDiscoveredListener;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BluetoothLEDebugSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BluetoothLEDebugSettingsFragment extends Fragment implements OnBluetoothDeviceDiscoveredListener {

    TextView deviceName;
    Button advertiseBLEDevice;
    Button discoverBluetoothLEDevices;
    Button discoverKonnectBLEDevices;

    Map<String, BluetoothDevice> bluetoothLEPeerDevicesMap;
    BLEDevicesListAdapter peerDevicesAdapter;
    ListView peerDevicesListView;

    BLEConnectionManager connMngr;
    private String TAG="BluetoothLEDebugSettingsFragment";

    public BluetoothLEDebugSettingsFragment() {
        // Required empty public constructor
    }


    public static BluetoothLEDebugSettingsFragment newInstance(String param1, String param2) {
        BluetoothLEDebugSettingsFragment fragment = new BluetoothLEDebugSettingsFragment();
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
        View rootView=inflater.inflate(R.layout.fragment_bluetooth_l_e_debug_settings, container, false);
        connMngr=new BLEConnectionManager(this.getContext(),this.getActivity());
        deviceName=rootView.findViewById(R.id.bluetoothLEDeviceNameText);
        deviceName.append(connMngr.getBluetoothDeviceName());



        advertiseBLEDevice =rootView.findViewById(R.id.advertiseBLEDevice);
        advertiseBLEDevice.setText("Start\nBLE Advertising");
        discoverBluetoothLEDevices =rootView.findViewById(R.id.discoverBluetoothLEDevices);
        discoverBluetoothLEDevices.setText("Discover\nAll Peers");
        discoverKonnectBLEDevices =rootView.findViewById(R.id.discoverKonnectBluetoothLEDevices);
        discoverKonnectBLEDevices.setText("Discover\nKonnect Peers");

        peerDevicesListView = rootView.findViewById(R.id.bluetoothLEPeerDevicesList);
        bluetoothLEPeerDevicesMap =connMngr.getBLEPeerDevicesMap();
        peerDevicesAdapter =new BLEDevicesListAdapter(requireContext(),getActivity(), bluetoothLEPeerDevicesMap,connMngr);
        peerDevicesListView.setAdapter(peerDevicesAdapter);

        discoverBluetoothLEDevices.setOnClickListener(v->{
            if(discoverBluetoothLEDevices.getText()=="Discover\nAll Peers"){
                discoverKonnectBLEDevices.setEnabled(false);
                bluetoothLEPeerDevicesMap.clear();
                peerDevicesAdapter = new BLEDevicesListAdapter(requireContext(), getActivity(), bluetoothLEPeerDevicesMap,connMngr);
                peerDevicesListView.setAdapter(peerDevicesAdapter);
                discoverBluetoothLEDevices.setText("Stop Discovery");
                connMngr.discoverBLEDevices();
                connMngr.setOnBluetoothDeviceDiscoveredListener(this);
                connMngr.clearBluetoothPeerDeviceMap();
            }
            else{
                discoverBluetoothLEDevices.setText("Discover\nAll Peers");
                connMngr.stopDiscovery();
                discoverKonnectBLEDevices.setEnabled(true);

            }

        });

        discoverKonnectBLEDevices.setOnClickListener(v->{
            if(discoverKonnectBLEDevices.getText()=="Discover\nKonnect Peers"){
                discoverBluetoothLEDevices.setEnabled(false);
                bluetoothLEPeerDevicesMap.clear();
                peerDevicesAdapter = new BLEDevicesListAdapter(requireContext(), getActivity(), bluetoothLEPeerDevicesMap,connMngr);
                peerDevicesListView.setAdapter(peerDevicesAdapter);
                discoverKonnectBLEDevices.setText("Stop Discovery");
                connMngr.discoverKonnectBLEDevices();
                connMngr.setOnBluetoothDeviceDiscoveredListener(this);
                connMngr.clearBluetoothPeerDeviceMap();
            }
            else{
                discoverKonnectBLEDevices.setText("Discover\nKonnect Peers");
                connMngr.stopDiscovery();
                discoverBluetoothLEDevices.setEnabled(true);
            }

        });

        advertiseBLEDevice.setOnClickListener(v -> {
            if(advertiseBLEDevice.getText()=="Start\nBLE Advertising"){
                if(connMngr.startBLEAdvertising()==0){
                    advertiseBLEDevice.setText("Stop\nBLE Advertising");
                }
            }
            else{
                advertiseBLEDevice.setText("Start\nBLE Advertising");
                connMngr.stopBLEAdvertising();
            }
        });

        return rootView;
    }

    @Override
    public void onBluetoothDeviceDiscovered(String deviceName, BluetoothDevice device) {
        Log.i(TAG,"OnDeviceDiscovered");
        // Update the UI with the discovered device
        bluetoothLEPeerDevicesMap.put(deviceName, device);
        Log.i(TAG, String.valueOf(bluetoothLEPeerDevicesMap.size()));
        peerDevicesAdapter = new BLEDevicesListAdapter(requireContext(), getActivity(), bluetoothLEPeerDevicesMap,connMngr);
        peerDevicesListView.setAdapter(peerDevicesAdapter);
    }
}