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
import java.util.function.LongFunction;

public class DataTransferManager {

    private static final String TAG = "DataTransferManager";
    private static final int PORT = 8888;

    private ServerSocket serverSocket;
    private Socket socket;
    private boolean isGroupOwner;
    private Channel channel;
    private WifiP2pManager wifiP2pManager;
    private ChatInterface chatInterface;
    private InputStream inputStream;
    private OutputStream outputStream;
    WifiDirectConnectionManager connMngr;
    private volatile boolean isRunning = true;

    public DataTransferManager(ChatInterface chatInterface,Context context,WifiDirectConnectionManager connMngr, WifiP2pInfo info) {
        this.chatInterface=chatInterface;
        this.channel = connMngr.getChannel();
        this.wifiP2pManager = connMngr.getWifiP2pManager();
        this.connMngr=connMngr;

        this.isGroupOwner = info.isGroupOwner;
        if (info.groupFormed && info.isGroupOwner) {
            startServerSocket();
        } else if (info.groupFormed) {

            connectToGroupOwner(info.groupOwnerAddress.getHostAddress());
        }

    }
    private void startServerSocket() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                socket = serverSocket.accept();
                Log.i(TAG,"Server Socket Created!");
                while (isRunning) {
                    receiveFromOtherDevice();
                }
            } catch (IOException e) {
                e.printStackTrace();
                cleanup();
            }
        }).start();
    }

    private void connectToGroupOwner(final String hostAddress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(hostAddress, PORT);
                    Log.i(TAG,"Connected to Group Owner.");
                    while (isRunning ) {
                        receiveFromOtherDevice();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    cleanup();
                }
            }
        }).start();
    }

    private void receiveFromOtherDevice() {
        try {
            inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            String receivedMessage = new String(buffer, 0, bytesRead);
            Log.i(TAG,"Received Message : "+receivedMessage);
            // Assuming 'receivedMessage' is the message received from the other device
            MessageItem messageItem = new MessageItem(connMngr.getDeviceNameFromSocketAddress(socket.getInetAddress()),receivedMessage);

            // After receiving the message, call the loadReceivedMessage() method of ChatInterface
            chatInterface.loadReceivedMessage(messageItem);
            // Handle received message as needed
        } catch (IOException e) {
            e.printStackTrace();
            cleanup();
        }
    }

    public void sendMessageToDevice(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    outputStream = socket.getOutputStream();
                    outputStream.write(message.getBytes());

                    Log.i(TAG,"Sent Message - "+message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    public void cleanup() {
        isRunning = false;
        // Perform cleanup operations here
        // Close sockets, release resources, etc.
        try {
            if (socket != null) {
                socket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
            inputStream.close();
            outputStream.close();
            // Other resource cleanup if needed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}