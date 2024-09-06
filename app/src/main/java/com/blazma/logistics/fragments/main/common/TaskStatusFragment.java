package com.blazma.logistics.fragments.main.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.databinding.FragmentTaskStatusBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.task.DefaultResponse;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.LocaleHelper;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class TaskStatusFragment extends BaseFragment {
    private String TAG = TaskStatusFragment.class.getSimpleName();
    private FragmentTaskStatusBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_status, container, false);
        this.initActions();
        this.initComponent();

        View view = mBinding.getRoot();
        return view;
    }

    private void initComponent(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }

        // Hide the Back button
        mBinding.btnBack.setVisibility(View.GONE);

        if(UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_COLLECTED){
            mBinding.txtDescription.setText(R.string.samples_added_to_container);
        } else {
            mBinding.txtDescription.setText(R.string.task_completed_successfully);
        }
    }

    private void initActions() {

        mBinding.btnDone.setOnClickListener(v -> {
            UserInfo.getInstance().scannedContainerId = 0;
            UserInfo.getInstance().scannedContainerType = "";
            UserInfo.getInstance().scannedBagBarCode = "";
            // Set the title
            if (UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_OUT_FREEZER){
                EventBus.getDefault().post(new EventBusMessage(EventBusMessage.MessageType.GO_TO_FREEZER_OUT));
            }
            else if (UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_IN_FREEZER) {
                EventBus.getDefault().post(new EventBusMessage(EventBusMessage.MessageType.GO_TO_SAMPLES_PULL_OUT));
            }
            else {
                EventBus.getDefault().post(new EventBusMessage(EventBusMessage.MessageType.GO_TO_FREEZER_PLACEMENT));
            }
        });
    }
}
