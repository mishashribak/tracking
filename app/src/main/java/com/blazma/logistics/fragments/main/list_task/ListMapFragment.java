package com.blazma.logistics.fragments.main.list_task;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.databinding.FragmentTaskMapBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.firstflow.FirstTaskDetailFragment;
import com.blazma.logistics.fragments.main.swap.SwapTaskBarcodeScanFragment;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.login.DriverTask;
import com.blazma.logistics.model.swap.SwapTaskData;
import com.blazma.logistics.model.task.DefaultResponse;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ListMapFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener {

    private String TAG = ListMapFragment.class.getSimpleName();

    private FragmentTaskMapBinding mBinding;
    private static final int CALL_PHONE_CODE = 1003;
    private static final int LOCATION_PERMISSION_CODE = 1004;

    private GoogleMap mGoogleMap;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    private Location mLocation;
    Marker mCurrLocationMarker;
    Marker mFromLocationMarker;

    private boolean isLoadMyLocation = false;

    // Check if task is Medical/Swap
    private boolean isSwapTask = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_map, container, false);
        this.initData();
        this.initActions();
        this.initComponent();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        return mBinding.getRoot();
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        isLoadMyLocation = false;

        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFragment(EventBusMessage messageEvent) {
        if (messageEvent != null) {
            int messageType = messageEvent.getMessageType();
            if (messageType == EventBusMessage.MessageType.GO_TO_MEDICAL_TASK) { // Added new address
                // Clear the Fragment BackStack
                clearFragmentBackStack();
            }
        }
    }

    private void clearFragmentBackStack() {
        FragmentProcess.moveToSpecifyFragment(mActivity.getSupportFragmentManager(), this.getClass().getSimpleName());
    }

    private void initComponent() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        if (LocaleHelper.getLanguage(mActivity).equals("ar")) {
            mBinding.btnBack.setRotation(180);
        }

        if (isSwapTask) { // Swap Task
            mBinding.tvTitle1.setText(getString(R.string.sample_count));
            mBinding.tvTitle2.setText(getString(R.string.bag_count));
            mBinding.btnCall.setVisibility(View.GONE);
            mBinding.btnReject.setVisibility(View.VISIBLE);
        }
        else{ // Medical Task
            mBinding.tvTitle1.setText(getString(R.string.distance));
            mBinding.tvTitle2.setText(getString(R.string.time_for_journey));
            mBinding.btnCall.setVisibility(View.VISIBLE);
            mBinding.btnReject.setVisibility(View.GONE);

            if (UserInfo.getInstance().selectedTask.otp != null) {
                mBinding.otpView.setVisibility(View.VISIBLE);
                mBinding.txtOtp.setText(String.format("%s %s", getString(R.string.please_share_otp), UserInfo.getInstance().selectedTask.otp));
            }
        }
    }

    private void initActions() {
        // Call button
        mBinding.btnCall.setOnClickListener(v -> checkCallPermission());
        // Scan Location ID (Start Button)
        mBinding.btnSamples.setOnClickListener(v -> navigateScreen());
        //Driver reach the location
        mBinding.btnReach.setOnClickListener(view -> reachTheLocation());
        // Back button
        mBinding.btnBack.setOnClickListener(v -> mActivity.back());

        // Reject button
        mBinding.btnReject.setOnClickListener(v -> rejectSwapTask());
    }

    //driver reach the location
    private void reachTheLocation() {
        if(isSwapTask){
            navigateScreen();
        }else{
            reachLocationApi();
        }
    }

    private void reachLocationApi() {
        DriverTask deliverTask = UserInfo.getInstance().selectedTask;
        LogisticsApplication.apiManager.reachFromLocationByDriver(deliverTask)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                    @Override
                    public void onSuccess(@NonNull DefaultResponse response) {
                        hideLoading();

                        if (response.status){
                            navigateScreen();
                        }
                        else{
                            Toast.makeText(mActivity, response.message, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoading();
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        if (getArguments() != null){
            isSwapTask = getArguments().getBoolean(AppConstants.KEY_IS_SWAP);
        }

        if (isSwapTask) {
            SwapTaskData swapTask = UserInfo.getInstance().selectedSwapTask;
            if (swapTask != null){
                mBinding.txtFromLocation.setText(swapTask.task.fromLocationName);
                mBinding.txtToLocation.setText(swapTask.task.toLocationName);

                // Sample count
                if (swapTask.task.taskType.equals("SAMPLE")){
                    mBinding.tvDistance.setText(String.valueOf(swapTask.task.samples.size()));
                }
                else{
                    mBinding.tvDistance.setText(swapTask.task.sampleCount.toString());
                }

                // Bag count(for now set into "0")
                mBinding.tvTime.setText("0");
            }
        }
        else{
            DriverTask deliverTask = UserInfo.getInstance().selectedTask;
            mBinding.txtFromLocation.setText(deliverTask.getFromLocationName());
            mBinding.txtToLocation.setText(deliverTask.getToLocationName());
        }
    }

    // Reject the swap task
    private void rejectSwapTask(){
        try{
            showLoading();
            LogisticsApplication.apiManager.rejectSwapTask(UserInfo.getInstance().selectedSwapTask.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                        @Override
                        public void onSuccess(@NonNull DefaultResponse response) {
                            hideLoading();

                            if (response.status){
//                            EventBus.getDefault().post(new EventBusMessage(EventBusMessage.MessageType.REJECT_SWAP_TASK));
                                // Go to task list page
                                mActivity.back();
                            }
                            else{
                                Toast.makeText(mActivity, response.message, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        catch (Exception e){
            hideLoading();
            e.printStackTrace();
        }
    }

    // Clicked "START" button and go to scan page
    private void navigateScreen(){
        if (isSwapTask) {
            FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new SwapTaskBarcodeScanFragment(), R.id.frameLayout);
        }
        else{ // Medical Task
//            FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new ListTaskScanLocationIdFragment(), R.id.frameLayout);

            // No need to go to LocationBarcode scan page (updated flow)
            UserInfo.getInstance().scannedLocationID    = String.valueOf(UserInfo.getInstance().selectedTask.getFromLocation());
            UserInfo.getInstance().scannedLocationName  = UserInfo.getInstance().selectedTask.getFromLocationName();
            UserInfo.getInstance().fromLocationID       = UserInfo.getInstance().selectedTask.getFromLocation();

            sendLocationInfo();
        }
    }

    private void sendLocationInfo(){
        showLoading();

        int taskId = UserInfo.getInstance().selectedTask.getId();
        int locationId = UserInfo.getInstance().fromLocationID;
        String locationType = "FROM";

        LogisticsApplication.apiManager.sendLocationInfo(taskId, locationId, "", locationType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                    @Override
                    public void onSuccess(@NonNull DefaultResponse defaultResponse) {
                        hideLoading();

                        if (defaultResponse.status){
                            // Go to first task details
                            FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new FirstTaskDetailFragment(), R.id.frameLayout);
                        }
                        else {
                            Toast.makeText(mActivity, defaultResponse.message, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoading();
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void callPhone() {
        String phoneNumber = UserInfo.getInstance().selectedTask.getFromMobile();
        if (!phoneNumber.isEmpty()) {
            String number = String.format("tel:%s", phoneNumber);
            mActivity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
        }
    }

    /**
     * Open the Google Maps
     */
    private void openGoogleMaps(Double latitude, Double longitude){
        try {
            String markerName = UserInfo.getInstance().selectedTask.getFromLocationName();
            String uri = "";

//            String uri = String.format(Locale.ENGLISH, "geo:<%f>,<%f>?q=<%f>,<%f>(%s)", latitude, longitude, latitude, longitude, markerName);

            if (mLocation != null){
                uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", latitude, longitude, markerName);
            }
            else {
                uri = String.format(Locale.ENGLISH, "geo:<%f>,<%f>?q=<%f>,<%f>(%s)", latitude, longitude, latitude, longitude, markerName);
            }

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(mActivity.getPackageManager()) != null) {
                mActivity.startActivity(mapIntent);
            }
            else {
                Toast.makeText(mActivity, mActivity.getResources().getString(R.string.google_maps_install_tint), Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e){
            e.printStackTrace();

            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.google_maps_error), Toast.LENGTH_SHORT).show();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Check the location permission
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(mActivity)
                        .setTitle(mActivity.getResources().getString(R.string.permission_dialog_title))
                        .setMessage(mActivity.getResources().getString(R.string.permission_dialog_content))
                        .setPositiveButton((mActivity.getResources().getString(R.string.ok)), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                            }
                        })
                        .create()
                        .show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            }
        }
    }

    /**
     * Show the pin to From_Location
     */
    private void showFromLocationPin(){
        if (UserInfo.getInstance().selectedTask != null && !isSwapTask){
            if (mFromLocationMarker != null){
                mFromLocationMarker.remove();
            }

            // Place the from location marker
            LatLng latLng = new LatLng(UserInfo.getInstance().selectedTask.getFromLocationLat(), UserInfo.getInstance().selectedTask.getFromLocationLng());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(UserInfo.getInstance().selectedTask.getFromLocationName());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
            mFromLocationMarker = mGoogleMap.addMarker(markerOptions);
            mFromLocationMarker.setTag("from");
        }
    }

    /**
     * Check the Call permission
     */
    private void checkCallPermission() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_CODE);
        } else {
            callPhone();
        }
    }

    // Calculate the distance between A and B
    private void calculateDistance() {
        Location fromLocation = new Location("Point A");
        fromLocation.setLatitude(UserInfo.getInstance().selectedTask.getFromLocationLat());
        fromLocation.setLongitude(UserInfo.getInstance().selectedTask.getFromLocationLng());

        float distance = fromLocation.distanceTo(mLocation) / 1000.0f;
        mBinding.tvDistance.setText(String.format(Locale.ENGLISH, "%.2f KM", distance));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);

                showFromLocationPin();
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CALL_PHONE_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    callPhone();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                 return;
            }
            case LOCATION_PERMISSION_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    if (mGoogleMap != null) {
                        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            if (mGoogleApiClient != null) {
                                buildGoogleApiClient();
                            }

                            mGoogleMap.setMyLocationEnabled(true);
                        }
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * LocationListener
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLocation = location;
        UserInfo.getInstance().driverLocation = location;

        if (!isLoadMyLocation) {
            isLoadMyLocation = true;

            showMarker(location);

            // Calculate the distance
            if (!isSwapTask)
                calculateDistance();
        }
    }

    // Show the marker to current location
    private void showMarker(Location location){
        if (mCurrLocationMarker != null){
            mCurrLocationMarker.remove();
        }

        // Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(mActivity.getResources().getString(R.string.current_location));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        mCurrLocationMarker.setTag("mine");

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
    }

    /**
     * GoogleApiClient.ConnectionCallbacks
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(TimeUnit.SECONDS.toMillis(1));
        mLocationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(1));
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * GoogleApiClient.OnConnectionFailedListener
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * OnMarkerClickListener
     */
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker.getTag().toString().equalsIgnoreCase("from")){
            // Open the GoogleMaps
            openGoogleMaps(marker.getPosition().latitude, marker.getPosition().longitude);
        }

        return false;
    }
}
