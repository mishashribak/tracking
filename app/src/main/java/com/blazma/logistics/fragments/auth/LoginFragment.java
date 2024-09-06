package com.blazma.logistics.fragments.auth;

import android.content.Intent;
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
import com.blazma.logistics.activities.MainActivity;
import com.blazma.logistics.activities.TermsConditionActivity;
import com.blazma.logistics.databinding.FragmentLoginBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.global.MyPreferenceManager;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.login.LoginResponse;
import com.blazma.logistics.utilities.AppConstants;
import com.google.android.material.button.MaterialButton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginFragment extends BaseFragment {
    private String TAG = LoginFragment.class.getSimpleName();
    private FragmentLoginBinding mBinding;

    CompositeDisposable compositeDisposable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        initData();
        initActions();

        View view = mBinding.getRoot();
        return view;
    }

    private void initData(){
        if(MyPreferenceManager.getInstance().getBoolean(AppConstants.KEY_IS_REMEMBER_ME)){
            String username = MyPreferenceManager.getInstance().getString(AppConstants.KEY_USERNAME);
            String password = MyPreferenceManager.getInstance().getString(AppConstants.KEY_PASSWORD);

            mBinding.etUsername.setText(username);
            mBinding.etPassword.setText(password);
        }
    }

    private void login(){
        // If correct information, do login
        String username = mBinding.etUsername.getText() != null ? mBinding.etUsername.getText().toString() : "";
        String password = mBinding.etPassword.getText() != null ? mBinding.etPassword.getText().toString() : "";

        if (username.isEmpty() || password.isEmpty()){
            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.incorrect_login_info), Toast.LENGTH_SHORT).show();
        }
        else{
            showLoading();

            LogisticsApplication.apiManager.loginWithPassword(username, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<LoginResponse>() {
                        @Override
                        public void onSuccess(@NonNull LoginResponse loginResponse) {
                            hideLoading();

                            if (loginResponse.getStatus()){

                                // Save the login status
                                UserInfo.getInstance().saveLoginInfo(mActivity, username, password);
                                MyPreferenceManager.getInstance().put(AppConstants.KEY_IS_REMEMBER_ME, mBinding.cbRemember.isChecked());
                                MyPreferenceManager.getInstance().put(AppConstants.KEY_IS_LOGGED_IN, true);

                                // Save the login information
                                UserInfo.getInstance().loginInfo = loginResponse.getData();
                                MyPreferenceManager.getInstance().put(AppConstants.KEY_USER_ID, loginResponse.getData().getId());

                                if(loginResponse.getData().getTermAccepted()){
                                    MyPreferenceManager.getInstance().put(AppConstants.KEY_IS_ACCEPT_TERMS, true);
                                    // Go to Main page
                                    gotoHomePage();
                                }
                                else{
                                    gotoTermsPage();
                                }
                            }
                            else{
                                Log.e("Login", "Error :" + loginResponse.getMessage());
                                Toast.makeText(mActivity, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();

                            Log.e("Login", "Error :" + e.getMessage());
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    // Go to Home page
    private void gotoHomePage(){
        Intent intent = new Intent(mActivity, MainActivity.class);
        startActivity(intent);
        mActivity.finish();
    }

    // Go to Terms page
    private void gotoTermsPage(){
        Intent intent = new Intent(mActivity, TermsConditionActivity.class);
        intent.putExtra("isFromLogin", true);
        startActivity(intent);
        mActivity.finish();
    }

    private void initActions(){
        MaterialButton btnLogin = mBinding.getRoot().findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }
}
