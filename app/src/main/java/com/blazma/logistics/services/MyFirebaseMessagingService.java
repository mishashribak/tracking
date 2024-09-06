package com.blazma.logistics.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.blazma.logistics.R;
import com.blazma.logistics.activities.LoginActivity;
import com.blazma.logistics.global.MyPreferenceManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";

    private static final String LOGISTICS_CHANNEL_NAME = "Logistics Notification Channel";
    private static final String LOGISTICS_NOTIFICATION_NAME = "Logistics";
    private static final String LOGISTICS_CHANNEL_DESCRIPTION = "This is used for managing the FCM";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        MyPreferenceManager.getInstance(this).setFCMToken(token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        String title = getString(R.string.app_name);
        String bodyMessage = "Welcome to Logistics!";

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Push Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
            }
            else {
                // Handle message within 10 seconds
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            title = remoteMessage.getNotification().getTitle();
            bodyMessage = remoteMessage.getNotification().getBody();
        }

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(LOGISTICS_CHANNEL_NAME, LOGISTICS_NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(LOGISTICS_CHANNEL_DESCRIPTION);
            channel.setShowBadge(true);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // show the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, LOGISTICS_CHANNEL_NAME)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(bodyMessage)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setNumber(1)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //Params:
        //  tag – the string identifier for a notification. Can be null. id – the ID of the notification.
        // The pair (tag, id) must be unique within your app. notification – the notification to post to the system
        notificationManager.notify(String.valueOf(System.currentTimeMillis()),0, mBuilder.build());
        super.onMessageReceived(remoteMessage);
    }

    private void sendRegistrationToServer(String token) {
        // Save the device token to local
    }
}
