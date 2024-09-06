package com.blazma.logistics.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.databinding.ActivityTermsConditionBinding;
import com.blazma.logistics.global.MyPreferenceManager;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.login.LoginResponse;
import com.blazma.logistics.model.terms.TermsResponse;
import com.blazma.logistics.model.task.DefaultResponse;
import com.blazma.logistics.utilities.AppConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class TermsConditionActivity extends BaseActivity{
    ActivityTermsConditionBinding mBinding;
    private Boolean isFromLogin = false;
    private String arabicLink = "";
    private String engLink = "";
    private Bitmap signBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutBinding();
        initActions();
    }

    private void layoutBinding(){
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_terms_condition);
    }

    private void initActions(){
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.btnArabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!arabicLink.equals("")){
                    openLink(arabicLink);
                }
            }
        });

        mBinding.btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!engLink.equals("")){
                    openLink(engLink);
                }
            }
        });

        mBinding.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValid()){
                    acceptTerms();
                }
            }
        });

        if(getIntent() != null){
            isFromLogin = getIntent().getBooleanExtra("isFromLogin", false);
        }

        mBinding.btnDelete.setOnClickListener(v -> {
            mBinding.signatureView.clearCanvas();
        });

        getTerms();
    }

    private File saveBitmapToLocal(Bitmap bmp) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, bytes);

        File signFile = null;
        long time= System.currentTimeMillis();
        String signFileName = "sign" + time + ".jpg";
        UserInfo.getInstance().signatureFileName = signFileName;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            String pictureDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File myDir = new File(pictureDirPath + "/tracking_images");

            // Make sure the directory exists
            myDir.mkdirs();

            signFile = new File(myDir, signFileName);
        } else
        {
            signFile = new File(Environment.getExternalStorageDirectory() + File.separator + signFileName);
        }

        FileOutputStream fo = new FileOutputStream(signFile);
        fo.write(bytes.toByteArray());
        fo.flush();
        fo.close();

        return signFile;
    }

    private Boolean checkValid(){
        if(mBinding.signatureView.isBitmapEmpty()){
            Toast.makeText(this, getResources().getString(R.string.add_sign), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openLink(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void getTerms(){
        showLoading();
        LogisticsApplication.apiManager.getTerms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<TermsResponse>() {
                    @Override
                    public void onSuccess(@NonNull TermsResponse termsResponse) {
                        hideLoading();
                        if(termsResponse.status){
                           arabicLink = termsResponse.data.arabicLink;
                           engLink = termsResponse.data.englishLink;
                        }
                        else {
                            Toast.makeText(TermsConditionActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }

                        initUserInfo();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoading();
                        Toast.makeText(TermsConditionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        initUserInfo();
                    }
                });
    }

    private void initUserInfo(){
        if(UserInfo.getInstance().loginInfo == null){
            showLoading();

            String username = MyPreferenceManager.getInstance().getString(AppConstants.KEY_USERNAME);
            String password = MyPreferenceManager.getInstance().getString(AppConstants.KEY_PASSWORD);

            LogisticsApplication.apiManager.loginWithPassword(username, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<LoginResponse>() {
                        @Override
                        public void onSuccess(@NonNull LoginResponse loginResponse) {
                            hideLoading();

                            if (loginResponse.getStatus()){
                                // Save the login information
                                UserInfo.getInstance().loginInfo = loginResponse.getData();
                                MyPreferenceManager.getInstance().put(AppConstants.KEY_USER_ID, loginResponse.getData().getId());
                            }
                            else{
                                Log.d("Terms", "Login Error : " + loginResponse.getMessage());
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();

                            Log.d("Terms", "Login Error :" + e.getMessage());
                        }
                    });
        }
    }

    private void acceptTerms(){
        signBitmap = mBinding.signatureView.getSignatureBitmap();
        // save the bitmap to file in local
        File signFile = null;
        try {
            signFile = saveBitmapToLocal(signBitmap);
        }
        catch (Exception e){
            Toast.makeText(this, getResources().getString(R.string.msg_failed_save_sign_image), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }

        if (UserInfo.getInstance().loginInfo != null) {
            showLoading();
            int driverId = UserInfo.getInstance().loginInfo.getId();

            LogisticsApplication.apiManager.acceptTerms(driverId, signFile)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                        @Override
                        public void onSuccess(@NonNull DefaultResponse defaultResponse) {
                            hideLoading();
                            if(defaultResponse.status){
                                // Go to main page
                                MyPreferenceManager.getInstance().put(AppConstants.KEY_IS_ACCEPT_TERMS, true);
                                Intent intent = new Intent(TermsConditionActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(TermsConditionActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();
                            Toast.makeText(TermsConditionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Toast.makeText(this, getResources().getString(R.string.msg_failed_getting_login_info), Toast.LENGTH_SHORT).show();

            // Go to login page
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
