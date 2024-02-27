package com.manet.konnect.core;

import android.content.Context;
import android.util.Log;

import com.manet.konnect.utils.Message;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThread extends Thread implements Serializable {
    // Your ServerThread implementation

    // Implement Serializable
    private static final long serialVersionUID = 1L;
    private final int serverPort=7777;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private String TAG="ServerThread";
    private Context context;

    public ServerThread(Context context) {
        this.context=context;
        executorService = Executors.newCachedThreadPool(); // Thread pool for clients
        Log.i(TAG,"ServerThread Initialized.");
    }

    @Override
    public void run() {
        try {
            Log.i(TAG,"Server Thread Started Running");
            serverSocket = new ServerSocket(serverPort);
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept(); // Accept incoming connection
                // Handle each client connection using a separate thread or task
                executorService.execute(new ClientHandler(context,clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServer() {
        interrupt(); // Interrupt the thread
        executorService.shutdownNow(); // Shutdown the executor service
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i(TAG,"Server Closed!!");
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private String TAG="ClientHandler";
    private WifiDirectConnectionManager connMngr;
    private String deviceName;
    private Context context;

    public ClientHandler(Context context,Socket socket) {
        this.context=context;
        this.clientSocket = socket;
        connMngr=new WifiDirectConnectionManager(context,null);
        deviceName = connMngr.getWifiP2pDeviceName();
        Log.i(TAG,"ClientHandler initialized.");
    }
    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String response = processRequest(inputLine); // Process client request
                out.println(response); // Send response to the client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processRequest(String request) {
        String[] parts = request.split("\\|"); // Assuming commands are separated by '|'
        String command = parts[0];

        switch (command) {
            case "GetDeviceName":
                return getDeviceName(); // Return device name

            case "ForwardMessage":
                if (parts.length >= 4) { // Assuming format: "ForwardMessage|Sender|Receiver|Message"
                    String sender = parts[1];
                    String receiver = parts[2];
                    String messageContent = parts[3];

                    Message message = new Message(sender, receiver, messageContent);

                    // Logic to handle the message based on sender, receiver, etc.
                    boolean sentSuccessfully = handleMessage(message);

                    return sentSuccessfully ? "Message sent successfully" : "Message delivery failed";
                } else {
                    return "Invalid message format";
                }

            default:
                return "Unknown command";
        }
    }

    private String getDeviceName() {
        return deviceName;
    }

    private boolean handleMessage(Message message) {
        // Replace this with your logic to handle the message
        // Check if the receiver matches this device, handle it accordingly
        if (message.getReceiver().equals(deviceName)) {
            // Process the message for this device
            Log.i(TAG,"Received message: " + message.getMessageContent());
            return true; // Message handled by this device
        } else {

            // Forward the message to the next neighbor or handle it as needed
            // Example: send the message to the next device in the network
            // Your logic to forward the message to the next neighbor
            return true; // Assume the message was successfully forwarded
        }
    }
}