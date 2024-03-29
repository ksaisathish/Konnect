package com.manet.konnect.core;

import android.util.Log;

import com.manet.konnect.utils.MessageItem;

import java.io.Serializable;
import java.util.ArrayList;

public class RoutingTableEntry implements Serializable  {


    private String username;
    private String endpointId;
    private String nextHop;
    private int distance;

    private transient ArrayList<MessageItem> messageList=new ArrayList<>();



    private transient int unreadMessageCount=0;


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

    public ArrayList<MessageItem> getMessageList() {
        if(messageList==null){
            messageList=new ArrayList<>();
        }
        return messageList;
    }

    public void setMessageList(ArrayList<MessageItem> messageList) {
        this.messageList = messageList;
    }
    public void addMessageToList(MessageItem messageItem) {
        if(messageList==null){
            messageList=new ArrayList<>();
        }
        this.messageList.add(messageItem);
        Log.i("RoutingTableEntry","Added Message to List! Size : "+messageList.size());
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public void incrementUnreadMessageCount(){
        unreadMessageCount++;
    }
    public void clearUnreadMessageCount(){
        unreadMessageCount=0;
    }
}
