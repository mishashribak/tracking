package com.blazma.logistics.fragments.main.swap;

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
import com.blazma.logistics.adapter.SwapTaskListAdapter;
import com.blazma.logistics.databinding.FragmentListTaskSwapBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.list_task.ListMapFragment;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.swap.SwapTaskData;
import com.blazma.logistics.model.swap.SwapTaskResponse;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SwapTaskListFragment extends BaseFragment {

    private final String TAG = SwapTaskListFragment.class.getSimpleName();
    private FragmentListTaskSwapBinding mBinding;
    private SwapTaskListAdapter taskListAdapter;

    // Task list
    private List<SwapTaskData> tasks = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_task_swap, container, false);

        initViews();
        getSwapTasks();

        // Register the EventBus Listener
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        return mBinding.getRoot();
    }

    private void initViews(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }

        // Set the title
        mBinding.tvToolbarTitle.setText(R.string.pickup_samples);

        // Back button
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.back();
            }
        });
    }

    // Load the task list
    private void getSwapTasks(){
        if (UserInfo.getInstance().loginInfo != null){
            showLoading();

            LogisticsApplication.apiManager.getSwapTaskList(UserInfo.getInstance().loginInfo.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<SwapTaskResponse>() {
                        @Override
                        public void onSuccess(@NonNull SwapTaskResponse tasksResponse) {
                            hideLoading();

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
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Log.e("SwapList", "User information is null");
        }
    }

    private void setData(){
        if(taskListAdapter == null){
            taskListAdapter = new SwapTaskListAdapter(mActivity, tasks, new SwapTaskListAdapter.OnSwapTaskItemClickListener() {
                @Override
                public void onClick(SwapTaskData task) {
                    // Save the selected task to local
                    UserInfo.getInstance().selectedSwapTask = task;

                    // Go to Map page
                    ListMapFragment mapFragment = new ListMapFragment();
                    Bundle args = new Bundle();
                    args.putBoolean(AppConstants.KEY_IS_SWAP, true);
                    mapFragment.setArguments(args);

                    FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), mapFragment, R.id.frameLayout);
                }
            });
        }

        taskListAdapter.setData(tasks);
        mBinding.rvListTask.setAdapter(taskListAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFragment(EventBusMessage messageEvent){
        if (messageEvent != null){
            int messageType = messageEvent.getMessageType();
            if (messageType == EventBusMessage.MessageType.GO_TO_SWAP_TASK){
                // Clear the back stack
                clearFragmentBackStack();
            }
            else if(messageType == EventBusMessage.MessageType.REJECT_SWAP_TASK){
                // Reload the task list
                getSwapTasks();
            }
        }
    }

    private void clearFragmentBackStack(){
        FragmentProcess.moveToSpecifyFragment(mActivity.getSupportFragmentManager(), this.getClass().getSimpleName());
        getSwapTasks();
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }
}
