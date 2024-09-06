package com.blazma.logistics.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blazma.logistics.BuildConfig;
import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.databinding.ActivityMainBinding;
import com.blazma.logistics.fragments.main.MainFragment;
import com.blazma.logistics.fragments.main.common.TaskDetailFragment;
import com.blazma.logistics.fragments.main.common.TaskDetailSecondFlowFragment;
import com.blazma.logistics.fragments.main.common.TaskStatusFragment;
import com.blazma.logistics.fragments.main.list_task.DriverSignatureFragment;
import com.blazma.logistics.fragments.main.list_task.ListTaskSecondSampleInfoFragment;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.global.MyPreferenceManager;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.login.LoginResponse;
import com.blazma.logistics.model.task.DefaultResponse;
import com.blazma.logistics.services.ForegroundOnlyLocationService;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private static Integer LOCATION_PERMISSION_CODE = 112;
    private ActivityMainBinding mMainBinding;
    private Context mContext;
//    private LayoutSideMenuBinding mSideMenuBinding;
    private boolean foregroundOnlyLocationServiceBound = false;
    private ForegroundOnlyLocationService foregroundOnlyLocationService;
    private ServiceConnection foregroundOnlyServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            foregroundOnlyLocationService = ((ForegroundOnlyLocationService.LocalBinder)service).getService();
            foregroundOnlyLocationServiceBound = true;
            if(checkLocationPermission()){
                startService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            foregroundOnlyLocationService = null;
            foregroundOnlyLocationServiceBound = false;
        }
    };

    private class ForegroundOnlyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
          Location  location = (Location) intent.getParcelableExtra(ForegroundOnlyLocationService.EXTRA_LOCATION);
          if (location != null) {
//            updateLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
              MyPreferenceManager.getInstance().put(AppConstants.LAT, String.valueOf(location.getLatitude()));
              MyPreferenceManager.getInstance().put(AppConstants.LNG, String.valueOf(location.getLongitude()));
          }
        }
    }

    private ForegroundOnlyBroadcastReceiver foregroundOnlyBroadcastReceiver;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFragment(EventBusMessage messageEvent){
        if (messageEvent != null){
            int messageType = messageEvent.getMessageType();
            if (messageType == EventBusMessage.MessageType.ON_APP_BACKGROUND){
                unbindService();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    private void unbindService(){
        if (foregroundOnlyLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection);
            foregroundOnlyLocationServiceBound = false;
        }
    }

    private void runLocationTimer(){
        Timer timer = new Timer();
        new Thread(new Runnable() {
            public void run() {
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        String lat = MyPreferenceManager.getInstance().getString(AppConstants.LAT);
                        String lng = MyPreferenceManager.getInstance().getString(AppConstants.LNG);
                        if(lat != null && lng != null){
                            if (UserInfo.getInstance().loginInfo != null){
                                Log.e("Timer", "Sending user location...");
                                updateLocation(lat, lng);
                            }
                            else{
                                Log.e("Timer", "User info is null...");
                            }
                        }
                    }
                }, 0, 120000);
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutBinding();

        // Initialize the actions of items in SideMenu
        initSideMenuAction();

        foregroundOnlyBroadcastReceiver = new ForegroundOnlyBroadcastReceiver();
        mContext = this;
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        runLocationTimer();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, ForegroundOnlyLocationService.class);
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void updateLocation(String lat, String lng){
        LogisticsApplication.apiManager.updateLocation(lat, lng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                    @Override
                    public void onSuccess(@NonNull DefaultResponse defaultResponse) {
                        Log.e("success", "location update");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    private void startService(){
        if(foregroundOnlyLocationService != null){
            foregroundOnlyLocationService.subscribeToLocationUpdates();
        }

//        foregroundOnlyLocationService.unsubscribeToLocationUpdates();
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.permission_dialog_title))
                        .setMessage(getResources().getString(R.string.permission_dialog_content))
                        .setPositiveButton((getResources().getString(R.string.ok)), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                foregroundOnlyBroadcastReceiver,
                new IntentFilter(
                        ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        );
        LocaleHelper.onAttach(this);
    }

    /**
     * SideMenu Actions
     */
    private void initSideMenuAction(){
        mMainBinding.menu.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSideMenu();
            }
        });

        // Notification
        mMainBinding.menu.menuNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSideMenu();

                Intent intent = new Intent(mContext, NotificationActivity.class);
                startActivity(intent);
            }
        });

        // Profile
        mMainBinding.menu.menuProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSideMenu();

                Intent intent = new Intent(mContext, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Logout
        mMainBinding.menu.menuLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSideMenu();

                logout();
            }
        });

        // Terms & Conditions
        mMainBinding.menu.menuTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSideMenu();

                // Go to Terms & Conditions
                Intent intent = new Intent(mContext, TermsConditionActivity.class);
                startActivity(intent);
            }
        });

        // Policy
        mMainBinding.menu.menuPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSideMenu();

                // Go to Privacy & Policy
                Intent intent = new Intent(mContext, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });

        //schedule
        mMainBinding.menu.menuSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSideMenu();

                Intent intent = new Intent(mContext, ScheduleActivity.class);
                startActivity(intent);
            }
        });

    }

    private void layoutBinding(){
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mMainBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        FragmentProcess.replaceFragment(getSupportFragmentManager(), new MainFragment(), R.id.frameLayout);
        mMainBinding.menu.tvVersion.setText(BuildConfig.VERSION_NAME);

    }

    public void toggleSideMenu(){
        if(mMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            mMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mMainBinding.drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**
     * Logout
     */
    public void logout(){
        // Actions for logout
        MyPreferenceManager.getInstance().put(AppConstants.KEY_IS_LOGGED_IN, false);

        // Go to login page
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                startService();
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }

    @Override
    public void onBackPressed(){
        FragmentManager supportFragment = this.getSupportFragmentManager();
        if((supportFragment.getBackStackEntryAt(supportFragment.getBackStackEntryCount() - 1).getName().equals(TaskDetailFragment.class.getSimpleName())) ||
                (supportFragment.getBackStackEntryAt(supportFragment.getBackStackEntryCount() - 1).getName().equals(TaskDetailSecondFlowFragment.class.getSimpleName()))){
            UserInfo.getInstance().scannedContainerId = 0;
            UserInfo.getInstance().scannedContainerType = "";
            UserInfo.getInstance().scannedBagBarCode = "";
            EventBus.getDefault().post(new EventBusMessage(EventBusMessage.MessageType.GO_TO_FREEZER_PLACEMENT));
        } else if(supportFragment.getBackStackEntryAt(supportFragment.getBackStackEntryCount() - 1).getName().equals(ListTaskSecondSampleInfoFragment.class.getSimpleName())){
            EventBus.getDefault().post(new EventBusMessage(EventBusMessage.MessageType.GO_TO_MEDICAL_TASK));
        } else if(supportFragment.getBackStackEntryAt(supportFragment.getBackStackEntryCount() - 1).getName().equals(TaskStatusFragment.class.getSimpleName())){

        } else {
            super.onBackPressed();
        }
    }
}