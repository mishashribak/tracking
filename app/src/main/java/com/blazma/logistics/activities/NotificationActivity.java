package com.blazma.logistics.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.adapter.ListTaskAdapter;
import com.blazma.logistics.adapter.NotificationAdapter;
import com.blazma.logistics.databinding.ActivityNotificationBinding;
import com.blazma.logistics.fragments.main.common.TaskDetailFragment;
import com.blazma.logistics.fragments.main.list_task.ListMapFragment;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.login.DriverTask;
import com.blazma.logistics.model.notification.NotificationModel;
import com.blazma.logistics.model.notification.NotificationResponse;
import com.blazma.logistics.model.task.DriverTasksResponse;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.FragmentProcess;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class NotificationActivity extends BaseActivity {
    ActivityNotificationBinding mBinding;
    Context mContext;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        layoutBinding();
        initActions();
        getNotifications();
    }

    private void layoutBinding(){
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
    }

    private void initActions(){
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getNotifications(){
        if (UserInfo.getInstance().loginInfo != null){
            showLoading();

            LogisticsApplication.apiManager.getNotifications(UserInfo.getInstance().loginInfo.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<NotificationResponse>() {
                        @Override
                        public void onSuccess(@NonNull NotificationResponse response) {
                            hideLoading();

                            if (response.status){
                                setData(response.notifications);
                            } else{
                                Toast.makeText(mContext, response.message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Log.e("Notifications", "User information is null");
        }
    }

    private void setData(List<NotificationModel> dataList){
        if(notificationAdapter == null){
            notificationAdapter = new NotificationAdapter(mContext, dataList);
        } else {
            notificationAdapter.setData(dataList);
        }

        mBinding.rvNotification.setAdapter(notificationAdapter);
    }
}
