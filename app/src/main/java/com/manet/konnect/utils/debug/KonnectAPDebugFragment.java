package com.manet.konnect.utils.debug;

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
import com.manet.konnect.core.WifiConnectionManager;
import com.manet.konnect.utils.WifiApsListAdapter;

import java.util.List;


public class KonnectAPDebugFragment extends Fragment {

    Button discoverKonnectAPs;
    List<ScanResult> konnectAps;
    WifiApsListAdapter wifiApsListAdapter;
    WifiConnectionManager connMngr;

    ListView konnectAPSListView;

    private String TAG="KonnectAPDebugSettingsFragment";

    public KonnectAPDebugFragment() {
        // Required empty public constructor
    }


    public static KonnectAPDebugFragment newInstance() {
        return new KonnectAPDebugFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        connMngr=new WifiConnectionManager(this.getContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_konnect_a_p_debug, container, false);

        discoverKonnectAPs =rootView.findViewById(R.id.discoverKonnectAPSDevices);
        konnectAPSListView=rootView.findViewById(R.id.konnectAPList);
        discoverKonnectAPs.setOnClickListener(v->{
            Log.i(TAG,"Button Clicked");

            konnectAps = connMngr.getKonnectNetworks();
            wifiApsListAdapter=new WifiApsListAdapter(this.getContext(), konnectAps);
            konnectAPSListView.setAdapter(wifiApsListAdapter);
        });
        // Inflate the layout for this fragment
        return rootView;
    
    }
}