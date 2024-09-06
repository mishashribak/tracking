package com.blazma.logistics.activities;

import android.content.Intent;
import android.os.Bundle;

import com.blazma.logistics.R;
import com.blazma.logistics.fragments.auth.LoginFragment;
import com.blazma.logistics.global.MyPreferenceManager;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

public class LoginActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkIfAlreadyLoggedIn();
    }

    private void checkIfAlreadyLoggedIn(){
        if(MyPreferenceManager.getInstance().getBoolean(AppConstants.KEY_IS_LOGGED_IN)){
            if (!MyPreferenceManager.getInstance().getBoolean(AppConstants.KEY_IS_ACCEPT_TERMS)) {
                // Go To Main Screen
                Intent intent = new Intent(this, TermsConditionActivity.class);
                startActivity(intent);
            }
            else{
                // Go To Main Screen
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

            // close this page
            this.finish();
        }
        else {
            FragmentProcess.replaceFragment(getSupportFragmentManager(), new LoginFragment(), R.id.frameLayout);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocaleHelper.onAttach(this);
    }

}
