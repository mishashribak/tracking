package com.blazma.logistics.global;

import android.content.Context;
import android.location.Location;

import com.blazma.logistics.model.login.DriverTask;
import com.blazma.logistics.model.login.LoginData;
import com.blazma.logistics.model.money.MoneyTransferTask;
import com.blazma.logistics.model.outfreezer.OutFreezerTaskModel;
import com.blazma.logistics.model.swap.SwapTaskData;
import com.blazma.logistics.model.task.SampleBarCodeModel;
import com.blazma.logistics.utilities.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    private static UserInfo instance = null;

    public static UserInfo getInstance(){
        if (instance == null){
            instance = new UserInfo();
        }

        return instance;
    }

    // User data
    public LoginData loginInfo;

    // Main Task Type (List Task, Freezer, Delivery)
    public String selectedTaskType = "NEW"; // COLLECTED, IN_FREEZER

    // Selected Task
    public DriverTask selectedTask = null;

    // Inputed Box Count
    public int boxCount = 0;

    // Inputed Sample Count
    public int sampleCount = 0;

    // Saved File Name
    public String signatureFileName = "";

    // Selected OutFreezer Task
    public OutFreezerTaskModel selectedOutFreezerTask = null;

    // Selected Container Type
    public int selectedContainerType = 0;

    // Scanned Bag Bar Code
    public String scannedBagBarCode = null;

    // Scanned Container Bar Code
    public int scannedContainerId = 0;

    // Scanned Container Type
    public String scannedContainerType = "";

    // Selected Sample TYpe
    public String selectedSampleType = "";

    // Scanned Location ID
    public String scannedLocationID = null;
    public String scannedLocationName = "";
    public int fromLocationID = 0;
    public int toLocationID = 0;

    // Scanned Bar codes list
    public List<SampleBarCodeModel> scannedBarCodes = new ArrayList<>();

    // Driver Location
    public Location driverLocation = null;

    public void addNewBarCode(SampleBarCodeModel newBarCode){
        UserInfo.getInstance().scannedBarCodes.add(newBarCode);
    }

    public void removeBarCode(int position){
        UserInfo.getInstance().scannedBarCodes.remove(position);
    }

    public void saveLoginInfo(Context context, String userName, String password){
        MyPreferenceManager.getInstance().put(AppConstants.KEY_USERNAME, userName);
        MyPreferenceManager.getInstance().put(AppConstants.KEY_PASSWORD, password);
    }

    public String getSelectedContainerValue(int selectedIndex){
        switch (selectedIndex){
            case 1: return AppConstants.CONTAINER_REFRIGERATE;
            case 2: return AppConstants.CONTAINER_FROZEN;
            default: return AppConstants.CONTAINER_ROOM;
        }
    }

    public SwapTaskData selectedSwapTask = null;
    public MoneyTransferTask selectedMoneyTask = null;
}
