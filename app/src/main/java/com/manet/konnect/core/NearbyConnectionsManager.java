package com.manet.konnect.core;

// Import statements
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionOptions;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsOptions;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.manet.konnect.utils.OnNearbyConnectionDevicesDiscoveredListener;
import com.manet.konnect.utils.OnWifiDirectDevicesDiscoveredListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyConnectionsManager {

    private static final String TAG = "NearbyConnectionsManager";

    private final Context context;

    private final String SERVICE_ID = "com.manet.konnect";
    private final Strategy STRATEGY = Strategy.P2P_CLUSTER;

    private final List<String> connectedDevices = new ArrayList<>();
    private final List<DiscoveredEndpointInfo> discoveredEndpoints = new ArrayList<>();

    private OnNearbyConnectionDevicesDiscoveredListener listener;
    private final Map<String, String> endpointIdMap = new HashMap<>();

    private final RoutingManager routingManager;
    private final PayloadCallback payloadCallback;




    public NearbyConnectionsManager(Context context) {
        this.context = context;
        routingManager=new RoutingManager(this);
        payloadCallback = routingManager.getPayloadCallback();
    }


    public String getLocalUserName() {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public void startAdvertising() {
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
        Nearby.getConnectionsClient(context)
                .startAdvertising(
                        getLocalUserName(), SERVICE_ID, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            Log.i(TAG,"Started Advertising");
                            // We're advertising!
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            Log.i(TAG,"Error in Advertising!");
                            e.printStackTrace();
                            // We were unable to start advertising.
                        });
    }

    public void startDiscovery() {
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        Nearby.getConnectionsClient(context)
                .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            discoveredEndpoints.clear();
                            listener.onNearbyConnectionDevicesDiscovered(discoveredEndpoints);
                            Log.i(TAG,"Started Discovery");
                            showToast("Started Discovery", Toast.LENGTH_SHORT);
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            Log.i(TAG,"Unable to Start Discovery!");
                            showToast("Unable to Start Discovery!", Toast.LENGTH_SHORT);
                            e.printStackTrace();
                        });
    }

    public void stopAdvertising() {
        Nearby.getConnectionsClient(context).stopAdvertising();
        Log.i(TAG,"Stopped Advertising");
        showToast("Stopped Advertising!", Toast.LENGTH_SHORT);

    }

    public void stopDiscovery() {
        Nearby.getConnectionsClient(context).stopDiscovery();
        Log.i(TAG,"Stopped Discovery");
        showToast("Stopped Discovery!", Toast.LENGTH_SHORT);

    }

    // Other necessary methods for sending/receiving data, connecting, etc.

    // Callbacks

    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(TAG,"Connection Initiated!");

                    Log.i(TAG,"Discovered Endpoint(Wanting to Connect) : "+endpointId+ " - " +connectionInfo.getEndpointInfo());
                    endpointIdMap.put(connectionInfo.getEndpointName(), endpointId);
                    Log.i(TAG,"SIZE : "+endpointIdMap.size());
                    // Handle connection initiation
                    Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    Log.i(TAG,"Connection Result!");

                    // Handle connection result
                    if (result.getStatus().isSuccess()) {
                        Log.i(TAG,"Connection Success!");



                        connectedDevices.add(endpointId);
                        routingManager.addRoutingTableEntry(getUsernameByEndpointId(endpointId),endpointId);
                        routingManager.updateNewNeighbouringNode(endpointId);
                        listener.onNearbyConnectionDevicesDevicesConnected(connectedDevices);
                    }
                    else{
                        Log.i(TAG,"Connection Failure!");
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    // Handle disconnection
                    connectedDevices.remove(endpointId);
                    //updateRoutingTableEntry()
                    //Add code to remove entries from routing table and to relay that info to others..
                    Log.i(TAG,"Context : "+context);
                    if(context!=null) {
                        listener.onNearbyConnectionDevicesDevicesConnected(connectedDevices);
                    }
                    Log.i(TAG,"Connection Disconnected!");
                }
            };

    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo discoveredEndpointInfo) {
                    // Handle endpoint discovery
                    /*
                    Nearby.getConnectionsClient(context).requestConnection(
                            SERVICE_ID, endpointId, connectionLifecycleCallback
                    );*/

                    Log.i(TAG,"Discovered Endpoint : "+endpointId+ " - " +discoveredEndpointInfo.getEndpointName());
                    discoveredEndpoints.add(discoveredEndpointInfo);
                    endpointIdMap.put(discoveredEndpointInfo.getEndpointName(), endpointId);
                    Log.i(TAG,"Discovered Endpoint : "+endpointIdMap.get(discoveredEndpointInfo.getEndpointName()));
                    Log.i(TAG,"SIZE : "+endpointIdMap.size());

                    listener.onNearbyConnectionDevicesDiscovered(discoveredEndpoints);
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    Log.i(TAG,"Discovered Endpoint Lost : "+endpointId);
                    // Handle endpoint loss
                    String endPointName = null;
                    for (Map.Entry<String, String> entry : endpointIdMap.entrySet()) {
                        if (entry.getValue().equals(endpointId)) {
                            endPointName=entry.getKey();
                            endpointIdMap.remove(entry.getKey());
                            break;
                        }
                    }
                    String finalEndPointName = endPointName;
                    discoveredEndpoints.removeIf(endpoint -> endpoint.getEndpointName().equals(finalEndPointName));


                    listener.onNearbyConnectionDevicesDiscovered(discoveredEndpoints);
                }
            };

    public String getUsernameByEndpointId(String endpointId) {
        for (Map.Entry<String, String> entry : endpointIdMap.entrySet()) {
            if (entry.getValue().equals(endpointId)) {
                return entry.getKey(); // Return the key (username/endpointName)
            }
        }
        return null; // Return null if no match is found
    }



    public void disconnectFromEndpoint(String endpointName) {
        // Check if the endpointName exists in the map
        if (endpointIdMap.containsKey(endpointName)) {
            String endpointId = endpointIdMap.get(endpointName);
            Nearby.getConnectionsClient(context).disconnectFromEndpoint(endpointId);
            connectedDevices.remove(endpointId); // Remove from the connectedDevices list
            endpointIdMap.remove(endpointName); // Remove from the map
        }
    }

    public void disconnectFromAllEndpoints() {
        for (String endpointId : connectedDevices) {
            Nearby.getConnectionsClient(context).disconnectFromEndpoint(endpointId);
        }
        connectedDevices.clear(); // Clear the list after disconnecting from all endpoints
        Log.i(TAG,"Disconnected form All Endpoints");
    }

    public String getEndpointId(String endpointName) {
        return endpointIdMap.get(endpointName);
    }
    public List<DiscoveredEndpointInfo> getDiscoveredEndpoints() {
        return discoveredEndpoints;
    }

    public void initiateConnection(String endpointName) {
        Log.i(TAG,"SIZE : "+endpointIdMap.size());
        Log.i(TAG,"Initiate Connection to "+endpointName+ " called! - "+endpointIdMap.get(endpointName));


         Nearby.getConnectionsClient(context).requestConnection(
                SERVICE_ID, endpointIdMap.get(endpointName), connectionLifecycleCallback
                ).addOnSuccessListener(
                (Void unused) -> {
                    Log.i(TAG,"Started Initiation");

                })
                .addOnFailureListener(
                        (Exception e) -> {
                            Log.i(TAG,"Error in Initiation!");
                            e.printStackTrace();
                        });


    }

    public void sendPayload(String endpointId, Packet packet) {

        if (Nearby.getConnectionsClient(context) != null) {
            // Send the payload to the specified endpoint
            Nearby.getConnectionsClient(context)
                    .sendPayload(endpointId, Payload.fromBytes(packet.toByteArray()))
                    .addOnSuccessListener(aVoid -> {
                        // Payload sent successfully
                        // Handle success if needed
                    })
                    .addOnFailureListener(e -> {
                        // Payload sending failed
                        // Handle failure if needed
                        e.printStackTrace();
                    });
        }else{
            Log.e(TAG,"Not able to Send Payload! Nearby Connections does not exist!!");
        }
    }

    public void setNearbyConnectionDevicesDiscoveredListener(OnNearbyConnectionDevicesDiscoveredListener listener) {
        Log.i(TAG,"Nearby Connection Devices Discovered Listener On.");
        this.listener = listener;

    }


    private void showToast(String message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }



}