package com.manet.konnect.utils.debug;


import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.manet.konnect.R;
import com.manet.konnect.core.WifiConnectionManager;
import com.manet.konnect.core.WifiDirectConnectionManager;
import com.manet.konnect.utils.BLTDevicesListAdapter;
import com.manet.konnect.utils.OnWifiDirectDevicesDiscoveredListener;
import com.manet.konnect.utils.WifiApsListAdapter;
import com.manet.konnect.utils.WifiDirectDevicesListAdapter;
import com.manet.konnect.utils.WifiDirectGroupDevicesListAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WifiDirectDebugSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WifiDirectDebugSettingsFragment extends Fragment implements WifiDirectConnectionManager.InitializationListener, OnWifiDirectDevicesDiscoveredListener {



    Button discoverWifiDirectDevices, makeWifiDirectDeviceDiscoverable;
    List<ScanResult> wifiAps;
    Map<String, WifiP2pDevice> discoveredDeviceMap;
    WifiDirectDevicesListAdapter wifiDirectDevicesListAdapter;
    WifiDirectGroupDevicesListAdapter wifiDirectGroupDevicesListAdapter;
    WifiDirectConnectionManager connMngr;
    TextView isWifiDirectSupported, getIsWifiDirectEnabled,wifiDirectDeviceName,isWifiDirectGroupFormed;

    ListView wifiDirectDevicesListView,wifiDirectGroupDevicesListView;

    private String TAG="WifiDirectDebugSettingsFragment";

    private final IntentFilter intentFilter = new IntentFilter();
    private WiFiDirectDebugBroadcastReceiver wiFiDirectReceiver;


    public WifiDirectDebugSettingsFragment() {
        // Required empty public constructor
    }


    public static WifiDirectDebugSettingsFragment newInstance(String param1, String param2) {
        WifiDirectDebugSettingsFragment fragment = new WifiDirectDebugSettingsFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    // Implement the InitializationListener callback method
    @Override
    public void onConnectionManagerInitialized() {
        // WifiDirectConnectionManager is fully initialized here
        // You can use it safely in your fragment now;
        wifiDirectDeviceName.append(connMngr.getWifiP2pDeviceName());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connMngr=new WifiDirectConnectionManager(this.getContext(),this);
        wiFiDirectReceiver = new WiFiDirectDebugBroadcastReceiver(connMngr.getWifiP2pManager(), connMngr.getChannel(), this.getContext(),this);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register the BroadcastReceiver
        requireActivity().registerReceiver(wiFiDirectReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the BroadcastReceiver
        requireActivity().unregisterReceiver(wiFiDirectReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_wifi_direct_debug_settings, container, false);

        discoverWifiDirectDevices=rootView.findViewById(R.id.discoverWifiDirectDevices);
        wifiDirectDevicesListView=rootView.findViewById(R.id.wifiDirectDevicesList);
        isWifiDirectSupported=rootView.findViewById(R.id.isWifiDirectSupportedText);
        getIsWifiDirectEnabled=rootView.findViewById(R.id.isWifiDirectEnabledText);
        wifiDirectDeviceName=rootView.findViewById(R.id.wifiDirectDeviceName);

        isWifiDirectGroupFormed=rootView.findViewById(R.id.isWifiDirectGroupFormed);
        wifiDirectGroupDevicesListView=rootView.findViewById(R.id.wifiDirectGroupDevicesList);

        makeWifiDirectDeviceDiscoverable=rootView.findViewById(R.id.makeWifiDirectDeviceDiscoverable);
        makeWifiDirectDeviceDiscoverable.setText("Make Wi-FI Direct \nDevice Discoverable");

        isWifiDirectSupported.append(String.valueOf(connMngr.isWifiDirectSupported()));
        getIsWifiDirectEnabled.append(String.valueOf(connMngr.isWifiDirectEnabled()));


        discoverWifiDirectDevices.setOnClickListener(v->{
            connMngr.discoverPeers();
            connMngr.setOnWifiDirectDevicesDiscoveredListener(this);
        });

        makeWifiDirectDeviceDiscoverable.setOnClickListener(v -> {
            if(makeWifiDirectDeviceDiscoverable.getText()=="Make Wi-FI Direct \nDevice Discoverable"){
                connMngr.makeDeviceDiscoverable(new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        makeWifiDirectDeviceDiscoverable.setText("Stop Wifi Direct \nDevice Discovery");
                    }

                    @Override
                    public void onFailure(int i) {}
                });
            }
            else{

                connMngr.stopDeviceDiscovery(new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        makeWifiDirectDeviceDiscoverable.setText("Make Wi-FI Direct \nDevice Discoverable");
                    }

                    @Override
                    public void onFailure(int i) {}
                });
            }
        });

        return rootView;
    }

    @Override
    public void onWifiDirectDevicesDiscovered(HashMap<String, WifiP2pDevice> wifiDirectDevicesMap) {
        Log.i(TAG,"OnWifiDirectDevicesDiscovered");
        wifiDirectDevicesListAdapter=new WifiDirectDevicesListAdapter(requireContext(),wifiDirectDevicesMap);
        wifiDirectDevicesListView.setAdapter(wifiDirectDevicesListAdapter);
    }

    @Override
    public void onWifiDirectGroupDevicesDiscovered(WifiP2pInfo info, List<WifiP2pDevice> peersList) {
        Log.i(TAG,"CALLED");
        HashMap<String, WifiP2pDevice> wifiDirectGroupDevicesMap=new HashMap<>();
        if(info.groupFormed){
            isWifiDirectGroupFormed.setText("isWifiDirectGroupFormed : True");
            for (WifiP2pDevice device:peersList) {
                wifiDirectGroupDevicesMap.put(device.deviceName,device);
            }
        }else{
            isWifiDirectGroupFormed.setText("isWifiDirectGroupFormed : False");
        }


        wifiDirectGroupDevicesListAdapter=new WifiDirectGroupDevicesListAdapter(requireContext(),wifiDirectGroupDevicesMap);
        wifiDirectGroupDevicesListView.setAdapter(wifiDirectGroupDevicesListAdapter);


    }

}