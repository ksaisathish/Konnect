package com.manet.konnect.utils;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.manet.konnect.R;

import java.util.ArrayList;
import java.util.List;

public class MessageDebugAdapter extends ArrayAdapter<MessageItem> {
    private Context context;

    private List<MessageItem> messageList;
    TextView personNameTextView;
    TextView messageTextView;
    private String TAG="MessageDebugAdapter";

    public MessageDebugAdapter(@NonNull Context context, ArrayList<MessageItem> list) {
        super(context, 0, list);
        this.context = context;
        this.messageList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_item_layout, null);

        }

        personNameTextView = convertView.findViewById(R.id.person_name);
        messageTextView = convertView.findViewById(R.id.message);
        // Get the current message item
        MessageItem currentItem = getItem(position);

        // Set the data to the views
        if (currentItem != null) {
            //Log.i(TAG,currentItem.getMessage());
            //Log.i(TAG,currentItem.getPersonName());
            personNameTextView.setText(currentItem.getPersonName());
            messageTextView.setText(currentItem.getMessage());
        }

        return convertView;
    }

    // Method to add a new message to the list
    public void addMessage(MessageItem messageItem) {
        messageList.add(messageItem);
        notifyDataSetChanged();
    }

    public void updateData(){
        notifyDataSetChanged();
    }
}


