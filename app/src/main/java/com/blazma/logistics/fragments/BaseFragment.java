package com.blazma.logistics.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.blazma.logistics.R;
import com.blazma.logistics.activities.BaseActivity;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.model.MessageEvent;
import com.blazma.logistics.utilities.CommonUtils;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

public class BaseFragment extends Fragment {
    protected BaseActivity mActivity;

    private ProgressDialog mProgressDialog = null;

    public BaseFragment(){

    }

    public void updateUI() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof BaseActivity) {
            mActivity = (BaseActivity) context;
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        EventBus.getDefault().unregister(this);
//    }
//
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFragment(EventBusMessage messageEvent) {

    }

    public void restart(){
        Objects.requireNonNull(mActivity).finish();
        startActivity(mActivity.getIntent());
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null && mActivity.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
    }

    public Fragment getCurrentFragment() {
        try {
            int index = this.getFragmentManager().getBackStackEntryCount() - 1;
            FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(index);
            String tag = backEntry.getName();
            Fragment fragment = getFragmentManager().findFragmentByTag(tag);
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void back(){
        hideSoftKeyboard();
        mActivity.back();
    }

    public void showErrorMessage(View view, String msg){
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorRed));
        TextView tv = sbView.findViewById(R.id.snackbar_text);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), getString(R.string.Nuinito_Regular));
        tv.setTypeface(font);
        tv.setTextColor(getResources().getColor(R.color.colorWhite));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackbar.show();
    }

    public void showSuccessMessage(View view, String msg){
        Snackbar snackbar = Snackbar
                .make(view, msg, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorTransparent));
        TextView tv = sbView.findViewById(R.id.snackbar_text);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), getString(R.string.Nuinito_Regular));
        tv.setTypeface(font);
        tv.setTextColor(getResources().getColor(R.color.colorSuccess));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackbar.show();
    }

    /**
     * show the loading dialog while  long processing
     */
    public void showLoading() {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(mActivity);
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
