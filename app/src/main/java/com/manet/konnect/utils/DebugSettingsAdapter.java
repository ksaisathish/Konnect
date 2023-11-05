package com.manet.konnect.utils;

import static com.manet.konnect.utils.DebugUtils.getDebugSettingsMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.manet.konnect.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DebugSettingsAdapter extends BaseAdapter {
    private Context context;
    private List<String> debugSettingsList;
    private Map<String, String> debugSettingsMap;

    public DebugSettingsAdapter(Context context, List<String> debugSettingsList, Map<String, String> debugSettingsMap) {
        this.context = context;
        this.debugSettingsMap = getDebugSettingsMap();
        this.debugSettingsList = new ArrayList<>(debugSettingsMap.keySet());
    }

    @Override
    public int getCount() {
        return debugSettingsList.size();
    }

    @Override
    public Object getItem(int position) {
        return debugSettingsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.debug_list_item_layout, null);
        }

        Button debugButton = convertView.findViewById(R.id.debugButton);
        final String debugSetting = debugSettingsList.get(position);
        debugButton.setText(debugSetting);

        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fragmentClassName = debugSettingsMap.get(debugSetting);
                if (fragmentClassName != null) {
                    try {
                        Fragment fragment = (Fragment) Class.forName(fragmentClassName).newInstance();
                        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return convertView;
    }
}