package com.manet.konnect.utils.debug;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.manet.konnect.R;
import com.manet.konnect.core.DataTransferManager;
import com.manet.konnect.core.ServerThread;
import com.manet.konnect.core.WifiDirectConnectionManager;
import com.manet.konnect.utils.ChatInterface;
import com.manet.konnect.utils.MessageDebugAdapter;
import com.manet.konnect.utils.MessageItem;

import java.util.ArrayList;

public class ChatDebugActivity extends AppCompatActivity implements ChatInterface {

    private TextView chatRecipientName;
    private ListView messagesListView;
    private MessageDebugAdapter messageAdapter;
    private EditText messageBoxField;
    private Button sendButton;
    private DataTransferManager dataTransferManager;
    private WifiDirectConnectionManager wifiDirectConnectionManager;
    ArrayList<MessageItem> messageList;

    ServerThread sThread;
    String deviceName;

    private final String  TAG="ChatDebugActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_debug);
        boolean isGroupOwner = getIntent().getBooleanExtra("isGroupOwner", false);
        WifiP2pInfo wifiP2pInfo=getIntent().getParcelableExtra("info");
        deviceName=getIntent().getStringExtra("deviceName");
        //sThread= (ServerThread) getIntent().getSerializableExtra("serverThread");


        wifiDirectConnectionManager =new WifiDirectConnectionManager(getApplicationContext(),null);
        dataTransferManager=new DataTransferManager(this,getApplicationContext(),wifiDirectConnectionManager,wifiP2pInfo);
        Log.i(TAG,"Data Transfer Manager called");
        chatRecipientName=findViewById(R.id.chatRecipientName);
        chatRecipientName.setText(deviceName);

        messagesListView = findViewById(R.id.messagesList);
        messageBoxField = findViewById(R.id.messageBoxField);
        sendButton = findViewById(R.id.sendButton);

        // Initialize the message list and adapter
        messageList = new ArrayList<>();

        messageAdapter = new MessageDebugAdapter(this, messageList);
        messagesListView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> sendMessage());
    }

    // Method to send a message
    private void sendMessage() {
        String message = messageBoxField.getText().toString().trim();
        if (!message.isEmpty()) {
            if (dataTransferManager != null) {
                dataTransferManager.sendMessageToDevice(message);
                // Add the sent message to the adapter and update ListView
                MessageItem sentMessage = new MessageItem("You", message);
                messageList.add(sentMessage);

                messageAdapter = new MessageDebugAdapter(this, messageList);
//                messageAdapter.addMessage(sentMessage);
                messagesListView.setAdapter(messageAdapter);
                messageBoxField.getText().clear();
            }
        }
    }

    @Override
    public void loadReceivedMessage(MessageItem messageItem) {
        runOnUiThread(() -> {
            messageItem.setPersonName(deviceName);
            messageList.add(messageItem);

            messageAdapter = new MessageDebugAdapter(this, messageList);
//            messageAdapter.addMessage(messageItem);
            messagesListView.setAdapter(messageAdapter);
            //Toast.makeText(this, "Received message: " + messageItem.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataTransferManager != null) {
            // Clean up resources in DataTransferManager
            dataTransferManager.cleanup();
        }
    }
}
