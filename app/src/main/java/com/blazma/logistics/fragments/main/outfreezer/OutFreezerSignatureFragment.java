package com.blazma.logistics.fragments.main.outfreezer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.adapter.FirstStepDriverBarCodeAdapter;
import com.blazma.logistics.databinding.FragmentDriverSignatureBinding;
import com.blazma.logistics.databinding.FragmentOutfreezerSignatureBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.common.TaskStatusFragment;
import com.blazma.logistics.fragments.main.list_task.DriverSignatureFragment;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.freezer.SampleItemModel;
import com.blazma.logistics.model.freezer.SampleResponse;
import com.blazma.logistics.model.outfreezer.OutFreezerSummaryItemModel;
import com.blazma.logistics.model.outfreezer.OutFreezerTaskItemModel;
import com.blazma.logistics.model.task.DefaultResponse;
import com.blazma.logistics.model.task.SampleBarCodeModel;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class OutFreezerSignatureFragment extends BaseFragment {
    private String TAG = OutFreezerSignatureFragment.class.getSimpleName();
    private FragmentOutfreezerSignatureBinding mBinding;
    private FirstStepDriverBarCodeAdapter commonBarCodeAdapter;
    private List<SampleBarCodeModel> registeredSampleList = new ArrayList<>();

    private static final int WRITE_STORAGE_CODE = 1002;

    // Signature Bitmap
    private Bitmap signBitmap = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_outfreezer_signature, container, false);

        this.initData();
        this.initActions();
        this.initComponent();
        this.setData();

        View view = mBinding.getRoot();
        return view;
    }

    private void initData(){
        List<SampleBarCodeModel> convertedData = new ArrayList<>();

        for(int i = 0; i < UserInfo.getInstance().selectedOutFreezerTask.taskList.size(); i++){
            OutFreezerTaskItemModel taskData = UserInfo.getInstance().selectedOutFreezerTask.taskList.get(i);
            for(int j = 0; j < taskData.bagList.size(); j++){
                OutFreezerSummaryItemModel item = taskData.bagList.get(j);

                convertedData.add(new SampleBarCodeModel(item.bagCode, item.temperatureType, item.sampleType));
            }
        }

        registeredSampleList = convertedData;
    }

    private void initComponent(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }

        mBinding.txtLocationName.setText(UserInfo.getInstance().selectedOutFreezerTask.clientName);
    }

    private void initActions() {
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.back();
            }
        });

        mBinding.btnDelete.setOnClickListener(v -> {
            mBinding.signatureView.clearCanvas();
        });

        mBinding.btnSubmitTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWriteStoragePermission();
            }
        });
    }

    private void setData(){

        if(commonBarCodeAdapter == null){
            commonBarCodeAdapter = new FirstStepDriverBarCodeAdapter(mActivity, false, true, new FirstStepDriverBarCodeAdapter.OnItemClickListener() {
                @Override
                public void onClickDelete(int position) {
                }
            });
        }

        commonBarCodeAdapter.setData(registeredSampleList);
        mBinding.txtBarcodeTotal.setText(String.format(mActivity.getResources().getString(R.string.total_barcodes), registeredSampleList.size()));
        mBinding.rvListBarCodes.setAdapter(commonBarCodeAdapter);
    }

    /**
     * Check the Write storage permission
     */
    private void checkWriteStoragePermission(){
//        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_CODE);
//        }
//        else{
//            uploadSignature();
//        }

        uploadSignature();
    }

    // Save the bitmap to file
    public static File saveBitmapToLocal(Bitmap bmp) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File signFile = null;
        long time= System.currentTimeMillis();
        String signFileName = "sign" + time + ".jpg";

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

    private void uploadSignature() {
        callUploadSignatureApi();

    /*
        // Upload signature with confirmation code
        signBitmap = mBinding.signatureView.getSignatureBitmap();

        // save the bitmap to file in local
        try {
            File savedFile = saveBitmapToLocal(signBitmap);

            if(mBinding.signatureView.isBitmapEmpty()){
                Toast.makeText(mActivity, mActivity.getResources().getString(R.string.msg_make_signature), Toast.LENGTH_SHORT).show();
            } else {
                callUploadSignatureApi(savedFile);
            }

        }
        catch (Exception e){
            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.msg_failed_save_sign_image), Toast.LENGTH_SHORT).show();
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

     */
    }

    private void callUploadSignatureApi(){
        callOutFreezerTaskSignatureApi();
    }

    private void callOutFreezerTaskSignatureApi(){
        List<Integer> taskId = UserInfo.getInstance().selectedOutFreezerTask.taskIds;

        showLoading();

        LogisticsApplication.apiManager.closeFreezerOutTaskWithoutSignature(taskId, mBinding.inputConfirmCode.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                    @Override
                    public void onSuccess(@NonNull DefaultResponse sampleResponse) {
                        hideLoading();

                        Toast.makeText(mActivity, sampleResponse.message, Toast.LENGTH_SHORT).show();

                        if (sampleResponse.status){
                            FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new TaskStatusFragment(), R.id.frameLayout);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoading();

                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case WRITE_STORAGE_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    uploadSignature();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.msg_enable_file_access), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }
}
