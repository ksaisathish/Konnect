package com.manet.konnect.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Packet implements Serializable {

    private static final long serialVersionUID = 1L;

    // Constants for packet types
    public static final int PACKET_TYPE_CONTROL = 1;
    public static final int PACKET_TYPE_ACK = 2;
    public static final int PACKET_TYPE_MESSAGE = 3;

    // Constants for data types
    public static final int DATA_TYPE_TEXT = 1;

    public static final int DATA_TYPE_IMAGE = 2;
    public static final int DATA_TYPE_CONTROL = 3;


    // Common packet fields
    private String srcUsername;
    private String dstUsername;
    private int hopCount;



    private int packetType;
    private int dataType;
    private byte[] body;

    // Constructor
    public Packet(String srcUsername, String dstUsername, int hopCount, int packetType, int dataType, byte[] body) {
        this.srcUsername = srcUsername;
        this.dstUsername = dstUsername;
        this.hopCount = hopCount;
        this.packetType = packetType;
        this.dataType = dataType;
        this.body = body;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "srcUsername='" + srcUsername + '\'' +
                ", dstUsername='" + dstUsername + '\'' +
                ", hopCount=" + hopCount +
                ", packetType=" + packetType +
                ", dataType=" + dataType +
                '}';
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


    public String getSrcUsername() {
        return srcUsername;
    }

    public void setSrcUsername(String srcUsername) {
        this.srcUsername = srcUsername;
    }

    public String getDstUsername() {
        return dstUsername;
    }

    public void setDstUsername(String dstUsername) {
        this.dstUsername = dstUsername;
    }

    public int getHopCount() {
        return hopCount;
    }

    public void setHopCount(int hopCount) {
        this.hopCount = hopCount;
    }

    public int getPacketType() {
        return packetType;
    }

    public void setPacketType(int packetType) {
        this.packetType = packetType;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
