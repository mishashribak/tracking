package com.blazma.logistics.fragments.main.infreezer;

import android.annotation.SuppressLint;
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
import com.blazma.logistics.adapter.OutFreezerListTaskAdapter;
import com.blazma.logistics.databinding.FragmentListTaskOutfreezerBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.outfreezer.OutFreezerLocationCheckFragment;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.outfreezer.OutFreezerTaskModel;
import com.blazma.logistics.model.outfreezer.OutFreezerTaskResponse;
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

public class InFreezerTaskListFragment extends BaseFragment {
    private String TAG = InFreezerTaskListFragment.class.getSimpleName();
    private FragmentListTaskOutfreezerBinding mBinding;
    private OutFreezerListTaskAdapter listTaskAdapter;
    private List<OutFreezerTaskModel> tasks = new ArrayList<>();

    CompositeDisposable compositeDisposable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_task_outfreezer, container, false);

        initComponent();
        initActions();
        initData();


        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        return mBinding.getRoot();
    }

    public void initComponent(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }
        mBinding.tvToolbarTitle.setText(R.string.samples_pull_out);
    }

    private void initActions() {
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.back();
            }
        });
    }

    private void initData(){
        getTaskList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFragment(EventBusMessage messageEvent){
        if (messageEvent != null){
            int messageType = messageEvent.getMessageType();
            if (messageType == EventBusMessage.MessageType.GO_TO_SAMPLES_PULL_OUT){ // Added new address
                // Clear the backstack
                clearFragmentBackStack();
            }
        }
    }

    private void clearFragmentBackStack(){
        FragmentProcess.moveToSpecifyFragment(mActivity.getSupportFragmentManager(), this.getClass().getSimpleName());
        this.initData();
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    /**
     * Get the task list
     */
    @SuppressLint("CheckResult")
    private void getTaskList(){
        compositeDisposable = new CompositeDisposable();

        if (UserInfo.getInstance().loginInfo != null){
            showLoading();

            LogisticsApplication.apiManager.getInFreezerTasks(UserInfo.getInstance().loginInfo.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<OutFreezerTaskResponse>() {
                        @Override
                        public void onSuccess(@NonNull OutFreezerTaskResponse tasksResponse) {
                            hideLoading();

                            if (tasksResponse.status){
                                tasks = tasksResponse.data;

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
            Log.e("InFreezerList", "User information is null");
        }
    }

    private void setData(){
        if(listTaskAdapter == null){
            listTaskAdapter = new OutFreezerListTaskAdapter(mActivity, tasks, new OutFreezerListTaskAdapter.OnTaskItemClickListener() {
                @Override
                public void onClick(OutFreezerTaskModel task) {
                    // Save the selected task to local
                    UserInfo.getInstance().selectedOutFreezerTask = task;
                    FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new InFreezerScanFreezerFragment(), R.id.frameLayout);
                }

                @Override
                public void onAccept(OutFreezerTaskModel task) {

                }
            });
        }

        listTaskAdapter.setData(tasks);
        mBinding.rvListTask.setAdapter(listTaskAdapter);

    }
}