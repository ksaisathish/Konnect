package com.manet.konnect.core;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class RoutingTable {
    private final String TAG="RoutingTable";

    private static final RoutingTable instance = new RoutingTable();

    public Map<String, RoutingTableEntry> getTable() {
        return table;
    }

    private final Map<String, RoutingTableEntry> table = new HashMap<>();

    private RoutingTable() {
        // Private constructor to enforce singleton pattern
    }

    public static RoutingTable getInstance() {
        return instance;
    }

    // Method to add an entry to the routing table
    public void addRoutingEntry(String username, String endpointId, String nextNodeToBeSent, int distance) {
        RoutingTableEntry entry = new RoutingTableEntry(username, endpointId, nextNodeToBeSent,distance);
        table.put(username, entry);
        Log.i(TAG,"Added new Entry "+username+" to the Routing Table!");
    }

    public void addRoutingEntry(RoutingTableEntry entry) {
        table.put(entry.getUsername(), entry);
        Log.i(TAG,"Added new Entry "+entry.getUsername()+" to the Routing Table!");
        Log.i(TAG,"Table Size : "+table.size());
    }
    public void removeRoutingEntry(String username){
        table.remove(username);
    }

    public RoutingTableEntry getEntryByUsername(String username) {
        return table.get(username);
    }

    // Method to get the next hop for a given username
    public String getNextHop(String username) {
        RoutingTableEntry entry = table.get(username);
        return (entry != null) ? entry.getNextHop() : null;
    }

    // Additional methods can be added based on your requirements

    // You might want to add methods for updating entries, removing entries, etc.
}
