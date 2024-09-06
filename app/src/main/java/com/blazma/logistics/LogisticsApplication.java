package com.blazma.logistics;

import android.app.Application;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.global.MyPreferenceManager;
import com.blazma.logistics.interfaces.api.ApiManager;
import com.google.firebase.FirebaseApp;

import org.greenrobot.eventbus.EventBus;

public class LogisticsApplication extends Application implements LifecycleObserver {

    // Api Manager
    public static ApiManager apiManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the preference manager
        MyPreferenceManager.getInstance(this);

        apiManager = new ApiManager(getApplicationContext());
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        FirebaseApp.initializeApp(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        //App in background
        EventBus.getDefault().post(new EventBusMessage(EventBusMessage.MessageType.ON_APP_BACKGROUND));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App in foreground
    }

}
