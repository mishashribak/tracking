package com.blazma.logistics.fragments.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.adapter.ScheduleListAdapter;
import com.blazma.logistics.databinding.FragmentScheduleListBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.model.schedule.Schedule;
import com.blazma.logistics.model.schedule.ScheduleResponse;
import com.blazma.logistics.model.task.DefaultResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ScheduleListFragment extends BaseFragment {
    private String TAG = ScheduleListFragment.class.getSimpleName();
    private FragmentScheduleListBinding mBinding;
    private ScheduleListAdapter scheduleListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule_list, container, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView(){
        scheduleListAdapter = new ScheduleListAdapter(mActivity, new ScheduleListAdapter.OnTaskItemClickListener() {
            @Override
            public void onClick(Schedule task) {

            }
        });
        mBinding.rvSchedule.setAdapter(scheduleListAdapter);
        getScheduleList();

        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.back();
            }
        });
        mBinding.tvConfirmAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptAllSchedule();
            }
        });
    }

    private void getScheduleList(){
        showLoading();
        LogisticsApplication.apiManager.getScheduleList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<ScheduleResponse>() {
                    @Override
                    public void onSuccess(@NonNull ScheduleResponse scheduleResponse) {
                        hideLoading();
                        if (scheduleResponse.status){
                          scheduleListAdapter.setData(scheduleResponse.data);
                        }
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoading();
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void acceptAllSchedule(){
        showLoading();
        LogisticsApplication.apiManager.acceptAllSchedule()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                    @Override
                    public void onSuccess(@NonNull DefaultResponse scheduleResponse) {
                        hideLoading();
                        if (scheduleResponse.status){
                            getScheduleList();
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
