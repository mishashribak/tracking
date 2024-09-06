package com.blazma.logistics.fragments.main.common;

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
import com.blazma.logistics.databinding.FragmentTaskDetailBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.freezer_placement.FreezerScanBagBarcodeFragment;
import com.blazma.logistics.fragments.main.freezer_placement.FreezerScanContainerBarcodeFragment;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.freezer.SampleItemModel;
import com.blazma.logistics.model.freezer.SampleResponse;
import com.blazma.logistics.model.task.ContainerListResponse;
import com.blazma.logistics.model.task.ContainerModel;
import com.blazma.logistics.model.task.DefaultResponse;
import com.blazma.logistics.model.task.SampleBarCodeModel;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class TaskDetailFragment extends BaseFragment {
    private String TAG = TaskDetailFragment.class.getSimpleName();
    private FragmentTaskDetailBinding mBinding;
    private CommonBarCodeAdapter commonBarCodeAdapter;
    private List<SampleBarCodeModel> sampleList = new ArrayList<>();
    private List<ContainerModel> containerList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_detail, container, false);

        this.initActions();
        this.initComponent();

        View view = mBinding.getRoot();
        return view;
    }

    private void initComponent(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }

        if(UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_COLLECTED){
            mBinding.btnSelectBarCode.setText(R.string.add_new_bag);
            mBinding.btnSelectContainer.setText(R.string.scan_freezer);
            mBinding.btnSubmit.setText(R.string.save_bag);
            mBinding.btnComplete.setText(R.string.close_freezers);
            mBinding.btnSelectContainer.setVisibility(View.GONE);

            if(UserInfo.getInstance().scannedContainerType.isEmpty()){
                mBinding.btnSubmit.setVisibility(View.GONE);
                mBinding.btnComplete.setVisibility(View.GONE);
                mBinding.btnSelectBarCode.setVisibility(View.INVISIBLE);
            }
        }
        else if(UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_IN_FREEZER) {
            mBinding.btnSelectContainer.setText(R.string.scan_container);
            mBinding.btnSelectBarCode.setText(R.string.scan_bag_barcode);
            mBinding.btnSubmit.setText(R.string.remove_samples);
            mBinding.btnComplete.setText(R.string.close_freezers);
        }

        if(UserInfo.getInstance().scannedBagBarCode.isEmpty()){
            mBinding.groupActionBtns.setVisibility(View.GONE);
            mBinding.viewBarCodeList.setVisibility(View.GONE);
            mBinding.txtTotalBarcodes.setVisibility(View.GONE);

            if(UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_COLLECTED){
                mBinding.btnSelectContainer.setVisibility(View.VISIBLE);
                mBinding.btnSelectBarCode.setVisibility(View.VISIBLE);
                mBinding.btnComplete.setVisibility(View.GONE);
            } else {
                mBinding.btnSelectContainer.setVisibility(View.VISIBLE);
                mBinding.btnSelectBarCode.setVisibility(View.GONE);
            }
        } else {
            mBinding.groupActionBtns.setVisibility(View.VISIBLE);
            mBinding.btnSelectContainer.setVisibility(View.VISIBLE);
            mBinding.viewBarCodeList.setVisibility(View.VISIBLE);
            mBinding.txtTotalBarcodes.setVisibility(View.VISIBLE);

            if(UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_COLLECTED){
                mBinding.btnSelectContainer.setVisibility(View.GONE);
                mBinding.btnComplete.setVisibility(View.VISIBLE);
            } else {
                mBinding.btnSelectContainer.setVisibility(View.VISIBLE);
            }

            this.initContainerView();
            this.initListView();
        }

        if(UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_COLLECTED){
            mBinding.spContainer.setVisibility(View.VISIBLE);
            mBinding.txtTemperature.setVisibility(View.VISIBLE);
            mBinding.spnContainer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mBinding.spnContainer.setSelection(0);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            mBinding.spContainer.setVisibility(View.GONE);
            mBinding.txtTemperature.setVisibility(View.GONE);
        }
    }

    private void initActions() {

        mBinding.btnSelectBarCode.setOnClickListener(v -> {
            FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new FreezerScanBagBarcodeFragment(), R.id.frameLayout);
        });

        mBinding.btnSelectContainer.setOnClickListener(v -> {
            FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new FreezerScanContainerBarcodeFragment(), R.id.frameLayout);
        });

        mBinding.btnSubmit.setOnClickListener(v -> {
            if(UserInfo.getInstance().selectedTaskType.equals(AppConstants.TASK_STATUS_COLLECTED)){

                if(UserInfo.getInstance().scannedContainerId == 0 || UserInfo.getInstance().scannedContainerType.isEmpty()){
                    Toast.makeText(mActivity, getString(R.string.msg_scan_container_barcode), Toast.LENGTH_SHORT).show();
                } else {
                    CustomConfirmDialog dlg = new CustomConfirmDialog(mActivity, getString(R.string.msg_sure_submit_samples), getString(R.string.yes), getString(R.string.no), new CustomConfirmDialog.ItemClickInterface() {
                        @Override
                        public void onOK() {
                            callSubmitSamples();
                        }
                    });
                    dlg.showDialog();
                    dlg.setCanceledOnTouchOutside(false);
                }
            } else {
                CustomConfirmDialog dlg = new CustomConfirmDialog(mActivity, getString(R.string.msg_sure_remove_samples), getString(R.string.yes), getString(R.string.no), new CustomConfirmDialog.ItemClickInterface() {
                    @Override
                    public void onOK() {
                        callRemoveBag();
                    }
                });
                dlg.showDialog();
                dlg.setCanceledOnTouchOutside(false);
            }
        });

        mBinding.btnComplete.setOnClickListener(v -> {
            if(UserInfo.getInstance().selectedTaskType.equals(AppConstants.TASK_STATUS_COLLECTED)){
                CustomConfirmDialog dlg = new CustomConfirmDialog(mActivity, getString(R.string.msg_sure_close_freezers), getString(R.string.yes), getString(R.string.no), new CustomConfirmDialog.ItemClickInterface() {
                    @Override
                    public void onOK() {
                        callCloseFreezers();
                    }
                });
                dlg.showDialog();
                dlg.setCanceledOnTouchOutside(false);
            } else {
                CustomConfirmDialog dlg = new CustomConfirmDialog(mActivity, getString(R.string.msg_sure_close_freezers), getString(R.string.yes), getString(R.string.no), new CustomConfirmDialog.ItemClickInterface() {
                    @Override
                    public void onOK() {
                        callFreezerOut();
                    }
                });
                dlg.showDialog();
                dlg.setCanceledOnTouchOutside(false);
            }
        });

        mBinding.btnBack.setOnClickListener(v -> {
            UserInfo.getInstance().scannedContainerId = 0;
            UserInfo.getInstance().scannedContainerType = "";
            UserInfo.getInstance().scannedBagBarCode = "";
            EventBus.getDefault().post(new EventBusMessage(EventBusMessage.MessageType.GO_TO_FREEZER_PLACEMENT));
        });
    }

    private void initContainerView(){
        int taskId = UserInfo.getInstance().selectedTask.getId();
        String bagCode = UserInfo.getInstance().scannedBagBarCode;
        showLoading();

        LogisticsApplication.apiManager.getContainersOfTask(taskId, bagCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<ContainerListResponse>() {
                    @Override
                    public void onSuccess(@NonNull ContainerListResponse containerListResponse) {
                        hideLoading();

                        if (containerListResponse.status){
                            containerList = new ArrayList<>();
                            List<String> valueList = new ArrayList<String>();
                            for (ContainerModel item : containerListResponse.containers){
                                if(item.isCorrectContainer){
                                    containerList.add(0, item);
                                    valueList.add(0, item.type);
                                } else {
                                    containerList.add(item);
                                    valueList.add(item.type);
                                }
                            }
                            SpinCommonAdapter adapter = new SpinCommonAdapter(mActivity, R.layout.item_spinner_temperature, valueList);
                            mBinding.spnContainer.setAdapter(adapter);
                        }
                        else{
                            Toast.makeText(mActivity, containerListResponse.message, Toast.LENGTH_SHORT).show();
                        }

                        checkIfCorrectContainerSelected();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoading();
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initListView(){
        if(commonBarCodeAdapter == null){
            commonBarCodeAdapter = new CommonBarCodeAdapter(mActivity, false, false, new CommonBarCodeAdapter.OnItemClickListener() {
                @Override
                public void onClickDelete(int position) {
                }
            });
        }

        mBinding.rvListBarCodes.setAdapter(commonBarCodeAdapter);
    }

    private void getData(){
        int taskId = UserInfo.getInstance().selectedTask.getId();
        String bagCode = UserInfo.getInstance().scannedBagBarCode;
        String containerType = UserInfo.getInstance().scannedContainerType;

        if (containerType.isEmpty()){
            return;
        }

        showLoading();

        LogisticsApplication.apiManager.getSamplesOfBagWithType(taskId, bagCode, containerType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<SampleResponse>() {
                    @Override
                    public void onSuccess(@NonNull SampleResponse sampleResponse) {
                        hideLoading();

                        if (sampleResponse.status){
                            List<SampleBarCodeModel> barCodeList = new ArrayList<>();
                            for (SampleItemModel item : sampleResponse.samples){
                                barCodeList.add(new SampleBarCodeModel(item.barcodeId, item.type, ""));
                            }
                            setData(barCodeList);
                        }
                        else{
                            setData(new ArrayList<>());
                            Toast.makeText(mActivity, sampleResponse.message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoading();
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setData(List<SampleBarCodeModel> data){
        mBinding.txtTotalBarcodes.setText(String.format(mActivity.getResources().getString(R.string.total_barcodes), data.size()));
        sampleList = data;
        commonBarCodeAdapter.setData(data);
    }

    private void checkIfCorrectContainerSelected(){
        if(UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_COLLECTED){
            if(UserInfo.getInstance().scannedContainerId != 0){
                if(containerList.size() > 0 && containerList.get(0).isCorrectContainer && (containerList.get(0).id == UserInfo.getInstance().scannedContainerId)){
                    this.getData();
                } else {
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.msg_scan_correct_container), Toast.LENGTH_SHORT).show();
                }
            } else {
            }
        } else {
            this.getData();
        }

    }

    private void callSubmitSamples(){
        if(sampleList.size() == 0){
            return;
        }

        int taskId = UserInfo.getInstance().selectedTask.getId();
        int containerId = UserInfo.getInstance().scannedContainerId;
        String bagCode = UserInfo.getInstance().scannedBagBarCode;

        showLoading();

        LogisticsApplication.apiManager.submitSamples(taskId, containerId, bagCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                    @Override
                    public void onSuccess(@NonNull DefaultResponse defaultResponse) {
                        hideLoading();

                        if (defaultResponse.status){
                            setData(new ArrayList<>());

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.viewResult.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                        else{
                            Toast.makeText(mActivity, defaultResponse.message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoading();
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void callCloseFreezers(){
        int taskId = UserInfo.getInstance().selectedTask.getId();

        showLoading();

        LogisticsApplication.apiManager.closeFreezer(taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                    @Override
                    public void onSuccess(@NonNull DefaultResponse defaultResponse) {
                        hideLoading();

                        if (defaultResponse.status){
                            FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new TaskStatusFragment(), R.id.frameLayout);
                        }
                        else{
                            Toast.makeText(mActivity, defaultResponse.message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoading();
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void callRemoveBag(){
        if(sampleList.size() == 0){
            return;
        }
        if(UserInfo.getInstance().scannedBagBarCode.isEmpty()){
            Toast.makeText(mActivity, getString(R.string.msg_scan_bag_code), Toast.LENGTH_SHORT).show();
        } else {
            int taskId = UserInfo.getInstance().selectedTask.getId();
            int containerId = UserInfo.getInstance().scannedContainerId;
            String bagCode = UserInfo.getInstance().scannedBagBarCode;

            showLoading();

            LogisticsApplication.apiManager.removeBagFromContainer(taskId, bagCode, containerId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                        @Override
                        public void onSuccess(@NonNull DefaultResponse defaultResponse) {
                            hideLoading();

                            if (defaultResponse.status){
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setData(new ArrayList<>());
                                        mBinding.txtResult.setText(getString(R.string.remove_successfully));
                                        mBinding.viewResult.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                            else{
                                Toast.makeText(mActivity, defaultResponse.message, Toast.LENGTH_SHORT).show();
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

    private void callFreezerOut(){
        int taskId = UserInfo.getInstance().selectedTask.getId();

        showLoading();

        LogisticsApplication.apiManager.freezerOut(taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                    @Override
                    public void onSuccess(@NonNull DefaultResponse defaultResponse) {
                        hideLoading();

                        if (defaultResponse.status){
                            FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new TaskStatusFragment(), R.id.frameLayout);
                        }
                        else{
                            Toast.makeText(mActivity, defaultResponse.message, Toast.LENGTH_SHORT).show();
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
