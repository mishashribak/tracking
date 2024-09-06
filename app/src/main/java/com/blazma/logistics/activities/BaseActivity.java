package com.blazma.logistics.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.model.MessageEvent;
import com.blazma.logistics.utilities.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog = null;

    public void back(){
        int backstack_count = getSupportFragmentManager().getBackStackEntryCount();
        if (backstack_count <= 1) {
            finish();
        } else if (backstack_count > 1) {
            ((BaseFragment)getBackFragment()).updateUI();
            getSupportFragmentManager().popBackStack();
        }
    }

    public Fragment getBackFragment() {
        try {
            int index = this.getSupportFragmentManager().getBackStackEntryCount() - 1;
            FragmentManager.BackStackEntry backEntry = this.getSupportFragmentManager().getBackStackEntryAt(index - 1);
            String tag = backEntry.getName();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearBackStack(){
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    /**
     * show the loading dialog while  long processing
     */
    public void showLoading() {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(this);
    }

    /**
     * hide loading dialog
     */
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }
}

