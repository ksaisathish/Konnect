package com.manet.konnect.utils.debug;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.manet.konnect.R;
import com.manet.konnect.core.DataTransferManager;
import com.manet.konnect.core.NearbyConnectionsManager;
import com.manet.konnect.core.Packet;
import com.manet.konnect.core.WifiDirectConnectionManager;
import com.manet.konnect.utils.ChatInterface;
import com.manet.konnect.utils.MessageDebugAdapter;
import com.manet.konnect.utils.MessageItem;

import java.util.ArrayList;

public class NearbyChatDebugActivity extends AppCompatActivity implements ChatInterface {

    private TextView chatUserName;
    private ListView messagesListView;
    private MessageDebugAdapter messageAdapter;
    private EditText messageBoxField;
    private Button sendButton;
    private NearbyConnectionsManager nearbyConnectionsManager;
    ArrayList<MessageItem> messageList;

    String username;

    private final String  TAG="NearbyChatDebugActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_chat_debug);
        username =getIntent().getStringExtra("username");

        nearbyConnectionsManager=NearbyConnectionsManager.getInstance();

        chatUserName =findViewById(R.id.chatUserName);
        chatUserName.setText(username);

        messagesListView = findViewById(R.id.messagesList);
        messageBoxField = findViewById(R.id.messageBoxField);
        sendButton = findViewById(R.id.sendButton);

        // Initialize the message list and adapter
        //messageList = new ArrayList<>();
        messageList=nearbyConnectionsManager.getRoutingManager().getRoutingTable().getEntryByUsername(username).getMessageList();
        messageAdapter = new MessageDebugAdapter(this, messageList);
        messagesListView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> sendMessage());
    }

    // Method to send a message
    private void sendMessage() {
        String message = messageBoxField.getText().toString().trim();
        if (!message.isEmpty()) {
            String srcUsername= nearbyConnectionsManager.getLocalUserName();

            Packet textPacket=new Packet(srcUsername,username, 0, Packet.PACKET_TYPE_MESSAGE, Packet.DATA_TYPE_TEXT,message.getBytes());
            String endpointId= nearbyConnectionsManager.getRoutingManager().getRoutingTable().getNextHop(username);
            Log.i(TAG,"Next Hop is "+endpointId);
            nearbyConnectionsManager.sendPayload(endpointId,textPacket);
            MessageItem sentMessage = new MessageItem("You / "+srcUsername, message);
            messageList.add(sentMessage);

            Log.i(TAG,"Here..");
            messageAdapter = new MessageDebugAdapter(this, messageList);
            //messageAdapter.addMessage(sentMessage);
            messagesListView.setAdapter(messageAdapter);
            messageBoxField.getText().clear();
        }
    }


    //This is not used for this case, as of yet.
    @Override
    public void loadReceivedMessage(MessageItem messageItem) {
        runOnUiThread(() -> {
            messageItem.setPersonName(username);
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
    }
}
