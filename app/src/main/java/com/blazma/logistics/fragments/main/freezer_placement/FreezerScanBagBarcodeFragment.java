package com.blazma.logistics.fragments.main.freezer_placement;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.R;
import com.blazma.logistics.databinding.FragmentScanLocationBarBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.common.TaskDetailFragment;
import com.blazma.logistics.fragments.main.common.TaskDetailSecondFlowFragment;
import com.blazma.logistics.fragments.main.list_task.ListTaskSecondSampleInfoFragment;
import com.blazma.logistics.global.UserInfo;
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

import java.util.List;

public class FreezerScanBagBarcodeFragment extends BaseFragment implements BarcodeCaptureListener {
    private String TAG = FreezerScanBagBarcodeFragment.class.getSimpleName();
    private FragmentScanLocationBarBinding mBinding;

    private DataCaptureContext dataCaptureContext;
    private BarcodeCapture barcodeCapture;
    private Camera camera;
    private DataCaptureView dataCaptureView;
    public boolean isEnableFreezerScan = false;

    private static final int CAMERA_PERMISSION_CODE = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan_location_bar, container, false);

        this.initActions();
        this.initComponent();
        this.initBarCodeScanner();

        return mBinding.getRoot();
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

    private void initComponent(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }

        mBinding.txtTitle.setText(getString(R.string.scan_biohazard_bag));
    }

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

        mBinding.viewCamera.addView(dataCaptureView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void initActions() {
        mBinding.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBinding.btnProceed.getText().equals(getString(R.string.proceed))){
                    if(UserInfo.getInstance().selectedTaskType.equals(AppConstants.TASK_STATUS_COLLECTED)){
                        if(isEnableFreezerScan){
                            UserInfo.getInstance().scannedContainerId = 0;
                            UserInfo.getInstance().scannedContainerType = "";
                        }
                        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new TaskDetailSecondFlowFragment(), R.id.frameLayout);
                    }
                    else {
                        FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new TaskDetailFragment(), R.id.frameLayout);
                    }
                } else {
                    mBinding.verifiedNotifyView.setVisibility(View.GONE);
                    barcodeCapture.setEnabled(true);
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

    private void doSuccessAction(String scannedCode){
        UserInfo.getInstance().scannedBagBarCode = scannedCode;
        this.showSuccessResultView();
    }

    private void showSuccessResultView(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // Show success view
                mBinding.txtScanResult.setText(getString(R.string.bar_code_verified));
                mBinding.btnProceed.setText(getString(R.string.proceed));
                mBinding.txtScanResult.setTextColor(getResources().getColor(R.color.colorMainBgBlue));
                mBinding.imageSuccess.setVisibility(View.VISIBLE);
                mBinding.verifiedNotifyView.setVisibility(View.VISIBLE);

                // Stop the barcode scanning
                barcodeCapture.setEnabled(false);
            }
        });
    }

    private void showFailedResultView(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.txtScanResult.setText(getString(R.string.invalid_bag_bar_code));
                mBinding.txtScanResult.setTextColor(getResources().getColor(R.color.colorRed));
                mBinding.imageSuccess.setVisibility(View.GONE);
                mBinding.btnProceed.setText(getString(R.string.rescan));
                mBinding.verifiedNotifyView.setVisibility(View.VISIBLE);

                // Stop the barcode scanning
                barcodeCapture.setEnabled(false);
            }
        });
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
        mBinding.verifiedNotifyView.setVisibility(View.GONE);

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

    @Override
    public void onBarcodeScanned(BarcodeCapture barcodeCapture, BarcodeCaptureSession barcodeCaptureSession, FrameData frameData) {
        List<Barcode> recognizedBarCodes = barcodeCaptureSession.getNewlyRecognizedBarcodes();

        if (!recognizedBarCodes.isEmpty()){
            // Add to public store
            Barcode barcode = recognizedBarCodes.get(0);
            String scannedBarcode = barcode.getData();
            this.doSuccessAction(scannedBarcode);
        }
        else{
            Log.e(TAG, "Barcode list is empty...");
        }
    }

    @Override
    public void onObservationStarted(BarcodeCapture barcodeCapture) {
        Log.e(TAG, "onObservationStarted");
    }

    @Override
    public void onObservationStopped(BarcodeCapture barcodeCapture) {
        Log.e(TAG, "onObservationStopped");
    }

    @Override
    public void onSessionUpdated(BarcodeCapture barcodeCapture, BarcodeCaptureSession barcodeCaptureSession, FrameData frameData) {
        Log.e(TAG, "onSessionUpdated");
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

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }
}
