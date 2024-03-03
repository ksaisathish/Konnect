package com.manet.konnect.core;

// Inside RoutingManager class

import android.util.Log;

import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.manet.konnect.utils.ChatInterface;
import com.manet.konnect.utils.MessageItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class RoutingManager {

    private static final RoutingManager instance = new RoutingManager();


    private RoutingManager() {
        // Private constructor to enforce singleton pattern
        this.routingTable = RoutingTable.getInstance();
        initPayloadCallback();
    }

    public static RoutingManager getInstance() {
        return instance;
    }
    private static final String TAG = "RoutingManager";

    private final RoutingTable routingTable;

    private PayloadCallback payloadCallback;
    private NearbyConnectionsManager nearbyConnectionsManager=NearbyConnectionsManager.getInstance();

    // Initialize PayloadCallback
    private void initPayloadCallback() {
        payloadCallback = new PayloadCallback() {
            @Override
            public void onPayloadReceived(String endpointId, Payload payload) {
                Log.i(TAG, "onPayloadReceived!");

                // Handle received packet
                if (payload.getType() == Payload.Type.BYTES) {
                    try {
                        byte[] receivedBytes = payload.asBytes();
                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receivedBytes));
                        Packet receivedPacket = (Packet) ois.readObject();

                        // Process the received packet using the RoutingManager
                        processReceivedPacket(receivedPacket, endpointId);

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                Log.i(TAG, "onPayloadTransferUpdate!");
                // Handle payload transfer update
            }
        };
    }

    public PayloadCallback getPayloadCallback() {
        return payloadCallback;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    // Other methods...

    // Method to process the received packet
    // Inside processReceivedPacket method in RoutingManager
    private void processReceivedPacket(Packet receivedPacket, String sourceEndpointId) {
        String destinationUsername = receivedPacket.getDstUsername();
        receivedPacket.setHopCount(receivedPacket.getHopCount()+1);

        if(receivedPacket.getHopCount()>25){
            Log.i(TAG,"Packet TimedOut!!");
            return;
        }

        if(destinationUsername==null ||destinationUsername.equals("")){
            if(receivedPacket.getPacketType()==Packet.PACKET_TYPE_CONTROL){
                handleControlPacket(receivedPacket,sourceEndpointId);
                return;
            }
        }

        // Check if the packet is meant for the current node
        if (destinationUsername.equals(nearbyConnectionsManager.getLocalUserName())) {
            int packetType = receivedPacket.getPacketType();

            switch (packetType) {
                case Packet.PACKET_TYPE_CONTROL:
                    handleControlPacket(receivedPacket, sourceEndpointId);
                    break;
                case Packet.PACKET_TYPE_ACK:
                    // Handle acknowledgment packet
                    break;
                case Packet.PACKET_TYPE_MESSAGE:
                    // Handle message packet
                    int dataType = receivedPacket.getDataType();
                    switch (dataType) {
                        case Packet.DATA_TYPE_TEXT:
                            handleTextData(receivedPacket, sourceEndpointId);
                            break;
                        case Packet.DATA_TYPE_IMAGE:
                            // Handle image data (you can add this part later)
                            break;
                        // Add more cases for other data types if needed
                    }
                    break;

                // Add more cases for other packet types if needed
            }
        } else {
            // Packet is not meant for the current node, look up routing table
            String nextHop = routingTable.getNextHop(destinationUsername);
            if (nextHop != null) {
                nearbyConnectionsManager.sendPayload(nextHop,receivedPacket);
            }
            else{
                Log.i(TAG,"Next Hop Not Found!");
                //Should Send Control Message back to src node, saying hop disconnected, etc..
            }

        }
    }

    private void handleTextData(Packet receivedPacket, String sourceEndpointId) {
        String senderUsername = receivedPacket.getSrcUsername();
        String textMessage = new String(receivedPacket.getBody());
        RoutingTableEntry entry= nearbyConnectionsManager.getRoutingManager().getRoutingTable().getEntryByUsername(senderUsername);
        MessageItem messageItem=new MessageItem(senderUsername,textMessage);
        entry.addMessageToList(messageItem);
        entry.incrementUnreadMessageCount();
        nearbyConnectionsManager.listener.onAllNearbyDevicesConnected();

        Log.i(TAG,"Unread Message Count : "+entry.getUnreadMessageCount());
        Log.i(TAG, "Received Text Message from " + senderUsername + ": " + textMessage +" through endpoint "+sourceEndpointId);
        MyNotificationManager notificationManager = new MyNotificationManager(nearbyConnectionsManager.getContext());
        notificationManager.showNewMessageNotification(senderUsername, textMessage);


        // You can add more handling logic here based on your requirements
    }
    private void handleControlPacket(Packet receivedPacket, String sourceEndpointId) {
        Log.i(TAG,"Received Control Packet from EndpointID-"+sourceEndpointId);
        // Handle the control packet
        try {
            byte[] controlPacketBytes = receivedPacket.getBody();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(controlPacketBytes));
            ControlPacket controlPacket = (ControlPacket) ois.readObject();

            // Process the control packet based on its type
            switch (controlPacket.getControlType()) {
                case ControlPacket.UPDATE_ROUTING_TABLE:
                    updateRoutingTable(controlPacket.getUpdatedEntry(),sourceEndpointId);
                    nearbyConnectionsManager.listener.onAllNearbyDevicesConnected();
                    break;
                case ControlPacket.SHARE_USER_NAME:
                    Log.i(TAG,"Discovered Endpoint(One that Connect) : "+sourceEndpointId+ " - " +controlPacket.getUserName());
                    nearbyConnectionsManager.getEndpointIdMap().put(controlPacket.getUserName(), sourceEndpointId);
                    addRoutingTableEntry(controlPacket.getUserName(), sourceEndpointId);
                    updateNewNeighbouringNode(sourceEndpointId);

//                    nearbyConnectionsManager.listener.onAllNearbyDevicesConnected(routingTable.getTable());
                    updateNeighbouringNodes(sourceEndpointId,getRoutingTable().getEntryByUsername(controlPacket.getUserName()));
                    nearbyConnectionsManager.listener.onNearbyConnectionDevicesDevicesConnected(nearbyConnectionsManager.getConnectedDevices());
                    nearbyConnectionsManager.listener.onAllNearbyDevicesConnected();
                    Log.i(TAG,"End Point Map SIZE : "+nearbyConnectionsManager.getEndpointIdMap().size());
                    Log.i(TAG,"RoutingTable SIZE : "+routingTable.getTable().size());
                    break;

                // Add more cases for other control packet types if needed
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // Handle exception if needed
        }
    }

    private void updateRoutingTable(RoutingTableEntry updatedEntry,String sourceEndpointId) {
        Log.i(TAG,"updateRoutingTable Called!");
        String updatedUsername = updatedEntry.getUsername();
        Log.i(TAG,updatedUsername);
        if(updatedEntry.getDistance()==-1){
            //routingTable.getEntryByUsername(updatedUsername);

            routingTable.removeRoutingEntry(updatedUsername);
            return;
            //updateNeighbouringNodes();
            //Should also relay this info to other nodes...Add the code here later..
            //Also missed some edge case, check on that too..
        }
        int updatedDistance = updatedEntry.getDistance() + 1; // Assuming the updated distance

        if(updatedUsername.equals(nearbyConnectionsManager.getLocalUserName())){
            Log.i(TAG,"Entry has same username as self.");
            return;
        }

        // Check if there is already an entry with the same username
        RoutingTableEntry existingEntry = routingTable.getEntryByUsername(updatedUsername);

        if (existingEntry == null) {
            updatedEntry.setDistance(updatedDistance);
            updatedEntry.setNextHop(sourceEndpointId);

            // No prior entry with the given username, add a new entry
            routingTable.addRoutingEntry(updatedEntry);
            updateNeighbouringNodes(sourceEndpointId,updatedEntry);
        } else {
            int existingDistance = existingEntry.getDistance();

            if (updatedDistance < existingDistance) {
                existingEntry.setDistance(updatedDistance);
                existingEntry.setNextHop(sourceEndpointId);
                updateNeighbouringNodes(sourceEndpointId,existingEntry);
            }
            // Otherwise, do nothing since the existing path is already shorter
        }
    }

    public void addRoutingTableEntry( String username,String endpointId){

        Log.i(TAG, "addRoutingTableEntry Called! "+username+" ID "+endpointId);
        RoutingTableEntry entry=new RoutingTableEntry(username,endpointId,endpointId,1);
        routingTable.addRoutingEntry(entry);
    }


    //Use this for disconnection - Incomplete code check logic..
    public void updateRoutingTableEntry( String username,String endpointId){

        Log.i(TAG, "updateRoutingTableEntry Called! "+username+" ID "+endpointId);
        RoutingTableEntry entry=new RoutingTableEntry(username,endpointId,endpointId,1);
        routingTable.addRoutingEntry(entry);
    }


    public void updateNewNeighbouringNode(String srcEndpointId) {
        Log.i(TAG, "updateNewNeighbouringNode Called!");
        Map<String, RoutingTableEntry> table = routingTable.getTable();
        //To be called when a new connection is established
        // 1. Send control packets to the new node with all entries in its routing table(except the new node's)
        for (RoutingTableEntry entry : table.values()) {
            Log.i(TAG,"Inside the Loop!");
            // Exclude the source endpoint ID to avoid flooding
            if (!entry.getEndpointId().equals(srcEndpointId)) {
                Log.i(TAG,"Sharing "+entry.getEndpointId()+" To "+srcEndpointId);
                sendControlPacket(entry,null, ControlPacket.UPDATE_ROUTING_TABLE, srcEndpointId);
            }
        }
    }

    public void updateNeighbouringNodes(String srcEndpointId,RoutingTableEntry updatedEntry) {
        Map<String, RoutingTableEntry> table = routingTable.getTable();
        Log.i(TAG,"Update Neighbouring Nodes called! "+srcEndpointId+ " - "+updatedEntry.toString());
        for (RoutingTableEntry entry : table.values()) {
            Log.i(TAG,"Inside loop!"+entry.getEndpointId());
            // Exclude the source endpoint ID to avoid flooding
            if (!entry.getEndpointId().equals(srcEndpointId)) {
                Log.i(TAG,"Inside First IF Block");
                if(entry.getEndpointId()==entry.getNextHop()) {
                    Log.i(TAG,"Inside Second IF Block");
                    sendControlPacket(updatedEntry,null, ControlPacket.UPDATE_ROUTING_TABLE, entry.getEndpointId());
                }
            }
        }
    }



    public void sendControlPacket(RoutingTableEntry entry,String username, int controlType, String destinationEndpointId) {
        // Create a ControlPacket with the specified control type and routing table entry
        ControlPacket controlPacket = new ControlPacket(controlType, entry,username);

        // Convert the ControlPacket to bytes
        byte[] controlPacketBytes = controlPacket.toByteArray();

        Packet controlPacketPacket = null;
        switch (controlType){
            case ControlPacket.UPDATE_ROUTING_TABLE:
                controlPacketPacket = new Packet(
                        nearbyConnectionsManager.getLocalUserName(),nearbyConnectionsManager.getUsernameByEndpointId(destinationEndpointId),0, Packet.PACKET_TYPE_CONTROL,Packet.DATA_TYPE_CONTROL, controlPacketBytes);
                break;
            case ControlPacket.SHARE_USER_NAME:
                controlPacketPacket = new Packet(
                        nearbyConnectionsManager.getLocalUserName(), null,0, Packet.PACKET_TYPE_CONTROL,Packet.DATA_TYPE_CONTROL, controlPacketBytes);
                break;
        }
        // Create a Packet with the control packet bytes

        // Send the control packet to the specified destination endpoint
        nearbyConnectionsManager.sendPayload(destinationEndpointId, controlPacketPacket);
    }

    private String generateRoutingTableKey(String srcUsername, String dstUsername) {
        return srcUsername + "-" + dstUsername;
    }
}
