package com.blazma.logistics.fragments.main.money;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.adapter.MoneyTransferTaskListAdapter;
import com.blazma.logistics.databinding.FragmentMoneyTransferListBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.money.MoneyTransferResponse;
import com.blazma.logistics.model.money.MoneyTransferTask;
import com.blazma.logistics.model.swap.SwapTaskResponse;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MoneyTransferTaskListFragment extends BaseFragment {
    private final String TAG = MoneyTransferTaskListFragment.class.getSimpleName();
    private FragmentMoneyTransferListBinding mBinding;

    // Task list
    private List<MoneyTransferTask> tasks = new ArrayList<>();

    private MoneyTransferTaskListAdapter taskListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_money_transfer_list, container, false);

        initViews();

        // Register the EventBus Listener
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        // Get the task list
        getMoneyTransferTasks();

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void initViews(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }

        // Set the title
        mBinding.tvToolbarTitle.setText(R.string.pickup_money);

        // Back button
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.back();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFragment(EventBusMessage messageEvent){
        if (messageEvent != null){
            int messageType = messageEvent.getMessageType();
            if(messageType == EventBusMessage.MessageType.GO_TO_MONEY_TRANSFER){
                // Reload the task list
                clearFragmentBackStack();
            }
        }
    }

    private void clearFragmentBackStack(){
        FragmentProcess.moveToSpecifyFragment(mActivity.getSupportFragmentManager(), this.getClass().getSimpleName());
        getMoneyTransferTasks();
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    /**
     * Get the money transfer tasks
     */
    private void getMoneyTransferTasks(){
        if (UserInfo.getInstance().loginInfo != null){
            showLoading();

            LogisticsApplication.apiManager.getMoneyTransferTaskList(UserInfo.getInstance().loginInfo.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<MoneyTransferResponse>() {
                        @Override
                        public void onSuccess(@NonNull MoneyTransferResponse tasksResponse) {
                            hideLoading();

                            if (tasksResponse.status){
                                tasks = tasksResponse.tasks;

                                // Refresh the task list
                                setData();
                            }
                            else{
                                Toast.makeText(mActivity, tasksResponse.message, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else{
            Log.e("MoneyTaskList", "User information is null");
        }
    }

    private void setData(){
        if (taskListAdapter == null) {
            taskListAdapter = new MoneyTransferTaskListAdapter(mActivity, tasks, new MoneyTransferTaskListAdapter.OnMoneyTaskItemClickListener() {
                @Override
                public void onClick(MoneyTransferTask task) {
                    // Go to Otp page
                    UserInfo.getInstance().selectedMoneyTask = task;
                    FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new FromLocationVerifyOtpFragment(), R.id.frameLayout);
                }
            });
        }

        taskListAdapter.setData(tasks);
        mBinding.rvListTask.setAdapter(taskListAdapter);
    }
}
