package com.manet.konnect.core;

import java.io.Serializable;

public class RoutingTableEntry implements Serializable  {

    private String username;
    private String endpointId;
    private String nextHop;
    private int distance;


    public RoutingTableEntry(String username, String endpointId, String nextHop, int distance) {
        this.username = username;
        this.endpointId = endpointId;
        this.nextHop = nextHop;
        this.distance = distance;
    }

    // Add getters and setters for each field...

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public String getNextHop() {
        return nextHop;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }
    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
