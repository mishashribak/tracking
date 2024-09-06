package com.blazma.logistics.fragments.main.list_task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.adapter.SpinCommonAdapter;
import com.blazma.logistics.custom.CustomConfirmDialog;
import com.blazma.logistics.databinding.FragmentListTaskFirstSampleInfoBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.task.DefaultResponse;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ListTaskFirstSampleInfoFragment extends BaseFragment {
    private String TAG = ListTaskFirstSampleInfoFragment.class.getSimpleName();
    private FragmentListTaskFirstSampleInfoBinding mBinding;
    private int mSelectedRadioIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_task_first_sample_info, container, false);

        this.initComponent();
        this.initActions();

        View view = mBinding.getRoot();
        return view;
    }

    private void initComponent(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")) {
            mBinding.btnBack.setRotation(180);
        }

        List<String> sampleTypeList = new ArrayList<>();
        sampleTypeList.add("Tubes");
        sampleTypeList.add("Body Fluids");
        sampleTypeList.add("Swabs");
        sampleTypeList.add("UBT");
        sampleTypeList.add("Blood Bags");
        sampleTypeList.add("Others");

        SpinCommonAdapter adapter = new SpinCommonAdapter(mActivity, R.layout.item_spinner_temperature, sampleTypeList);
        mBinding.spnSampleType.setAdapter(adapter);

        updateRadioGroup();
    }

    private void initActions() {

        // Radio Buttons action
        mBinding.radio1.setOnClickListener(v -> {
            mSelectedRadioIndex = 0;
            updateRadioGroup();
        });
        mBinding.radio2.setOnClickListener(v -> {
            mSelectedRadioIndex = 1;
            updateRadioGroup();
        });
        mBinding.radio3.setOnClickListener(v -> {
            mSelectedRadioIndex = 2;
            updateRadioGroup();
        });

        // Scan barcode
        mBinding.btnScanBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedRadioIndex != -1){
                    FragmentProcess.removeCurrentFragment(mActivity.getSupportFragmentManager());

                    UserInfo.getInstance().selectedContainerType = mSelectedRadioIndex;
                    UserInfo.getInstance().selectedSampleType = mBinding.spnSampleType.getSelectedItem().toString();
                    ListTaskBarcodeScanFragment reviewScanFragment = new ListTaskBarcodeScanFragment();
                    FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), reviewScanFragment, R.id.frameLayout);
                } else{
                    Toast.makeText(mActivity, getResources().getString(R.string.msg_select_temperature), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBinding.btnNoSample.setOnClickListener(v -> {
            CustomConfirmDialog dlg = new CustomConfirmDialog(mActivity, getString(R.string.msg_sure_no_sample), getString(R.string.yes), getString(R.string.no), new CustomConfirmDialog.ItemClickInterface() {
                @Override
                public void onOK() {
                    callNoSampleApi();
                }
            });
            dlg.showDialog();
            dlg.setCanceledOnTouchOutside(false);
        });

        // Back
        mBinding.btnBack.setOnClickListener(v -> {
            mActivity.back();
        });
    }

    private void updateRadioGroup(){
        switch (mSelectedRadioIndex){
            case -1:
                mBinding.imageRadio1.setImageResource(R.drawable.ic_default_radio);
                mBinding.imageRadio2.setImageResource(R.drawable.ic_default_radio);
                mBinding.imageRadio3.setImageResource(R.drawable.ic_default_radio);
                break;
            case 0:
                mBinding.imageRadio1.setImageResource(R.drawable.ic_big_green_checked);
                mBinding.imageRadio2.setImageResource(R.drawable.ic_default_radio);
                mBinding.imageRadio3.setImageResource(R.drawable.ic_default_radio);
                break;
            case 1:
                mBinding.imageRadio1.setImageResource(R.drawable.ic_default_radio);
                mBinding.imageRadio2.setImageResource(R.drawable.ic_big_green_checked);
                mBinding.imageRadio3.setImageResource(R.drawable.ic_default_radio);
                break;
            case 2:
                mBinding.imageRadio1.setImageResource(R.drawable.ic_default_radio);
                mBinding.imageRadio2.setImageResource(R.drawable.ic_default_radio);
                mBinding.imageRadio3.setImageResource(R.drawable.ic_big_green_checked);
                break;
        }
    }

    private void callNoSampleApi(){
        showLoading();

        LogisticsApplication.apiManager.noSample(UserInfo.getInstance().selectedTask.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                    @Override
                    public void onSuccess(@NonNull DefaultResponse tasksResponse) {
                        hideLoading();

                        if (tasksResponse.status){
                            FragmentProcess.removeCurrentFragment(mActivity.getSupportFragmentManager());
                            DriverSignatureFragment driverSignatureFragment = new DriverSignatureFragment();
                            FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), driverSignatureFragment, R.id.frameLayout);
                        }
                        else{
                            Toast.makeText(mActivity, tasksResponse.message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoading();
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

