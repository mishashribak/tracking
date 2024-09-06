package com.blazma.logistics.fragments.main;

import static com.blazma.logistics.global.TaskStatus.CLOSED;
import static com.blazma.logistics.global.TaskStatus.COLLECTED;
import static com.blazma.logistics.global.TaskStatus.IN_FREEZER;
import static com.blazma.logistics.global.TaskStatus.NEW;
import static com.blazma.logistics.global.TaskStatus.OUT_FREEZER;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.activities.LoginActivity;
import com.blazma.logistics.activities.MainActivity;
import com.blazma.logistics.activities.NotificationActivity;
import com.blazma.logistics.activities.TermsConditionActivity;
import com.blazma.logistics.adapter.TaskTimeLineAdapter;
import com.blazma.logistics.custom.CustomConfirmDialog;
import com.blazma.logistics.databinding.FragmentMainBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.common.TaskTypeFragment;
import com.blazma.logistics.fragments.main.money.MoneyTransferTaskListFragment;
import com.blazma.logistics.fragments.main.swap.SwapTaskListFragment;
import com.blazma.logistics.global.MyPreferenceManager;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.Type;
import com.blazma.logistics.model.login.LatestTask;
import com.blazma.logistics.model.login.LoginResponse;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainFragment extends BaseFragment {
    private String TAG = MainFragment.class.getSimpleName();
    private FragmentMainBinding mBinding;

    // Timeline adapter
    private TaskTimeLineAdapter timeLineAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        initComponent();
        initActions();
        initUserInfo();

        View view = mBinding.getRoot();
        return view;
    }

    private void initComponent(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnGoMedicalTask.setRotation(180);
            mBinding.btnGoCarTask.setRotation(180);
            mBinding.btnGoPharma.setRotation(180);
            mBinding.btnGoSwap.setRotation(180);
        }

        // Set the horizontal layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mBinding.timelineRecycler.setLayoutManager(layoutManager);
    }

    private void initActions(){
        // Tap the Hamburger button
        mBinding.btnSideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mActivity).toggleSideMenu();
            }
        });

        // Notification
        mBinding.ivNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the notification page
                Intent intent = new Intent(mActivity, NotificationActivity.class);
                startActivity(intent);
            }
        });

        // Logout
        mBinding.ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logout and go to login page
                ((MainActivity) mActivity).logout();
            }
        });

        // Medical Task
        mBinding.tabMedicalTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskTypeFragment taskTypeFragment = new TaskTypeFragment();
                Bundle args = new Bundle();
                args.putInt(Type.TAG_SELECTED_TAB_TYPE, Type.TabType.MedicalTask);
                taskTypeFragment.setArguments(args);

                FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), taskTypeFragment, R.id.frameLayout);
            }
        });

        // Swap
        mBinding.tabSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new SwapTaskListFragment(), R.id.frameLayout);
            }
        });

        // Pharma Task
        mBinding.tabPharmaTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TaskTypeFragment taskTypeFragment = new TaskTypeFragment();
//                Bundle args = new Bundle();
//                args.putInt(Type.TAG_SELECTED_TAB_TYPE, Type.TabType.PharmaTask);
//                taskTypeFragment.setArguments(args);
//
//                FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), taskTypeFragment, R.id.frameLayout);
            }
        });

        // Money Task
        mBinding.tabCarTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new MoneyTransferTaskListFragment(), R.id.frameLayout);
            }
        });
    }

    private void initUserInfo(){
        if(UserInfo.getInstance().loginInfo == null){
            showLoading();

            String username = MyPreferenceManager.getInstance().getString(AppConstants.KEY_USERNAME);
            String password = MyPreferenceManager.getInstance().getString(AppConstants.KEY_PASSWORD);

            LogisticsApplication.apiManager.loginWithPassword(username, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<LoginResponse>() {
                        @Override
                        public void onSuccess(@NonNull LoginResponse loginResponse) {
                            hideLoading();

                            if (loginResponse.getStatus()){
                                // Save the login information
                                UserInfo.getInstance().loginInfo = loginResponse.getData();
                                MyPreferenceManager.getInstance().put(AppConstants.KEY_USER_ID, loginResponse.getData().getId());
                                MyPreferenceManager.getInstance().put(AppConstants.KEY_IS_ACCEPT_TERMS, true);

                                // Update the Task Timeline View
                                setupLastTaskView();
                            }
                            else{
                                Log.d("Main", "Error Message : " + loginResponse.getMessage());
                                Toast.makeText(mActivity, loginResponse.getMessage(), Toast.LENGTH_LONG).show();

                                gotoLoginPage();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();

                            Log.d("Main", "Error Message : " + e.getMessage());
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();

                            gotoLoginPage();
                        }
                    });
        } else {
            // Update the Task Timeline View
            setupLastTaskView();
        }
    }

    /**
     * Go to Login page
     */
    private void gotoLoginPage(){
        MyPreferenceManager.getInstance().put(AppConstants.KEY_IS_LOGGED_IN, false);

        Intent intent = new Intent(mActivity, LoginActivity.class);
        startActivity(intent);
        mActivity.finish();
    }

    /**
     * Set the Last Task information
     */
    private void setupLastTaskView(){
        LatestTask task = UserInfo.getInstance().loginInfo.getLatestTask();
        // Client name
        String name = "";
        if (LocaleHelper.getLanguage(mActivity) == AppConstants.LANGUAGE_ENGLISH){
            name = task.getClientEnglishName();
        }
        else{
            name = task.getClientArabicName();
        }
        mBinding.tvClientName.setText(name);

        // Status
        mBinding.tvStatus.setText(task.getStatus());
        switch (task.getStatus()){
            case AppConstants.TASK_STATUS_NEW:
                setTaskTimeline(NEW.id());
                break;
            case AppConstants.TASK_STATUS_COLLECTED:
                setTaskTimeline(COLLECTED.id());
                break;
            case AppConstants.TASK_STATUS_IN_FREEZER:
            case AppConstants.TASK_STATUS_OUT_FREEZER:
                setTaskTimeline(IN_FREEZER.id());
                break;
            case AppConstants.TASK_STATUS_CLOSED:
                setTaskTimeline(CLOSED.id());
                break;
        }

        // Start date
        mBinding.tvStartDate.setText(task.getTaskDate());

        // From (Place A)
        mBinding.tvFrom.setText(task.getFromLocationName());

        // To (Place B)
        mBinding.tvTo.setText(task.getToLocationName());
    }

    /**
     * Set the task timeline
     */
    private void setTaskTimeline(int status){
        if (status == 0)
            mBinding.thumb.setVisibility(View.VISIBLE);
        else
            mBinding.thumb.setVisibility(View.GONE);

        mBinding.timelineRecycler.post(new Runnable() {
            @Override
            public void run() {
                timeLineAdapter = new TaskTimeLineAdapter(mActivity, status, mBinding.timelineRecycler.getWidth());
                mBinding.timelineRecycler.setAdapter(timeLineAdapter);
            }
        });
    }
}
