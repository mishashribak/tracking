package com.blazma.logistics.fragments.main.outfreezer;

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
import com.blazma.logistics.databinding.FragmentOutfreezerScanBagsBinding;
import com.blazma.logistics.databinding.FragmentSecondScannerBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.list_task.DriverSignatureFragment;
import com.blazma.logistics.fragments.main.list_task.ListTaskBagScanFragment;
import com.blazma.logistics.fragments.main.list_task.ListTaskBarcodeScanFragment;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.outfreezer.OutFreezerSummaryItemModel;
import com.blazma.logistics.model.outfreezer.OutFreezerTaskItemModel;
import com.blazma.logistics.model.outfreezer.OutFreezerTaskModel;
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

import java.util.ArrayList;
import java.util.List;

public class OutFreezerScannedBagsFragment extends BaseFragment implements BarcodeCaptureListener {

    private String TAG = OutFreezerScannedBagsFragment.class.getSimpleName();
    private FragmentOutfreezerScanBagsBinding mBinding;

    private CommonBarCodeAdapter commonBarCodeAdapter;
    private DataCaptureContext dataCaptureContext;
    private BarcodeCapture barcodeCapture;
    private Camera camera;
    private DataCaptureView dataCaptureView;
    private List<SampleBarCodeModel> dataList;

    private static final int CAMERA_PERMISSION_CODE = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_outfreezer_scan_bags, container, false);

        initData();
        initComponent();
        initBarCodeScanner();
        setData();
        updateLayout();

        return mBinding.getRoot();
    }

    private void initData() {
        List<SampleBarCodeModel> convertedData = new ArrayList<>();

        for(int i = 0; i < UserInfo.getInstance().selectedOutFreezerTask.taskList.size(); i++){
            OutFreezerTaskItemModel taskData = UserInfo.getInstance().selectedOutFreezerTask.taskList.get(i);
            for(int j = 0; j < taskData.bagList.size(); j++){
                OutFreezerSummaryItemModel item = taskData.bagList.get(j);

                boolean isExistedBagCode = false;
                for(int m = 0; m < convertedData.size(); m++){
                    if(convertedData.get(m).code.equals(item.bagCode)){
                        isExistedBagCode = true;
                    }
                }

                if(!isExistedBagCode){
                    convertedData.add(new SampleBarCodeModel(item.bagCode, item.temperatureType, item.sampleType));
                }
            }
        }

        dataList = convertedData;
    }

    private void initComponent(){

        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }

        mBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    OutFreezerSignatureFragment outFreezerSignatureFragment = new OutFreezerSignatureFragment();
                    FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), outFreezerSignatureFragment, R.id.frameLayout);
            }
        });

        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.back();
            }
        });
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

        mBinding.viewCamera.addView(dataCaptureView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void setData(){
        if(commonBarCodeAdapter == null){
            commonBarCodeAdapter = new CommonBarCodeAdapter(mActivity, false, false, new CommonBarCodeAdapter.OnItemClickListener() {
                @Override
                public void onClickDelete(int position) {
                }
            });
        }

        commonBarCodeAdapter.setData(dataList);
        mBinding.rvListBarCodes.setAdapter(commonBarCodeAdapter);
        updateLayout();
    }

    private void doAction(String scannedBarcode){

        for(int i = 0; i < dataList.size(); i++){
            if(dataList.get(i).code.equals(scannedBarcode)){
                dataList.remove(i);
                break;
            }
        }

        commonBarCodeAdapter.setData(dataList);
        updateLayout();

        this.adjustBarCodeCapture();
    }

    private void updateLayout(){
        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(dataList.size() == 0){
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
