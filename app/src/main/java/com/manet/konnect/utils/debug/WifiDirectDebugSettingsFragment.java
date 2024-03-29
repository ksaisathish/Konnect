package com.manet.konnect.utils.debug;


import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.p2p.WifiP2pConfig;
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
import android.widget.Toast;

import com.manet.konnect.R;
import com.manet.konnect.core.ServerThread;
import com.manet.konnect.core.WifiConnectionManager;
import com.manet.konnect.core.WifiDirectConnectionManager;
import com.manet.konnect.utils.BLTDevicesListAdapter;
import com.manet.konnect.utils.OnWifiDirectDevicesDiscoveredListener;
import com.manet.konnect.utils.WifiApsListAdapter;
import com.manet.konnect.utils.WifiDirectDevicesListAdapter;
import com.manet.konnect.utils.WifiDirectGroupDevicesListAdapter;

import java.net.InetAddress;
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
    TextView isWifiDirectSupported, getIsWifiDirectEnabled,wifiDirectDeviceName,isWifiDirectGroupFormed,isWifiDirectGroupOwner;

    ListView wifiDirectDevicesListView,wifiDirectGroupDevicesListView;

    ServerThread sThread;

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
        sThread=new ServerThread(this.getContext());
        //sThread.run();
        //connMngr.requestDeviceInfo();
        wiFiDirectReceiver = new WiFiDirectDebugBroadcastReceiver(connMngr, this.getContext(),this);
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
        isWifiDirectGroupOwner=rootView.findViewById(R.id.isWifiDirectGroupOwner);
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
                    public void onFailure(int i) {
                        if(i==2){
                            makeWifiDirectDeviceDiscoverable.setText("Stop Wifi Direct \nDevice Discovery");
                        }
                    }
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
        wifiDirectDevicesListAdapter=new WifiDirectDevicesListAdapter(requireContext(),wifiDirectDevicesMap,sThread);
        wifiDirectDevicesListView.setAdapter(wifiDirectDevicesListAdapter);
    }

    @Override
    public void onWifiDirectGroupDevicesDiscovered(WifiP2pInfo info, List<WifiP2pDevice> peersList) {
        Log.i(TAG,"CALLED");
        HashMap<String, WifiP2pDevice> wifiDirectGroupDevicesMap=new HashMap<>();


        if(info.groupFormed){
            isWifiDirectGroupFormed.setText("isWifiDirectGroupFormed : True");
            if(info.isGroupOwner){
                isWifiDirectGroupOwner.setText("isWifiDirectGroupOwner : True");
            }
            else{
                isWifiDirectGroupOwner.setText("isWifiDirectGroupOwner : False");
            }
            for (WifiP2pDevice device:peersList) {
                if(device.deviceName.length()==0){
                    wifiDirectGroupDevicesMap.put(device.deviceAddress,device);
                }
                else{
                    wifiDirectGroupDevicesMap.put(device.deviceName,device);
                }
                Log.i(TAG,device.toString());
            }
        }else{
            isWifiDirectGroupFormed.setText("isWifiDirectGroupFormed : False");
            isWifiDirectGroupOwner.setText("isWifiDirectGroupOwner : False");

        }

        wifiDirectGroupDevicesListAdapter=new WifiDirectGroupDevicesListAdapter(requireContext(),wifiDirectGroupDevicesMap,info,sThread);
        wifiDirectGroupDevicesListView.setAdapter(wifiDirectGroupDevicesListAdapter);

    }

    @Override
    public void onPeerDevicesDiscovered(List<WifiP2pDevice> peersList) {
        HashMap<String, WifiP2pDevice> wifiDirectPeerDevicesMap=new HashMap<>();
        boolean isFound=false;
        if(peersList!=null ){
            if(peersList.size()!=0){
                isFound=true;
                for (WifiP2pDevice device:peersList) {
                wifiDirectPeerDevicesMap.put(device.deviceName,device);
                }
            }

        }
        if(!isFound){
            wifiDirectPeerDevicesMap.put("No Wifi Direct Devices Discovered.",null);
            Log.i(TAG,"No Wifi Direct Devices Discovered.");
            showToast("No Wifi Direct Devices Discovered.", Toast.LENGTH_SHORT);
        }


        wifiDirectDevicesListAdapter=new WifiDirectDevicesListAdapter(requireContext(),wifiDirectPeerDevicesMap,sThread);
        wifiDirectDevicesListView.setAdapter(wifiDirectDevicesListAdapter);
    }

    private void showToast(String message, int duration) {
        Toast toast = Toast.makeText(requireContext(), message, duration);
        toast.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sThread != null) {
            sThread.closeServer(); // Custom method to close the server thread
        }
    }

}