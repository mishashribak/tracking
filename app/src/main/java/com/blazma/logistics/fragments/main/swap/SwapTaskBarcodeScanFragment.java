package com.blazma.logistics.fragments.main.swap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.adapter.CommonBarCodeAdapter;
import com.blazma.logistics.databinding.FragmentScanSwapBarcodeBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.common.TaskStatusFragment;
import com.blazma.logistics.fragments.main.outfreezer.OutFreezerScannedBagsFragment;
import com.blazma.logistics.fragments.main.outfreezer.OutFreezerSignatureFragment;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.swap.SampleBarcode;
import com.blazma.logistics.model.swap.SwapTaskData;
import com.blazma.logistics.model.task.DefaultResponse;
import com.blazma.logistics.model.task.SampleBarCodeModel;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;
import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.control.TorchSwitchControl;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderStyle;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SwapTaskBarcodeScanFragment extends BaseFragment implements BarcodeCaptureListener {
    private final String TAG = SwapTaskBarcodeScanFragment.class.getSimpleName();
    private FragmentScanSwapBarcodeBinding mBinding;

    private CommonBarCodeAdapter commonBarCodeAdapter;
    private DataCaptureContext dataCaptureContext;
    private BarcodeCapture barcodeCapture;
    private Camera camera;
    private DataCaptureView dataCaptureView;

    private List<SampleBarCodeModel> dataList;

    private static final int CAMERA_PERMISSION_CODE = 2001;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan_swap_barcode, container, false);

        initData();
        initViews();
        initBarcodeScanner();
        setData();
        updateLayout();

        return mBinding.getRoot();
    }

    private void initData(){
        List<SampleBarCodeModel> convertedData = new ArrayList<>();
        SwapTaskData taskData = UserInfo.getInstance().selectedSwapTask;
        if (taskData != null && taskData.task != null) {
            for (int i=0; i < taskData.task.samples.size(); i++) {
                SampleBarcode barcode = taskData.task.samples.get(i);
                boolean isExistedBagCode = false;
                for(int m = 0; m < convertedData.size(); m++){
                    if(convertedData.get(m).code.equals(barcode.bagCode)){
                        isExistedBagCode = true;
                    }
                }

                if(!isExistedBagCode){
                    convertedData.add(new SampleBarCodeModel(barcode.bagCode, barcode.temperatureType, barcode.sampleType));
                }

//                convertedData.add(new SampleBarCodeModel(barcode.bagCode, barcode.temperatureType, barcode.sampleType));
            }
        }

        // Set the barcode list
        dataList = convertedData;
    }

    private void initViews(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }

        mBinding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSwapTaskAction();

            }
        });

        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.back();
            }
        });

        mBinding.tvTotalCount.setText(String.format(mActivity.getResources().getString(R.string.total_barcodes), dataList.size()));
    }

    private void initBarcodeScanner(){
        BarcodeCaptureSettings settings = new BarcodeCaptureSettings();
        settings.enableSymbology(Symbology.EAN8, true);
        settings.enableSymbology(Symbology.UPCE, true);
        settings.enableSymbology(Symbology.QR, true);
        settings.enableSymbology(Symbology.CODE11, true);
        settings.enableSymbology(Symbology.CODE39, true);
        settings.enableSymbology(Symbology.CODE128, true);
        settings.enableSymbology(Symbology.EAN13_UPCA, true);

        dataCaptureContext = DataCaptureContext.forLicenseKey(getString(R.string.scandit_api_key));
        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, settings);
        barcodeCapture.addListener(this);
        dataCaptureView = DataCaptureView.newInstance(mActivity, dataCaptureContext);

        BarcodeCaptureOverlay overlay = BarcodeCaptureOverlay.newInstance(barcodeCapture, dataCaptureView);
        overlay.setViewfinder(new RectangularViewfinder(RectangularViewfinderStyle.SQUARE));
        Brush brush = new Brush(Color.TRANSPARENT, Color.WHITE, 3f);
        overlay.setBrush(brush);

        // Create the flash button (Torch On/Off)
        TorchSwitchControl torchSwitchControl = new TorchSwitchControl(mActivity);
        dataCaptureView.addControl(torchSwitchControl);

        CameraSettings cameraSettings = BarcodeCapture.createRecommendedCameraSettings();
        camera = Camera.getDefaultCamera();

        if (camera != null) {
            camera.applySettings(cameraSettings);
            camera.switchToDesiredState(FrameSourceState.ON);
            dataCaptureContext.setFrameSource(camera);
        }

        mBinding.viewCamera.addView(dataCaptureView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void setData(){
        if(commonBarCodeAdapter == null){
            commonBarCodeAdapter = new CommonBarCodeAdapter(mActivity, false, false, new CommonBarCodeAdapter.OnItemClickListener() {
                @Override
                public void onClickDelete(int position) {
                    // Delete the selected barcode
                    dataList.remove(position);

                    // Refresh the barcode list
                    commonBarCodeAdapter.setData(dataList);

                    updateLayout();
                }
            });
        }

        commonBarCodeAdapter.setData(dataList);
        mBinding.rvListBarCodes.setAdapter(commonBarCodeAdapter);
        updateLayout();
    }

    // Check if barcode list is empty
    private void updateLayout(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.tvTotalCount.setText(String.format(mActivity.getResources().getString(R.string.total_barcodes), dataList.size()));

                if(dataList.size() == 0){
                    mBinding.btnConfirm.setVisibility(View.VISIBLE);
                } else {
                    mBinding.btnConfirm.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Call the swap task api
     */
    private void doSwapTaskAction(){
        try {
            showLoading();
            LogisticsApplication.apiManager.acceptSwapTask(UserInfo.getInstance().selectedSwapTask.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                        @Override
                        public void onSuccess(@NonNull DefaultResponse response) {
                            hideLoading();

                            if (response.status){
                                // Go to Success page
                                FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new SwapTaskFinishFragment(), R.id.frameLayout);
                            }
                            else{
                                Toast.makeText(mActivity, response.message, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideLoading();
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        catch (Exception e){
            hideLoading();
            e.printStackTrace();
        }

    }

    private void adjustBarCodeCapture(){
        barcodeCapture.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                barcodeCapture.setEnabled(true);
            }
        }, 3000);
    }

    // Update the Barcode list after scanned the barcode
    @SuppressLint("NotifyDataSetChanged")
    private void checkScannedBarcode(String scannedBarcode){
//        Toast.makeText(mActivity, "Scanned barcode : " + scannedBarcode, Toast.LENGTH_LONG).show();

        boolean isMatched = false;
        for(int i = 0; i < dataList.size(); i++){
            if(dataList.get(i).code.equals(scannedBarcode)){
                dataList.remove(i);
                isMatched = true;
                break;
            }
        }

        commonBarCodeAdapter.setData(dataList);
        updateLayout();

        if (!isMatched && dataList.size() != 0) {
            Toast.makeText(mActivity, mActivity.getString(R.string.barcode_not_matched), Toast.LENGTH_LONG).show();
        }


        adjustBarCodeCapture();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check for camera permission and request it
        requestCameraPermission();
    }

    @Override
    public void onDestroy() {
        barcodeCapture.removeListener(this);
        dataCaptureContext.removeMode(barcodeCapture);

        super.onDestroy();
    }

    @Override
    public void onPause() {
        pauseFrameSource();
        super.onPause();
    }

    /**
     * Request the camera permission
     */
    private void requestCameraPermission(){
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
        else{
            resumeFrameSource();
        }
    }

    /**
     * Switch camera on to start streaming frames
     */
    private void resumeFrameSource(){
        barcodeCapture.setEnabled(true);

        if (camera != null)
            camera.switchToDesiredState(FrameSourceState.ON);
    }

    /**
     * Switch camera off to stop streaming frames
     */
    private void pauseFrameSource(){
        barcodeCapture.setEnabled(false);

        if (camera != null)
            camera.switchToDesiredState(FrameSourceState.OFF);
    }


    /**
     * BarcodeCaptureListener
     * @param barcodeCapture
     * @param barcodeCaptureSession
     * @param frameData
     */

    @Override
    public void onBarcodeScanned(@NotNull BarcodeCapture barcodeCapture, @NotNull BarcodeCaptureSession barcodeCaptureSession, @NotNull FrameData frameData) {
        List<Barcode> recognizedBarcodes = barcodeCaptureSession.getNewlyRecognizedBarcodes();

        if (!recognizedBarcodes.isEmpty()){
            Barcode barcode = recognizedBarcodes.get(0);
            String scannedBarcode = barcode.getData();

            checkScannedBarcode(scannedBarcode);
        }
        else{
            Log.e(TAG, "Barcode list is empty...");
        }
    }

    @Override
    public void onObservationStarted(@NotNull BarcodeCapture barcodeCapture) {

    }

    @Override
    public void onObservationStopped(@NotNull BarcodeCapture barcodeCapture) {

    }

    @Override
    public void onSessionUpdated(@NotNull BarcodeCapture barcodeCapture, @NotNull BarcodeCaptureSession barcodeCaptureSession, @NotNull FrameData frameData) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    resumeFrameSource();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
