package com.blazma.logistics.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.databinding.ActivityProfileBinding;
import com.blazma.logistics.global.MyPreferenceManager;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.login.LoginData;
import com.blazma.logistics.model.login.LoginResponse;
import com.blazma.logistics.model.task.DefaultResponse;
import com.blazma.logistics.utilities.AppConstants;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ProfileActivity extends BaseActivity {
    ActivityProfileBinding mBinding;
    Context mContext;
    private String mCarNo="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutBinding();
        initActions();
        getData();
    }

    private void layoutBinding(){
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        mContext = this;
    }

    private void initActions(){
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.btnReceiveCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddImages();
            }
        });

        mBinding.btnReleaseCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseCar();
            }
        });
    }

    private void getData(){
        if (UserInfo.getInstance().loginInfo == null){
            return;
        }

        try {
            showLoading();

            Integer userId = UserInfo.getInstance().loginInfo.getId();
            LogisticsApplication.apiManager.getProfile(userId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<LoginResponse>() {
                        @Override
                        public void onSuccess(@NonNull LoginResponse response) {
                            hideLoading();

                            if (response.getStatus()){
                                setData(response.getData());
                            } else{
                                Log.d("Profile", "Error Message : " + response.getMessage());
                                Toast.makeText(mContext, response.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();
                            Log.d("Profile", "Error Message : " + e.getMessage());
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        catch (Exception e){
            hideLoading();
            e.printStackTrace();
            Log.d("Profile", "Error Message : " + e.getMessage());
        }
    }

    private void setData(LoginData data){
        if(data != null){
            mBinding.txtName.setText(data.getName());
            mBinding.txtCity.setText(data.getCity());
            mBinding.txtEmail.setText(data.getEmail());
            mBinding.txtMobileNumber.setText(data.getMobile());

            mBinding.txtDriverID.setText(data.getCar().getDriverId().toString());
            mBinding.txtModel.setText(data.getCar().getModel());
            mBinding.txtColor.setText(data.getCar().getColor());
            mBinding.txtDescription.setText(data.getCar().getDescription());
            mBinding.txtCarNumber.setText(data.getCar().getPlateNumber());
            mBinding.txtUserName.setText(data.getUsername());
            mCarNo = data.getCar().getPlateNumber();
        }
    }

    private void goToAddImages(){
        Intent intent = new Intent(this, AddCarImagesActivity.class);
        intent.putExtra("car_no", mCarNo);
        startActivity(intent);
    }

    private void releaseCar(){
        showLoading();

        int carId = UserInfo.getInstance().loginInfo.getCar().getId();
        int driverId = UserInfo.getInstance().loginInfo.getId();

        LogisticsApplication.apiManager.releaseCar(carId, driverId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                    @Override
                    public void onSuccess(@NonNull DefaultResponse response) {
                        hideLoading();

                        if (response.status){
                            // Logout
                            MyPreferenceManager.getInstance().put(AppConstants.KEY_IS_LOGGED_IN, false);

                            // Go to login page
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
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
}