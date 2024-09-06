package com.blazma.logistics.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.adapter.AddCarPhotoAdapter;
import com.blazma.logistics.custom.CustomConfirmDialog;
import com.blazma.logistics.databinding.ActivityAddCarPhotosBinding;
import com.blazma.logistics.global.MyPreferenceManager;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.task.DefaultResponse;
import com.blazma.logistics.utilities.AppConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class AddCarImagesActivity extends BaseActivity {
    ActivityAddCarPhotosBinding mBinding;
    Context mContext;
    private AddCarPhotoAdapter addCarPhotoAdapter;
    private ArrayList<String> mPaths = new ArrayList();
    private final static Integer CAMERA_PERMISSION_CODE = 113;
    private final static Integer MEDIA_STORAGE_CODE     = 114;
    private Bitmap signBitmap = null;

    // Maximum upload image count
    private final static Integer MAX_IMAGE_COUNT        = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutBinding();
        initActions();
        initView();
    }

    private void layoutBinding(){
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_car_photos);
        mContext = this;
    }
    
    private void initView(){
        if(getIntent() != null){
            String car_no = getIntent().getStringExtra("car_no");
            mBinding.txtCarNumber.setText(car_no);
        }
        addCarPhotoAdapter = new AddCarPhotoAdapter(this, mPaths, new AddCarPhotoAdapter.OnTaskItemClickListener() {
            @Override
            public void onAdd() {
                if(checkCameraPermission()){
                    addPhoto();
                }
            }

            @Override
            public void onRemove(String path) {
                mPaths.remove(path);
                if(mPaths.size() < MAX_IMAGE_COUNT && !mPaths.contains("add")){
                    mPaths.add("add");
                }
                addCarPhotoAdapter.setData(mPaths);
            }
        });
        mBinding.rvImages.setAdapter(addCarPhotoAdapter);

        mPaths.add("add");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initActions(){
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.btnDelete.setOnClickListener(v -> {
            mBinding.signatureView.clearCanvas();
        });

        mBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValid()){
                    CustomConfirmDialog dlg = new CustomConfirmDialog(AddCarImagesActivity.this, getString(R.string.are_u_sure_receive_car), getString(R.string.yes), getString(R.string.no), new CustomConfirmDialog.ItemClickInterface() {
                        @Override
                        public void onOK() {
                            uploadImages();
                        }
                    });
                    dlg.showDialog();
                    dlg.setCanceledOnTouchOutside(false);
                }
            }
        });

        /**
         * SignatureView TouchEventListener
         */
        mBinding.signatureView.setOnTouchListener(new View.OnTouchListener(){
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        // Disable the scroll view to intercept the touch event
                        mBinding.scrollview.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_UP:
                        // Allow scroll View to intercept the touch event
                        mBinding.scrollview.requestDisallowInterceptTouchEvent(false);
                        return true;
                    default:
                        return true;
                }
            }
        });
    }
    
    private void addPhoto(){
        openCamera();
    }

    private Uri imageUri;
    public void openCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, MEDIA_STORAGE_CODE);
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == MEDIA_STORAGE_CODE){
                try {
                   String path = getRealPathFromURI(imageUri);
                   compressImage(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void compressImage(String filePath) throws IOException {
        File imageFile = new File(filePath);
        File newFile =  new Compressor(this).compressToFile(imageFile);
        mPaths.add(0,  newFile.getAbsolutePath());
        if(mPaths.size() >= MAX_IMAGE_COUNT){
            mPaths.remove("add");
        }
        addCarPhotoAdapter.setData(mPaths);
    }

    private boolean checkCameraPermission() {
        int permissionCamera = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int permissionStorage = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),CAMERA_PERMISSION_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    addPhoto();
                }
            }
        }
    }

    public File saveBitmapToLocal(Bitmap bmp) throws IOException {
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
        if(mPaths.contains("add")){
            Toast.makeText(this, getResources().getString(R.string.upload_car_images), Toast.LENGTH_SHORT).show();
            return false;
        }else{
            if(mPaths.size() < 6){
                Toast.makeText(this, getResources().getString(R.string.upload_car_images), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if(mBinding.signatureView.isBitmapEmpty()){
            Toast.makeText(this, getResources().getString(R.string.add_sign), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadImages(){
        // Upload signature with confirmation code
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

        showLoading();

        int carId = UserInfo.getInstance().loginInfo.getCar().getId();
        int driverId = UserInfo.getInstance().loginInfo.getId();
        File file1 = null;
        File file2 = null;
        File file3 = null;
        File file4 = null;
        File file5 = null;
        File file6 = null;

        mPaths.remove("add");
        for (int i=0; i<mPaths.size(); i++){
            if(i == 0){
                file1 = new File(mPaths.get(i));
            }
            if(i == 1){
                file2 = new File(mPaths.get(i));
            }
            if(i == 2){
                file3 = new File(mPaths.get(i));
            }
            if(i == 3){
                file4 = new File(mPaths.get(i));
            }
            if(i == 4){
                file5 = new File(mPaths.get(i));
            }
            if(i == 5){
                file6 = new File(mPaths.get(i));
            }
        }

        LogisticsApplication.apiManager.uploadImages(carId, driverId, signFile, file1, file2, file3, file4, file5, file6)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                    @Override
                    public void onSuccess(@NonNull DefaultResponse response) {
                        hideLoading();

                        if (response.status){
                            finish();
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