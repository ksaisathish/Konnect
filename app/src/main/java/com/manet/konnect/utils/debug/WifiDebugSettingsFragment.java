package com.manet.konnect.utils.debug;

import android.bluetooth.BluetoothDevice;
import android.net.wifi.ScanResult;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.manet.konnect.R;
import com.manet.konnect.core.BLTConnectionManager;
import com.manet.konnect.core.WifiConnectionManager;
import com.manet.konnect.utils.BLTDevicesListAdapter;
import com.manet.konnect.utils.WifiApsListAdapter;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WifiDebugSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WifiDebugSettingsFragment extends Fragment {

    Button discoverWifiAPs;
    List<ScanResult> wifiAps;
    WifiApsListAdapter wifiApsListAdapter;
    WifiConnectionManager connMngr;

    ListView wifiAPSListView;

    private String TAG="WifiDebugSettingsFragment";
    public WifiDebugSettingsFragment() {
        // Required empty public constructor
    }


    public static WifiDebugSettingsFragment newInstance(String param1, String param2) {
        WifiDebugSettingsFragment fragment = new WifiDebugSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connMngr=new WifiConnectionManager(this.getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_wifi_debug_settings, container, false);

        discoverWifiAPs=rootView.findViewById(R.id.discoverWifiAPSDevices);
        wifiAPSListView=rootView.findViewById(R.id.wifiAPList);
        discoverWifiAPs.setOnClickListener(v->{
            Log.i(TAG,"Button Clicked");
            wifiAps=connMngr.getAvailableNetworks();
            wifiApsListAdapter=new WifiApsListAdapter(this.getContext(),wifiAps);
            wifiAPSListView.setAdapter(wifiApsListAdapter);
        });
        // Inflate the layout for this fragment
        return rootView;
    }
}