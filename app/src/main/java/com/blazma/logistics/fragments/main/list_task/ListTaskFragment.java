package com.blazma.logistics.fragments.main.list_task;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.adapter.ListTaskAdapter;
import com.blazma.logistics.databinding.FragmentListTaskBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.freezer_placement.FreezerScanBagBarcodeFragment;
import com.blazma.logistics.fragments.main.freezer_placement.FreezerScanContainerBarcodeFragment;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.login.DriverTask;
import com.blazma.logistics.model.task.DefaultResponse;
import com.blazma.logistics.model.task.DriverTasksResponse;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ListTaskFragment extends BaseFragment {
    private String TAG = ListTaskFragment.class.getSimpleName();
    private FragmentListTaskBinding mBinding;
    private ListTaskAdapter listTaskAdapter;

    // Task list
    private List<DriverTask> tasks = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_task, container, false);

        this.initActions();
        this.initComponent();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        return mBinding.getRoot();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFragment(EventBusMessage messageEvent){
        if (messageEvent != null){
            int messageType = messageEvent.getMessageType();
            if (messageType == EventBusMessage.MessageType.GO_TO_FREEZER_PLACEMENT){ // Added new address
                // Clear the backstack
                clearFragmentBackStack();
            }
        }
    }

    private void clearFragmentBackStack(){
        FragmentProcess.moveToSpecifyFragment(mActivity.getSupportFragmentManager(), this.getClass().getSimpleName());
        this.initData();
    }

    private void initComponent(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }

        // Set the title
        if (UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_NEW){
            mBinding.tvToolbarTitle.setText(mActivity.getResources().getString(R.string.pickup_samples));
        }
        else if (UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_COLLECTED){
            mBinding.tvToolbarTitle.setText(mActivity.getResources().getString(R.string.samples_placement));
        }
        else if (UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_IN_FREEZER){
            mBinding.tvToolbarTitle.setText(mActivity.getResources().getString(R.string.samples_pull_out));
        }
        else {
            mBinding.tvToolbarTitle.setText(R.string.drop_off_samples);
        }
    }

    private void initActions() {
        mBinding.btnBack.setOnClickListener(v -> mActivity.back());
        mBinding.swipeRefreshLayout.setOnRefreshListener(this::getTaskList);
        mBinding.tvAcceptAll.setOnClickListener(view -> acceptAllPickupSamplesTasks());
    }

    private void initData(){
        if (UserInfo.getInstance().loginInfo != null){
            getTaskList();
        }
        else{
            Log.d("TaskList", "User information is null");
        }
    }

    /**
     * Get the task list
     */
    @SuppressLint("CheckResult")
    private void getTaskList(){
        try{
            showLoading();

            LogisticsApplication.apiManager.getTaskList(UserInfo.getInstance().loginInfo.getId(), UserInfo.getInstance().selectedTaskType)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<DriverTasksResponse>() {
                        @Override
                        public void onSuccess(@NonNull DriverTasksResponse tasksResponse) {
                            hideLoading();
                            mBinding.swipeRefreshLayout.setRefreshing(false);
                            if (tasksResponse.status){
                                tasks = tasksResponse.tasks;

                                // Refresh the task list
                                setData();
                            }
                            else{
                                Toast.makeText(mActivity, tasksResponse.message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();
                            mBinding.swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e){
            hideLoading();
            e.printStackTrace();
        }
    }


    private void acceptAllPickupSamplesTasks(){
        try{
            showLoading();
           List<Integer> unConfirmedTaskIds = getPendingConfirmTaskIds();
            LogisticsApplication.apiManager.confirmTaskByDriver(unConfirmedTaskIds)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                        @Override
                        public void onSuccess(@NonNull DefaultResponse tasksResponse) {
                            hideLoading();
                            mBinding.swipeRefreshLayout.setRefreshing(false);
                            if (tasksResponse.status){
                                // Refresh the task list
                                getTaskList();
                            }
                            else{
                                Toast.makeText(mActivity, tasksResponse.message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();
                            mBinding.swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e){
            hideLoading();
            e.printStackTrace();
        }
    }

    public List<Integer> getPendingConfirmTaskIds() {
        if(tasks!=null && !tasks.isEmpty()) {
            return tasks.stream().filter(task -> !task.isConfirmedByDriver()).map(DriverTask::getId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private void setData(){
       boolean isTaskConfirmPending = !getPendingConfirmTaskIds().isEmpty();
       if(isTaskConfirmPending){
           mBinding.tvAcceptAll.setVisibility(View.VISIBLE);
       }else {
           mBinding.tvAcceptAll.setVisibility(View.GONE);
       }
        if(listTaskAdapter == null){
            listTaskAdapter = new ListTaskAdapter(mActivity, tasks,isTaskConfirmPending, new ListTaskAdapter.OnTaskItemClickListener() {
                @Override
                public void onClick(DriverTask task) {
                    // Save the selected task to local
                    UserInfo.getInstance().selectedTask = task;

                    if (UserInfo.getInstance().selectedTaskType.equals(AppConstants.TASK_STATUS_NEW)){ // Pickup samples
                        // Go to Map
                        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new ListMapFragment(), R.id.frameLayout);
                    }
                    else if (UserInfo.getInstance().selectedTaskType.equals(AppConstants.TASK_STATUS_COLLECTED)){ // Samples placement
                        // Go to Task Details (Select container)
                        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new FreezerScanBagBarcodeFragment(), R.id.frameLayout);
                    }
                    else if (UserInfo.getInstance().selectedTaskType.equals(AppConstants.TASK_STATUS_IN_FREEZER)){ // Samples Pull out
                        // Go to Task Details (Select container)
                        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new FreezerScanContainerBarcodeFragment(), R.id.frameLayout);
                    }
                    else { // Drop off samples
                        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new ListTaskScanLocationIdFragment(), R.id.frameLayout);
                    }
                }

                @Override
                public void onClickSwap(DriverTask task) {
                    showSwapConfirmDialog(task);
                }

                @Override
                public void onAccept(DriverTask task) {
                    //confirmTaskByDriver(task.getId());
                }
            });
        }

        listTaskAdapter.setData(tasks,isTaskConfirmPending);
        mBinding.rvListTask.setAdapter(listTaskAdapter);

    }

    private void showSwapConfirmDialog(DriverTask task){
        new AlertDialog.Builder(mActivity)
                .setMessage(getResources().getString(R.string.msg_dialog_swap))
                .setPositiveButton((getResources().getString(R.string.yes)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        swapTask(task);
                    }
                })
                .setNegativeButton((getResources().getString(R.string.no)), null)
                .create()
                .show();
    }

    // Create the task for swapping
    private void swapTask(DriverTask task){
        try{
            showLoading();

            LogisticsApplication.apiManager.createSwap(UserInfo.getInstance().loginInfo.getId(), task.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                        @Override
                        public void onSuccess(@NonNull DefaultResponse response) {
                            hideLoading();

                            Toast.makeText(getActivity(), response.message, Toast.LENGTH_LONG).show();
                            if (response.status){
                                // reload the task list
                                getTaskList();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        catch (Exception e){
            hideLoading();
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.initData();
    }
}
