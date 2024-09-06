package com.blazma.logistics.fragments.main.list_task;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.R;
import com.blazma.logistics.databinding.FragmentLocationSampleBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

public class LocationSampleFragment extends BaseFragment {
    private String TAG = LocationSampleFragment.class.getSimpleName();
    private FragmentLocationSampleBinding mBinding;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_location_sample, container, false);
        this.initActions();
        this.initComponent();

        View view = mBinding.getRoot();
        return view;
    }

    private void initComponent(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }
    }

    private void initActions() {
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.back();
            }
        });

        mBinding.btnPerSample.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new ListTaskSecondSampleInfoFragment(), R.id.frameLayout);
            }
        });
    }
}
