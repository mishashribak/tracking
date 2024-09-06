package com.blazma.logistics.fragments.main.money;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.databinding.FragmentVerifyFromLocationBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.money.VerifyLocationOtpResponse;
import com.blazma.logistics.model.swap.SwapTaskResponse;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class FromLocationVerifyOtpFragment extends BaseFragment {
    FragmentVerifyFromLocationBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_from_location, container, false);

        initViews();

        return mBinding.getRoot();
    }

    private void initViews(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }

        // Set the title
        mBinding.tvToolbarTitle.setText(R.string.verify_from_location);

        // Back button
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.back();
            }
        });

        // Verify
        mBinding.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOTP();
            }
        });
    }

    private void verifyOTP(){

        String otp = mBinding.etOtp.getText().toString().trim();
        if (otp.isEmpty()) {
            Toast.makeText(mActivity, getString(R.string.please_enter_otp), Toast.LENGTH_LONG).show();
        }
        else{
            // Call api
            showLoading();

            LogisticsApplication.apiManager.verifyFromLocationOTP(UserInfo.getInstance().selectedMoneyTask.id, otp)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<VerifyLocationOtpResponse>() {
                        @Override
                        public void onSuccess(@NonNull VerifyLocationOtpResponse tasksResponse) {
                            hideLoading();

                            if (tasksResponse.status){
                                if (tasksResponse.data.equals(AppConstants.MSG_CORRECT_OTP)) {
                                    // Go to ToLocation Otp
                                    FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new ToLocationVerifyOtpFragment(), R.id.frameLayout);
                                }
                                else{
                                    Toast.makeText(mActivity, tasksResponse.data, Toast.LENGTH_LONG).show();
                                }
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
    }
}
