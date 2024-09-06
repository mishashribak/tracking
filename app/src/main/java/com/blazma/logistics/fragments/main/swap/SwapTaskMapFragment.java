package com.blazma.logistics.fragments.main.swap;

import android.location.Location;

import com.blazma.logistics.databinding.FragmentSwapTaskMapBinding;
import com.blazma.logistics.databinding.FragmentTaskMapBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class SwapTaskMapFragment extends BaseFragment {
    private final String TAG = SwapTaskMapFragment.class.getSimpleName();

    private FragmentSwapTaskMapBinding mBinding;
    private static final int LOCATION_PERMISSION_CODE = 2004;

    private GoogleMap mGoogleMap;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    private Location mLocation;
    Marker mCurrLocationMarker;
    Marker mFromLocationMarker;

    private boolean isLoadMyLocation = false;
}
