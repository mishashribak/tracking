package com.blazma.logistics.fragments.main.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.R;
import com.blazma.logistics.databinding.FragmentMedicalTaskBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.MainFragment;
import com.blazma.logistics.fragments.main.list_task.ListTaskBarcodeScanFragment;
import com.blazma.logistics.fragments.main.list_task.ListTaskFragment;
import com.blazma.logistics.fragments.main.outfreezer.OutFreezerTaskListFragment;
import com.blazma.logistics.fragments.main.infreezer.InFreezerTaskListFragment;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.Type;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

public class TaskTypeFragment extends BaseFragment {
    private String TAG = MainFragment.class.getSimpleName();
    private FragmentMedicalTaskBinding mBinding;
    private int selectedTab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_medical_task, container, false);
        this.initActions();
        this.initComponent();
        this.initData();

        View view = mBinding.getRoot();
        return view;
    }

    private void initComponent(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
            mBinding.btnGoListTask.setRotation(180);
            mBinding.btnGoFreezerPlacement.setRotation(180);
            mBinding.btnGoDropOffSample.setRotation(180);
        }
    }

    private void initActions(){
        // Go Back
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.back();
            }
        });

        mBinding.subViewListTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo.getInstance().selectedTaskType = AppConstants.TASK_STATUS_NEW;
                gotoTaskList();
            }
        });

        mBinding.subViewFreezerPlacement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo.getInstance().selectedTaskType = AppConstants.TASK_STATUS_COLLECTED;
                gotoTaskList();
            }
        });

        mBinding.subViewSamplesPullOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo.getInstance().selectedTaskType = AppConstants.TASK_STATUS_IN_FREEZER;
                goToInFreezerTaskList();
            }
        });

        mBinding.subViewDropOffSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo.getInstance().selectedTaskType = AppConstants.TASK_STATUS_OUT_FREEZER;
                goToOutFreezerTaskList();
            }
        });
    }

    /**
     * Navigate to the Task List page
     */
    private void gotoTaskList(){
        ListTaskFragment listTaskFragment = new ListTaskFragment();

        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), listTaskFragment, R.id.frameLayout);
    }

    // Samples pull out
    private void goToInFreezerTaskList(){
        InFreezerTaskListFragment inFreezerTaskListFragment = new InFreezerTaskListFragment();

        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), inFreezerTaskListFragment, R.id.frameLayout);
    }

    // Drop Off Samples
    private void goToOutFreezerTaskList(){
        OutFreezerTaskListFragment listTaskFragment = new OutFreezerTaskListFragment();

        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), listTaskFragment, R.id.frameLayout);
    }

    private void initData(){
        selectedTab = getArguments().getInt(Type.TAG_SELECTED_TAB_TYPE);
    }
}
