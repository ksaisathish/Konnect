package com.manet.konnect.utils.debug;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
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

public class NearbyChatDebugActivity extends AppCompatActivity{

    private TextView chatUserName;
    private ListView messagesListView;
    private MessageDebugAdapter messageAdapter;
    private EditText messageBoxField;
    private Button sendButton;
    private NearbyConnectionsManager nearbyConnectionsManager;
    ArrayList<MessageItem> messageList;

    String username;

    private final String  TAG="NearbyChatDebugActivity";
    private Handler mHandler = new Handler();

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

        if(nearbyConnectionsManager.getRoutingManager().getRoutingTable().getEntryByUsername(username)==null){
            Log.i(TAG,"IS null");
        }
        Log.i(TAG,"HERE!");
        if(messageList==null){
            Log.i(TAG,"List is Null");
            messageList=new ArrayList<>();
        }
        else{
            Log.i(TAG,"List is Not Null");
        }
        messageAdapter = new MessageDebugAdapter(this, messageList);
        messagesListView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> sendMessage());

        mHandler.postDelayed(mRunnable, 500);
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



    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            messageAdapter.updateData();
            messagesListView.setAdapter(messageAdapter);
            nearbyConnectionsManager.getRoutingManager().getRoutingTable().getEntryByUsername(username).clearUnreadMessageCount();
            nearbyConnectionsManager.listener.onAllNearbyDevicesConnected();
//            Log.i(TAG,"MessageList updated..");
            mHandler.postDelayed(this, 200);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

    }
}
