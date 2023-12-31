package com.manet.konnect.ui;

import static com.manet.konnect.utils.DebugUtils.getDebugSettingsMap;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DebugSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DebugSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DebugSettingsFragment newInstance(String param1, String param2) {
        DebugSettingsFragment fragment = new DebugSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_debug_settings, container, false);
        ListView listView = rootView.findViewById(R.id.debugSettingsList);
        Map<String, String> debugSettingsMap=getDebugSettingsMap();
        List<String> debugSettingsList = new ArrayList<>(debugSettingsMap.keySet());
        DebugSettingsAdapter adapter = new DebugSettingsAdapter(requireContext(), debugSettingsList, debugSettingsMap);
        listView.setAdapter(adapter);
        return rootView;
    }
}