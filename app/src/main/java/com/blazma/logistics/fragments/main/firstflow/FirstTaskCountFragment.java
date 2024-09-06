package com.blazma.logistics.fragments.main.firstflow;

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
import com.blazma.logistics.databinding.FragmentFirstTaskCountBinding;
import com.blazma.logistics.databinding.FragmentFirstTaskDetailBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.list_task.DriverSignatureFragment;
import com.blazma.logistics.fragments.main.list_task.ListTaskFirstSampleInfoFragment;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.task.DefaultResponse;
import com.blazma.logistics.model.task.SampleBarCodeModel;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class FirstTaskCountFragment extends BaseFragment {
    private String TAG = FirstTaskCountFragment.class.getSimpleName();
    private FragmentFirstTaskCountBinding mBinding;
    private int mSelectedRadioIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_first_task_count, container, false);

        this.initComponent();
        this.initActions();

        View view = mBinding.getRoot();
        return view;
    }

    private void initComponent() {
        if (LocaleHelper.getLanguage(mActivity).equals("ar")) {
            mBinding.btnBack.setRotation(180);
        }

        List<String> containerList = Arrays.asList(getResources().getStringArray(R.array.container_list));
        SpinCommonAdapter adapter = new SpinCommonAdapter(mActivity, R.layout.item_spinner_temperature, containerList);
        mBinding.spnSampleType.setEnabled(false);
        mBinding.spnSampleType.setClickable(false);
        mBinding.spnSampleType.setAdapter(adapter);
        mBinding.spnSampleType.setSelection(UserInfo.getInstance().selectedContainerType);
    }

    private void initActions() {
        // Add more boxes
        mBinding.btnAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAddMore();
            }
        });

        // Save box
        mBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSaveBox();
            }
        });

        // Finish Collecting
        mBinding.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFinishCollecting();
            }
        });

        // Back
        mBinding.btnBack.setOnClickListener(v -> {
            mActivity.back();
        });
    }

    private void callAddMore(){
        FirstTaskDetailFragment firstTaskDetailFragment = new FirstTaskDetailFragment();
        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), firstTaskDetailFragment, R.id.frameLayout);

    }

    // Container type is "ROOM"
    private void callSendRoomData(int taskId, int locationId, String boxCount, String sampleCount){
        List<SampleBarCodeModel> allScannedBarCodes = UserInfo.getInstance().scannedBarCodes;
        List<SampleBarCodeModel> roomBarCodes = new ArrayList<>();
        for(SampleBarCodeModel item : allScannedBarCodes){
            if(item.container.equals(AppConstants.CONTAINER_ROOM)){
                roomBarCodes.add(item);
            }
        }

        if(roomBarCodes.size() > 0){
            List<String> codeList = new ArrayList<>();
            for (SampleBarCodeModel item : roomBarCodes){
                int repeatCount = item.count;
                while (repeatCount > 0) {
                    codeList.add(item.code);
                    repeatCount --;
                }
            }

            showLoading();
            LogisticsApplication.apiManager.sendBoxes(taskId, locationId, codeList, AppConstants.CONTAINER_ROOM, "test-bag", UserInfo.getInstance().selectedSampleType, boxCount, sampleCount)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                        @Override
                        public void onSuccess(@NonNull DefaultResponse tasksResponse) {
                            hideLoading();

                            if(tasksResponse.status){
                                callSendRefrigerateData(taskId, locationId, boxCount, sampleCount);
                            } else {
                                Toast.makeText(mActivity, tasksResponse.message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            callSendRefrigerateData(taskId, locationId, boxCount, sampleCount);
        }
    }

    // Container type is "REFRIGERATE"
    private void callSendRefrigerateData(int taskId, int locationId, String boxCount, String sampleCount){
        List<SampleBarCodeModel> allScannedBarCodes = UserInfo.getInstance().scannedBarCodes;
        List<SampleBarCodeModel> refCodeList = new ArrayList<>();
        for(SampleBarCodeModel item : allScannedBarCodes){
            if(item.container.equals(AppConstants.CONTAINER_REFRIGERATE)){
                refCodeList.add(item);
            }
        }

        if(refCodeList.size() > 0){
            List<String> codeList = new ArrayList<>();
            for (SampleBarCodeModel item : refCodeList){
                int repeatCount = item.count;
                while (repeatCount > 0) {
                    codeList.add(item.code);
                    repeatCount --;
                }
            }
            showLoading();

            LogisticsApplication.apiManager.sendBoxes(taskId, locationId, codeList, AppConstants.CONTAINER_REFRIGERATE, "test-bag", UserInfo.getInstance().selectedSampleType, boxCount, sampleCount)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                        @Override
                        public void onSuccess(@NonNull DefaultResponse tasksResponse) {
                            hideLoading();

                            if(tasksResponse.status){
                                callSendFrozenData(taskId, locationId, boxCount, sampleCount);
                            } else {
                                Toast.makeText(mActivity, tasksResponse.message, Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            callSendFrozenData(taskId, locationId, boxCount, sampleCount);
        }
    }

    // Container type is "FROZEN"
    private void callSendFrozenData(int taskId, int locationId, String boxCount, String sampleCount){
        List<SampleBarCodeModel> allScannedBarCodes = UserInfo.getInstance().scannedBarCodes;
        List<SampleBarCodeModel> frozenCodeList = new ArrayList<>();
        for(SampleBarCodeModel item : allScannedBarCodes){
            if(item.container.equals(AppConstants.CONTAINER_FROZEN)){
                frozenCodeList.add(item);
            }
        }

        if(frozenCodeList.size() > 0){
            List<String> codeList = new ArrayList<>();
            for (SampleBarCodeModel item : frozenCodeList){
                int repeatCount = item.count;
                while (repeatCount > 0) {
                    codeList.add(item.code);
                    repeatCount --;
                }
            }

            showLoading();
            LogisticsApplication.apiManager.sendBoxes(taskId, locationId, codeList, AppConstants.CONTAINER_FROZEN, "test-bag", UserInfo.getInstance().selectedSampleType, boxCount, sampleCount)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                        @Override
                        public void onSuccess(@NonNull DefaultResponse tasksResponse) {
                            hideLoading();

                            if(tasksResponse.status){
                                UserInfo.getInstance().scannedBarCodes = new ArrayList<>();
                                Toast.makeText(mActivity, getString(R.string.sample_added_successfully), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mActivity, tasksResponse.message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            UserInfo.getInstance().scannedBarCodes = new ArrayList<>();
            Toast.makeText(mActivity, getString(R.string.sample_added_successfully), Toast.LENGTH_SHORT).show();
        }
    }

    private void callSaveBox(){
        if(mBinding.inputBoxCount.getText().toString().isEmpty()){
            Toast.makeText(mActivity, "Please enter box count", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mBinding.inputSampleCount.getText().toString().isEmpty()){
            Toast.makeText(mActivity, "Please enter sample count", Toast.LENGTH_SHORT).show();
            return;
        }

        String boxCount = mBinding.inputBoxCount.getText().toString();
        String sampleCount = mBinding.inputSampleCount.getText().toString();

        int locationId = Integer.parseInt(UserInfo.getInstance().scannedLocationID);
        int taskId = UserInfo.getInstance().selectedTask.getId();

        callSendRoomData(taskId, locationId, boxCount, sampleCount);
    }

    private void callFinishCollecting(){

        if(mBinding.inputBoxCount.getText().toString().isEmpty()){
            Toast.makeText(mActivity, "Please enter box count", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mBinding.inputSampleCount.getText().toString().isEmpty()){
            Toast.makeText(mActivity, "Please enter sample count", Toast.LENGTH_SHORT).show();
            return;
        }

        int boxCount = Integer.parseInt(mBinding.inputBoxCount.getText().toString());
        int sampleCount = Integer.parseInt(mBinding.inputSampleCount.getText().toString());
        UserInfo.getInstance().boxCount = boxCount;
        UserInfo.getInstance().sampleCount = sampleCount;

        DriverSignatureFragment driverSignatureFragment = new DriverSignatureFragment();
        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), driverSignatureFragment, R.id.frameLayout);
    }
}