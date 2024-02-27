package com.manet.konnect.core;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

public class ClientThread extends Thread {
    private String serverIpAddress;
    private int serverPort;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Queue<String> responseQueue; // Queue to store server responses

    public ClientThread(String serverIpAddress, int serverPort) {
        this.serverIpAddress = serverIpAddress;
        this.serverPort = serverPort;
        responseQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        try {
            clientSocket = new Socket(serverIpAddress, serverPort);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Start a loop to continuously read from server
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseQueue.offer(inputLine); // Store server response in the queue
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendCommand(String command) {
        if (out != null) {
            out.println(command); // Send command to the server
        }
    }

    public String getNextResponse() {
        return responseQueue.poll(); // Retrieve and remove the next response from the queue
    }

    // Other methods to handle the response queue, close connections, etc.
}
