package com.blazma.logistics.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blazma.logistics.R;
import com.blazma.logistics.activities.LoginActivity;
import com.blazma.logistics.activities.MainActivity;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.utilities.AppConstants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.util.concurrent.TimeUnit;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

public class ForegroundOnlyLocationService extends Service {

    private boolean configurationChange = false;
    private boolean serviceRunningInForeground = false;
    private IBinder localBinder = new LocalBinder();

    private NotificationManager notificationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location currentLocation;

    private static String TAG = "ForegroundOnlyLocationService";

    private static String PACKAGE_NAME = "com.example.android.whileinuselocation";

    public static String ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST";

    public static String EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION";

    private static String EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION";

    private static Integer NOTIFICATION_ID = 12345678;

    private static String NOTIFICATION_CHANNEL_ID = "while_in_use_channel_01";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        serviceRunningInForeground = false;
        configurationChange = false;
        return localBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        serviceRunningInForeground = false;
        configurationChange = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        if (!configurationChange && SharedPreferenceUtil.getLocationTrackingPref(this)) {
            Log.d(TAG, "Start foreground service");
            Notification notification = generateNotification(currentLocation);
            startForeground(NOTIFICATION_ID, notification);
            serviceRunningInForeground = true;
//        }

        // Ensures onRebind() is called if MainActivity (client) rebinds.
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configurationChange = true;
    }

    public void subscribeToLocationUpdates() {
        Log.d(TAG, "subscribeToLocationUpdates()");

//        SharedPreferenceUtil.saveLocationTrackingPref(this, true);

        // Binding to this service doesn't actually trigger onStartCommand(). That is needed to
        // ensure this Service can be promoted to a foreground service, i.e., the service needs to
        // be officially started (which we do here).
        startService(new Intent(getApplicationContext(), ForegroundOnlyLocationService.class));

        try {
            // TODO: Step 1.5, Subscribe to location changes.
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        } catch (SecurityException e) {
//            SharedPreferenceUtil.saveLocationTrackingPref(this, false)
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely");
        }
    }

    public void unsubscribeToLocationUpdates() {
        Log.d(TAG, "unsubscribeToLocationUpdates()");

        try {
            // TODO: Step 1.6, Unsubscribe to location changes.

//            SharedPreferenceUtil.saveLocationTrackingPref(this, false)

        } catch (SecurityException e) {
//            SharedPreferenceUtil.saveLocationTrackingPref(this, true)
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely");
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private Notification generateNotification(Location location) {
        Log.e("generateNotification", "start");
        Intent intent = new Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        String mainNotificationText = "Location is sending to system";
//        if(location != null){
//            mainNotificationText = location.toString();
//        }
        String titleText = getString(R.string.app_name);
        // 1. Create Notification Channel for O+ and beyond devices (26+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setShowBadge(false);

            // Adds NotificationChannel to system. Attempting to create an
            // existing notification channel with its original values performs
            // no operation, so it's safe to perform the below sequence.
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent launchActivityIntent = new Intent(this, MainActivity.class);

        Intent cancelIntent = new Intent(this, ForegroundOnlyLocationService.class);
        cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true);

        //        PendingIntent servicePendingIntent = PendingIntent.getService(
//                this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent servicePendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            servicePendingIntent = PendingIntent.getActivity
                    (this, 0, cancelIntent, PendingIntent.FLAG_MUTABLE);
        }
        else{
            servicePendingIntent = PendingIntent.getActivity
                    (this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

//        PendingIntent activityPendingIntent = PendingIntent.getActivity(
//                this, 0, launchActivityIntent, 0);
        PendingIntent activityPendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            activityPendingIntent = PendingIntent.getActivity
                    (this, 0, launchActivityIntent, PendingIntent.FLAG_MUTABLE);
        }
        else{
            activityPendingIntent = PendingIntent.getActivity
                    (this, 0, launchActivityIntent, 0);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);

        return  builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titleText)
                .setContentText(mainNotificationText)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(R.mipmap.ic_launcher, "Launch Activity", activityPendingIntent)
                .addAction(R.mipmap.ic_launcher, "Stop", servicePendingIntent)
                .setNumber(0)
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(30));
        locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(30));
        locationRequest.setMaxWaitTime(TimeUnit.MINUTES.toMillis(2));
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                if (serviceRunningInForeground) {
                    notificationManager.notify(
                            NOTIFICATION_ID,
                            generateNotification(currentLocation));
                }else{
                    Intent intent = new Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST);
                    intent.putExtra(EXTRA_LOCATION, currentLocation);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean cancelLocationTrackingFromNotification =
                intent.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false);
        if (cancelLocationTrackingFromNotification) {
            unsubscribeToLocationUpdates();
            stopSelf();
        }
        // Tells the system not to recreate the service after it's been killed.
        return START_NOT_STICKY;
    }

    public class LocalBinder extends Binder {
        public ForegroundOnlyLocationService getService(){
            return ForegroundOnlyLocationService.this;
        }
    }
}
