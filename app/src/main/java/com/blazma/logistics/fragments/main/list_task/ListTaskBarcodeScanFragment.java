package com.blazma.logistics.fragments.main.list_task;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.R;
import com.blazma.logistics.adapter.CommonBarCodeAdapter;
import com.blazma.logistics.databinding.FragmentSecondScannerBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.firstflow.FirstTaskCountFragment;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.task.SampleBarCodeModel;
import com.blazma.logistics.utilities.AppConstants;
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

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListTaskBarcodeScanFragment extends BaseFragment implements BarcodeCaptureListener {

    private String TAG = ListTaskBarcodeScanFragment.class.getSimpleName();
    private FragmentSecondScannerBinding mBinding;

    private CommonBarCodeAdapter commonBarCodeAdapter;
    private DataCaptureContext dataCaptureContext;
    private BarcodeCapture barcodeCapture;
    private Camera camera;
    private DataCaptureView dataCaptureView;

    private static final int CAMERA_PERMISSION_CODE = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_second_scanner, container, false);

        initComponent();
        initBarCodeScanner();
        setData();
        updateLayout();

        return mBinding.getRoot();
    }

    private void initComponent(){

        if (UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_NEW){
            String selectedContainer = UserInfo.getInstance().getSelectedContainerValue(UserInfo.getInstance().selectedContainerType);
            mBinding.txtTemperature.setText(selectedContainer);
        }
        else{
            mBinding.txtTemperature.setText("");
        }

        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }

        // Save
        mBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_NEW){
                    if (UserInfo.getInstance().selectedTask.taskType.equals("BOX")){
                        FirstTaskCountFragment firstTaskCountFragment = new FirstTaskCountFragment();
                        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), firstTaskCountFragment, R.id.frameLayout);
                    }
                    else{
                        ListTaskBagScanFragment listTaskBagScanFragment = new ListTaskBagScanFragment();
                        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), listTaskBagScanFragment, R.id.frameLayout);
                    }
                }
                else{
                    DriverSignatureFragment driverSignatureFragment = new DriverSignatureFragment();
                    FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), driverSignatureFragment, R.id.frameLayout);
                }
            }
        });

        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.back();
            }
        });
    }

    // Setup the barcode capture
    private void initBarCodeScanner(){
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
            commonBarCodeAdapter = new CommonBarCodeAdapter(mActivity, true, false, new CommonBarCodeAdapter.OnItemClickListener() {
                @Override
                public void onClickDelete(int position) {
                    UserInfo.getInstance().removeBarCode(position);
                    commonBarCodeAdapter.setData(UserInfo.getInstance().scannedBarCodes);
                    updateLayout();
                }
            });
        }

        commonBarCodeAdapter.setData(UserInfo.getInstance().scannedBarCodes);
        mBinding.rvListBarCodes.setAdapter(commonBarCodeAdapter);
        updateLayout();
    }

    private void doAction(String scannedBarcode){
        // Check if there is same barcode
        List<SampleBarCodeModel> result = UserInfo.getInstance().scannedBarCodes.stream().filter(item -> item.code.equals(scannedBarcode)).collect(Collectors.toList());
        if (result.size() > 0){
            SampleBarCodeModel extBarcode = result.get(0);
            int idx = UserInfo.getInstance().scannedBarCodes.indexOf(extBarcode);
            UserInfo.getInstance().scannedBarCodes.get(idx).count += 1;
            UserInfo.getInstance().scannedBarCodes.get(idx).scanDate = Calendar.getInstance().getTime();
        }
        else{
            if (UserInfo.getInstance().selectedTaskType == AppConstants.TASK_STATUS_OUT_FREEZER){
                UserInfo.getInstance().addNewBarCode(new SampleBarCodeModel(scannedBarcode, "", ""));
            }
            else{
                String selectedContainer = UserInfo.getInstance().getSelectedContainerValue(UserInfo.getInstance().selectedContainerType);
                UserInfo.getInstance().addNewBarCode(new SampleBarCodeModel(scannedBarcode, selectedContainer, ""));
            }
        }

        // Sort the barcode list by Date
        Collections.sort(UserInfo.getInstance().scannedBarCodes, Collections.reverseOrder());

        // Refresh the barcode list
        commonBarCodeAdapter.setData(UserInfo.getInstance().scannedBarCodes);
        updateLayout();

        this.adjustBarCodeCapture();
    }

    private void updateLayout(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int barCodeCount = 0;
                for (SampleBarCodeModel barcode : UserInfo.getInstance().scannedBarCodes) {
                    barCodeCount += barcode.count;
                }

                mBinding.txtBarcodeTotal.setText(String.format(getString(R.string.total_barcodes), barCodeCount));

                if(UserInfo.getInstance().scannedBarCodes.size() > 0){
                    mBinding.btnSave.setVisibility(View.VISIBLE);
                } else {
                    mBinding.btnSave.setVisibility(View.GONE);
                }
            }
        });
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

    private void adjustBarCodeCapture(){
        barcodeCapture.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                barcodeCapture.setEnabled(true);
            }
        }, 3000);
    }

    @Override
    public void onBarcodeScanned(@NotNull BarcodeCapture barcodeCapture, @NotNull BarcodeCaptureSession barcodeCaptureSession, @NotNull FrameData frameData) {
        List<Barcode> recognizedBarcodes = barcodeCaptureSession.getNewlyRecognizedBarcodes();

        if (!recognizedBarcodes.isEmpty()){
            Barcode barcode = recognizedBarcodes.get(0);

            String scannedBarcode = barcode.getData();
            this.doAction(scannedBarcode);
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
