package com.manet.konnect.core;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.manet.konnect.R;

public class MyNotificationManager {

    private static final String CHANNEL_ID = "konnect_channel";
    private static final String CHANNEL_NAME = "KonnectApp Notifications";

    private static final int NOTIFICATION_ID = 1;

    private final Context context;

    public MyNotificationManager(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void showNewMessageNotification(String sender, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(sender)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Add sound to the notification
        builder.setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);

        vibrate(context);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    private static void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            // Vibrate for 500 milliseconds
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

}
