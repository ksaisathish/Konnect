    package com.manet.konnect.utils.debug;
    import android.annotation.SuppressLint;
    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.net.wifi.p2p.WifiP2pDevice;
    import android.net.wifi.p2p.WifiP2pGroup;
    import android.net.wifi.p2p.WifiP2pInfo;
    import android.net.wifi.p2p.WifiP2pManager;
    import android.util.Log;

    import com.manet.konnect.core.WifiDirectConnectionManager;
    import com.manet.konnect.utils.OnWifiDirectDevicesDiscoveredListener;

    import java.util.ArrayList;
    import java.util.Collection;
    import java.util.List;

    public class WiFiDirectDebugBroadcastReceiver extends BroadcastReceiver {
        private WifiP2pManager wifiP2pManager;
        private WifiP2pManager.Channel channel;
        private Context context;
        private final String TAG = "WiFiDirectDebugBroadcastReceiver";

        private List<WifiP2pDevice> peersList;
        private OnWifiDirectDevicesDiscoveredListener listener;
        WifiDirectConnectionManager connMngr;

        public WiFiDirectDebugBroadcastReceiver(WifiDirectConnectionManager connMngr,
                                                Context context, OnWifiDirectDevicesDiscoveredListener listener) {
            super();
            this.connMngr=connMngr;
            this.wifiP2pManager = connMngr.getWifiP2pManager();
            this.channel = connMngr.getChannel();
            this.context = context;
            this.listener=listener;

            wifiP2pManager.requestConnectionInfo(channel, connectionInfoListener);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

                // Check if Wi-Fi P2P is supported/enabled
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Log.d(TAG, "Wi-Fi Direct is enabled");
                    // Perform actions like initiating a connection, discovery, etc.
                } else {
                    Log.d(TAG, "Wi-Fi Direct is not enabled");
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Peer list has changed
                Log.d(TAG, "Wifi P2P Peers Changed Action");

                // Update available peers
                if (wifiP2pManager != null) {
                    wifiP2pManager.requestPeers(channel, peerListListener);
                }
                wifiP2pManager.requestConnectionInfo(channel, connectionInfoListener);

            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

                Log.d(TAG, "Wifi P2P Peers Connection Action");


                if (wifiP2pManager != null) {
                    wifiP2pManager.requestConnectionInfo(channel, connectionInfoListener);
                }


                if (wifiP2pManager == null) {
                    return;
                }


                // Get current connection status
//                wifiP2pManager.requestConnectionInfo(channel, info -> {
//                    Log.d(TAG, "Connection Info: " + info.toString());
//
//                    if (info != null && info.groupFormed && info.isGroupOwner && false) {
//                        // Connected to a group as group owner, start chat activity
//                        Intent chatIntent = new Intent(context, ChatDebugActivity.class);
//                        chatIntent.putExtra("isGroupOwner", true); // Indicate if this device is a group owner
//                        chatIntent.putExtra("info",info);
//                        context.startActivity(chatIntent);
//                    } else if (info != null && info.groupFormed) {
//                        // Connected to a group as client, start chat activity
//                        Intent chatIntent = new Intent(context, ChatDebugActivity.class);
//                        chatIntent.putExtra("isGroupOwner", false); // Indicate if this device is a group client
//                        chatIntent.putExtra("info",info);
//                        context.startActivity(chatIntent);
//                    }
//                });
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // This device details have changed

                Log.d(TAG, "Wifi P2P This Device Changed Action");
                // Update device info
                WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                // Handle device info change as needed
            }
        }

        // Implement PeerListListener for handling available peers
        private WifiP2pManager.PeerListListener peerListListener = peerList -> {
            //peersList=peerList.getDeviceList();
            // Handle the list of available peers
            //listener.onPeerDevicesDiscovered(peersList);
            // For example: iterate through peerList to get device details
        };

        // Implement ConnectionInfoListener to handle connection details
        @SuppressLint("MissingPermission")
        private WifiP2pManager.ConnectionInfoListener connectionInfoListener =
                info -> {
                    Log.d(TAG, "Connection Info: " + info.toString());
                    if (info != null && info.groupFormed) {

                        wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                            @Override
                            public void onGroupInfoAvailable(WifiP2pGroup group) {
                                if (group != null) {

                                    List<WifiP2pDevice> clientList = new ArrayList<>(group.getClientList());
                                    if(!info.isGroupOwner) {
                                        Log.i(TAG,"Size 1 : "+group.getClientList().size() );
                                        Log.i(TAG,"Size 2 : "+clientList.size() );
                                        clientList.add(group.getOwner());

                                    }
                                    connMngr.setGroupDevicesList(clientList);

                                    Log.d(TAG, "Other Peers present in the group."+clientList.size());
                                    listener.onWifiDirectGroupDevicesDiscovered(info,clientList);

                                }
                            }
                        });
                    }else{
                        Log.d(TAG, "No Group Formed Yet.");
                        listener.onWifiDirectGroupDevicesDiscovered(info,null);
                    }

                };
    }