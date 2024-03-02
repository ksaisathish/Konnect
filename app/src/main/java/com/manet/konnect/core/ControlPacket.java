package com.manet.konnect.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ControlPacket implements Serializable {

    public static final int UPDATE_ROUTING_TABLE = 1;
    public static final int SHARE_USER_NAME=2;

    private int controlType;
    private RoutingTableEntry updatedEntry;

    public String getUserName() {
        return userName;
    }

    private String userName;

    // Constructors, getters, setters...

    public ControlPacket(int controlType, RoutingTableEntry updatedEntry,String userName) {
        this.controlType = controlType;
        this.updatedEntry = updatedEntry;
        this.userName=userName;

    }

    public int getControlType() {
        return controlType;
    }

    public RoutingTableEntry getUpdatedEntry() {
        return updatedEntry;
    }

    public byte[] toByteArray() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            // Write the Packet object to the ObjectOutputStream
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();

            // Get the byte array from the ByteArrayOutputStream
            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception if needed
            return new byte[0]; // Return an empty byte array on failure
        }
    }
}
