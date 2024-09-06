package com.blazma.logistics.fragments.main.list_task;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.adapter.CommonBarCodeAdapter;
import com.blazma.logistics.adapter.SpinCommonAdapter;
import com.blazma.logistics.custom.CustomConfirmDialog;
import com.blazma.logistics.databinding.FragmentListTaskSecondSampleInfoBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.task.DefaultResponse;
import com.blazma.logistics.model.task.SampleBarCodeModel;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ListTaskSecondSampleInfoFragment extends BaseFragment {
    private String TAG = ListTaskSecondSampleInfoFragment.class.getSimpleName();
    private FragmentListTaskSecondSampleInfoBinding mBinding;
    private CommonBarCodeAdapter commonBarCodeAdapter;
    private String selectedContainer = AppConstants.CONTAINER_ROOM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_task_second_sample_info, container, false);

        this.initComponent();
        this.initActions();
        this.setData();

        return mBinding.getRoot();
    }

    private void initComponent(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")) {
            mBinding.btnBack.setRotation(180);
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private void initActions() {

        // Scan barcode
        mBinding.btnScanBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo.getInstance().selectedContainerType = mBinding.spnTemperature.getSelectedItemPosition();

//                ListTaskBarcodeScanFragment reviewScanFragment = new ListTaskBarcodeScanFragment();
                ListTaskFirstSampleInfoFragment firstSampleInfoFragment = new ListTaskFirstSampleInfoFragment();
                FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), firstSampleInfoFragment, R.id.frameLayout);
            }
        });

        mBinding.btnSendCodes.setOnClickListener(v -> {
            if(UserInfo.getInstance().scannedBarCodes.size() > 0 && UserInfo.getInstance().scannedLocationID != "")
            callSendBarCodeApi();
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

        mBinding.btnFinish.setOnClickListener(v -> {
            callFinishApi();
        });

        // Back
        mBinding.btnBack.setOnClickListener(v -> {
            // Post the action to pop the fragments in Backstack
            EventBus.getDefault().post(new EventBusMessage(EventBusMessage.MessageType.GO_TO_MEDICAL_TASK));
        });
    }

    private void setBarcodeTotal(){
        // Total Count
        int barCodeCount = 0;
        for (SampleBarCodeModel barcode : UserInfo.getInstance().scannedBarCodes) {
            barCodeCount += barcode.count;
        }
        mBinding.txtTotalBarcodes.setText(String.format(mActivity.getResources().getString(R.string.total_barcodes), barCodeCount));
    }

    private void setData(){
        // Total barcode count
        setBarcodeTotal();

        // Container Dropdown
        List<String> containerList = Arrays.asList(getResources().getStringArray(R.array.container_list));
        SpinCommonAdapter adapter = new SpinCommonAdapter(mActivity, R.layout.item_spinner_temperature, containerList);
        mBinding.spnTemperature.setAdapter(adapter);
        mBinding.spnTemperature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: selectedContainer = AppConstants.CONTAINER_ROOM; break;
                    case 1: selectedContainer = AppConstants.CONTAINER_REFRIGERATE; break;
                    case 2: selectedContainer = AppConstants.CONTAINER_FROZEN; break;
                }

                loadBarCodeList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Bar Code List
        if(commonBarCodeAdapter == null){
            commonBarCodeAdapter = new CommonBarCodeAdapter(mActivity, false, false, new CommonBarCodeAdapter.OnItemClickListener() {
                @Override
                public void onClickDelete(int position) {
                    UserInfo.getInstance().removeBarCode(position);
                    loadBarCodeList();
                }
            });
        }

        mBinding.rvListBarCodes.setAdapter(commonBarCodeAdapter);
        this.loadBarCodeList();
    }

    private void loadBarCodeList(){
        List<SampleBarCodeModel> filteredBarCodes = new ArrayList<>();
        List<SampleBarCodeModel> allScannedBarCodes = UserInfo.getInstance().scannedBarCodes;

        switch (this.selectedContainer){
            case AppConstants.CONTAINER_ROOM:
                for(SampleBarCodeModel item : allScannedBarCodes){
                    if(item.container.equals(AppConstants.CONTAINER_ROOM)){
                        filteredBarCodes.add(item);
                    }
                };
                break;
            case AppConstants.CONTAINER_REFRIGERATE:
                for(SampleBarCodeModel item : allScannedBarCodes){
                    if(item.container.equals(AppConstants.CONTAINER_REFRIGERATE)){
                        filteredBarCodes.add(item);
                    }
                };
                break;
            case AppConstants.CONTAINER_FROZEN:
                for(SampleBarCodeModel item : allScannedBarCodes){
                    if(item.container.equals(AppConstants.CONTAINER_FROZEN)){
                        filteredBarCodes.add(item);
                    }
                };
                break;
        }

        commonBarCodeAdapter.setData(allScannedBarCodes);
    }

    private void callSendBarCodeApi(){

        int locationId = Integer.parseInt(UserInfo.getInstance().scannedLocationID);
        int taskId = UserInfo.getInstance().selectedTask.getId();
        String bagCode = UserInfo.getInstance().scannedBagBarCode;

        this.callSendRoomData(taskId, locationId, bagCode);
    }

    // Container type is "ROOM"
    private void callSendRoomData(int taskId, int locationId, String bagCode){
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

            LogisticsApplication.apiManager.sendSamples(taskId, locationId, codeList, AppConstants.CONTAINER_ROOM, bagCode, UserInfo.getInstance().selectedSampleType)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                        @Override
                        public void onSuccess(@NonNull DefaultResponse tasksResponse) {
                            hideLoading();

                            if(tasksResponse.status){
                                UserInfo.getInstance().scannedBarCodes.removeAll(roomBarCodes);
                                loadBarCodeList();

                                callSendRefrigerateData(taskId, locationId, bagCode);
                            }
                            else {
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
        else {
            callSendRefrigerateData(taskId, locationId, bagCode);
        }
    }

    // Container type is "REFRIGERATE"
    private void callSendRefrigerateData(int taskId, int locationId, String bagCode){
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
            LogisticsApplication.apiManager.sendSamples(taskId, locationId, codeList, AppConstants.CONTAINER_REFRIGERATE, bagCode, UserInfo.getInstance().selectedSampleType)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                        @Override
                        public void onSuccess(@NonNull DefaultResponse tasksResponse) {
                            hideLoading();

                            if(tasksResponse.status){
                                UserInfo.getInstance().scannedBarCodes.removeAll(refCodeList);
                                loadBarCodeList();

                                callSendFrozenData(taskId, locationId, bagCode);
                            }
                            else {
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
        else {
            callSendFrozenData(taskId, locationId, bagCode);
        }
    }

    // Container type is "FROZEN"
    private void callSendFrozenData(int taskId, int locationId, String bagCode){
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
            LogisticsApplication.apiManager.sendSamples(taskId, locationId, codeList, AppConstants.CONTAINER_FROZEN, bagCode, UserInfo.getInstance().selectedSampleType)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                        @Override
                        public void onSuccess(@NonNull DefaultResponse tasksResponse) {
                            hideLoading();

                            if(tasksResponse.status){
                                UserInfo.getInstance().scannedBarCodes.removeAll(frozenCodeList);
                                loadBarCodeList();
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
            Toast.makeText(mActivity, getString(R.string.sample_added_successfully), Toast.LENGTH_SHORT).show();
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

    private void callFinishApi(){
        DriverSignatureFragment driverSignatureFragment = new DriverSignatureFragment();
        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), driverSignatureFragment, R.id.frameLayout);
    }
}
