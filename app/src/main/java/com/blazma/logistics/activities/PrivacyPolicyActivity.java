package com.blazma.logistics.activities;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.R;
import com.blazma.logistics.databinding.ActivityPrivacyPolicyBinding;

public class PrivacyPolicyActivity extends BaseActivity{
    ActivityPrivacyPolicyBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutBinding();

        initActions();
    }

    private void layoutBinding(){
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_privacy_policy);
    }

    private void initActions(){
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
