package com.manet.konnect.core;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import com.manet.konnect.utils.ChatInterface;
import com.manet.konnect.utils.MessageItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DataTransferManager {

    private static final String TAG = "DataTransferManager";
    private static final int PORT = 8888;

    private ServerSocket serverSocket;
    private Socket socket;
    private boolean isGroupOwner;
    private Channel channel;
    private WifiP2pManager wifiP2pManager;
    private ChatInterface chatInterface;

    public DataTransferManager(ChatInterface chatInterface,Context context,Channel channel, WifiP2pManager wifiP2pManager, WifiP2pInfo info) {
        this.chatInterface=chatInterface;
        this.channel = channel;
        this.wifiP2pManager = wifiP2pManager;

        this.isGroupOwner = info.isGroupOwner;
        if (info.groupFormed && info.isGroupOwner) {
            startServerSocket();
        } else if (info.groupFormed) {
            connectToGroupOwner(info.groupOwnerAddress.getHostAddress());
        }
    }
    private void startServerSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(PORT);
                    socket = serverSocket.accept();

                    while (true) {
                        receiveFromOtherDevice();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void connectToGroupOwner(final String hostAddress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(hostAddress, PORT);

                    while (true) {
                        receiveFromOtherDevice();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void receiveFromOtherDevice() {
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            String receivedMessage = new String(buffer, 0, bytesRead);
            // Assuming 'receivedMessage' is the message received from the other device
            MessageItem messageItem = new MessageItem(socket.getInetAddress().toString(),receivedMessage);

            // After receiving the message, call the loadReceivedMessage() method of ChatInterface
            chatInterface.loadReceivedMessage(messageItem);
            // Handle received message as needed
            Log.d(TAG, "Received message: " + receivedMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToDevice(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(message.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    public void cleanup() {
        // Perform cleanup operations here
        // Close sockets, release resources, etc.
        try {
            if (socket != null) {
                socket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
            // Other resource cleanup if needed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}