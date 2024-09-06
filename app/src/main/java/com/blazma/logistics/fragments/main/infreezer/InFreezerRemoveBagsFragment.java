package com.blazma.logistics.fragments.main.infreezer;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.LogisticsApplication;
import com.blazma.logistics.R;
import com.blazma.logistics.adapter.BagCodeAdapter;
import com.blazma.logistics.custom.CustomConfirmDialog;
import com.blazma.logistics.databinding.FragmentRemoveBagBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.fragments.main.common.RemoveBagsFragment;
import com.blazma.logistics.fragments.main.common.TaskStatusFragment;
import com.blazma.logistics.fragments.main.freezer_placement.FreezerScanBagBarcodeFragment;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.freezer.BagItemModel;
import com.blazma.logistics.model.freezer.BagListResponse;
import com.blazma.logistics.model.outfreezer.OutFreezerTaskItemModel;
import com.blazma.logistics.model.outfreezer.OutFreezerTaskModel;
import com.blazma.logistics.model.task.DefaultResponse;
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

public class InFreezerRemoveBagsFragment  extends BaseFragment implements BarcodeCaptureListener {
    private String TAG = InFreezerRemoveBagsFragment.class.getSimpleName();
    private FragmentRemoveBagBinding mBinding;
    private DataCaptureContext dataCaptureContext;
    private BarcodeCapture barcodeCapture;
    private Camera camera;
    private DataCaptureView dataCaptureView;

    private static final int CAMERA_PERMISSION_CODE = 1001;

    private BagCodeAdapter bagCodeAdapter;
    private List<BagItemModel> bagList = new ArrayList<>();
    private String tmpScannedBagCode = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_remove_bag, container, false);

        initComponent();
        initBarCodeScanner();
        getBagList();
        setData();

        return mBinding.getRoot();
    }

    private void initComponent(){
        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
            mBinding.btnBack.setRotation(180);
        }

        mBinding.btnCloseFreezer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bagList.size() == 0){
                    CustomConfirmDialog dlg = new CustomConfirmDialog(mActivity, getString(R.string.msg_sure_close_freezers), getString(R.string.yes), getString(R.string.no), new CustomConfirmDialog.ItemClickInterface() {
                        @Override
                        public void onOK() {
                            callFreezerOut();
                        }
                    });
                    dlg.showDialog();
                    dlg.setCanceledOnTouchOutside(false);
                } else {
                    Toast.makeText(mActivity, "Please remove all bags to close freezer", Toast.LENGTH_LONG).show();
                }
            }
        });

        mBinding.btnScanFreezer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentProcess.removeCurrentFragment(mActivity.getSupportFragmentManager());
                FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new InFreezerScanFreezerFragment(), R.id.frameLayout);
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

    private void getBagList() {
        List<String> bagIds = UserInfo.getInstance().selectedOutFreezerTask.bagIds;
        bagList = new ArrayList<>();
        setData();
        for (int i = 0; i < bagIds.size(); i++){
            BagItemModel item = new BagItemModel();
            boolean isValid = false;
            item.bagCode = bagIds.get(i);
            item.temperatureType = "";

            for (int j = 0; j < UserInfo.getInstance().selectedOutFreezerTask.taskList.size(); j++){
                OutFreezerTaskItemModel outFreezerTaskModelItem = UserInfo.getInstance().selectedOutFreezerTask.taskList.get(j);
                for (int m = 0; m < outFreezerTaskModelItem.bagList.size(); m++){
                    if(outFreezerTaskModelItem.bagList.get(m).bagCode.equals(bagIds.get(i)) && outFreezerTaskModelItem.bagList.get(m).containerId == UserInfo.getInstance().scannedContainerId){
                        item.temperatureType = outFreezerTaskModelItem.bagList.get(m).temperatureType;
                        item.taskId = outFreezerTaskModelItem.id;
                        isValid = true;
                    }
                }
            }

            if(isValid){
                bagList.add(item);
            }
        }
    }

    private void setData(){
        if(bagCodeAdapter == null){
            bagCodeAdapter = new BagCodeAdapter(mActivity);
        }

        bagCodeAdapter.setData(bagList);
        mBinding.rvListBarCodes.setAdapter(bagCodeAdapter);
    }

    private void doAction(String scannedBarcode){
        this.tmpScannedBagCode = scannedBarcode;
        this.doRemoveAction();
        this.adjustBarCodeCapture();
    }

    private void doRemoveAction(){
        if (bagList.size() != 0 && tmpScannedBagCode != ""){
            for (BagItemModel bagItem : bagList){
                if(bagItem.bagCode.equals(tmpScannedBagCode)){
                    callRemoveBag(bagItem);
                    break;
                }
            }
        } else {
            if(tmpScannedBagCode == ""){
                Toast.makeText(mActivity, "Please scan bag to remove", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void callRemoveBag(BagItemModel bagItem){

        int taskId = bagItem.taskId;
        int containerId = UserInfo.getInstance().scannedContainerId;
        String bagCode = bagItem.bagCode;

        showLoading();

        LogisticsApplication.apiManager.removeBagFromContainer(taskId, bagCode, containerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                    @Override
                    public void onSuccess(@NonNull DefaultResponse defaultResponse) {
                        hideLoading();

                        if (defaultResponse.status){
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    bagList.remove(bagItem);
                                    tmpScannedBagCode = "";
                                    setData();
                                }
                            });
                        }
                        else{
                            Toast.makeText(mActivity, defaultResponse.message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoading();
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void callFreezerOut(){
        List<Integer> taskIds = UserInfo.getInstance().selectedOutFreezerTask.taskIds;

        showLoading();

        LogisticsApplication.apiManager.closeInFreezer(taskIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<DefaultResponse>() {
                    @Override
                    public void onSuccess(@NonNull DefaultResponse defaultResponse) {
                        hideLoading();

                        if (defaultResponse.status){
                            FragmentProcess.replaceFragment(mActivity.getSupportFragmentManager(), new TaskStatusFragment(), R.id.frameLayout);
                        } else {
                            Toast.makeText(mActivity, defaultResponse.message, Toast.LENGTH_SHORT).show();
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