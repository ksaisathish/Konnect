package com.manet.konnect.ui;

import static com.manet.konnect.utils.DebugUtils.getDebugSettingsMap;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.manet.konnect.R;
import com.manet.konnect.utils.DebugSettingsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DebugSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DebugSettingsFragment extends Fragment {

    private String TAG="DebugSettingsFragment";


    public DebugSettingsFragment() {
        // Required empty public constructor
    }

    public static DebugSettingsFragment newInstance(String param1, String param2) {
        DebugSettingsFragment fragment = new DebugSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG,"onCreateView Called!");

        View rootView = inflater.inflate(R.layout.fragment_debug_settings, container, false);
        ListView listView = rootView.findViewById(R.id.debugSettingsList);
        Map<String, String> debugSettingsMap=getDebugSettingsMap();
        List<String> debugSettingsList = new ArrayList<>(debugSettingsMap.keySet());
        DebugSettingsAdapter adapter = new DebugSettingsAdapter(requireContext(), debugSettingsList, debugSettingsMap);
        listView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy Called!");
        super.onDestroy();
    }
}