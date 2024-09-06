package com.blazma.logistics.interfaces.api;

import android.content.Context;

import com.blazma.logistics.global.HttpUtils;
import com.blazma.logistics.global.MyPreferenceManager;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.login.Car;
import com.blazma.logistics.model.login.DriverTask;
import com.blazma.logistics.model.money.MoneyTransferResponse;
import com.blazma.logistics.model.money.VerifyLocationOtpResponse;
import com.blazma.logistics.model.outfreezer.OutFreezerTaskModel;
import com.blazma.logistics.model.swap.SwapTaskResponse;
import com.blazma.logistics.model.terms.TermsResponse;
import com.blazma.logistics.model.freezer.BagListResponse;
import com.blazma.logistics.model.freezer.SampleResponse;
import com.blazma.logistics.model.login.LoginResponse;
import com.blazma.logistics.model.notification.NotificationResponse;
import com.blazma.logistics.model.outfreezer.OutFreezerLocationCheckResponse;
import com.blazma.logistics.model.outfreezer.OutFreezerTaskResponse;
import com.blazma.logistics.model.schedule.ScheduleResponse;
import com.blazma.logistics.model.task.ContainerListResponse;
import com.blazma.logistics.model.task.DriverTasksResponse;
import com.blazma.logistics.model.task.DefaultResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class ApiManager {
    private ApiInterface apiInterface;
    private Context mContext;
    private MyPreferenceManager preferenceManager;

    private static ApiManager instance = null;

   // private String BASE_URL = "https://gosample.com/api/";
    private String BASE_URL = "http://188.166.189.227/api/";

    public ApiManager(Context context){
        mContext = context;
        apiInterface = HttpUtils.getRetrofit(BASE_URL).create(ApiInterface.class);
        preferenceManager = MyPreferenceManager.getInstance(mContext);
    }

    // Login with password
    public Single<LoginResponse> loginWithPassword(String username, String password){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("mobile", username);
            requestParam.put("password", password);
            requestParam.put("fcmToken", preferenceManager.getFCMToken());
            requestParam.put("language", "en");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.driverLoginWithMobile(body);
    }

    // Task List
    public Single<DriverTasksResponse> getTaskList(int driverId, String taskType){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("driver_id", driverId);
            requestParam.put("status", taskType);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.getDriverTaskList(body);
    }

    public Single<DefaultResponse> confirmTaskByDriver(List<Integer> taskIDs){

        return apiInterface.confirmTaskByDriver(taskIDs);
    }

    public Single<DefaultResponse> noSample(int taskID){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskID);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.noSample(body);
    }

    public Single<DefaultResponse> sendSamples(int taskID, int locationID, List<String> barCodes, String temperature, String bagCode, String sampleType){

        HashMap<String, Object> params = new HashMap<>();
        try {
            params.put("task_id", taskID);
            params.put("location_id", locationID);
            params.put("barcode_ids", barCodes);
            params.put("temperature_type", temperature);
            params.put("sample_type", sampleType);
            params.put("bag_code", bagCode);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return apiInterface.sendSamples(params);
    }

    public Single<DefaultResponse> sendBoxes(int taskID, int locationID, List<String> barCodes, String temperature, String bagCode, String sampleType, String boxCount, String sampleCount){

        HashMap<String, Object> params = new HashMap<>();
        try {
            params.put("task_id", taskID);
            params.put("location_id", locationID);
            params.put("barcode_ids", barCodes);
            params.put("temperature_type", temperature);
            params.put("sample_type", sampleType);
            params.put("bag_code", bagCode);
            params.put("box_count", boxCount);
            params.put("sample_count", sampleCount);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return apiInterface.sendBoxes(params);
    }

    public Single<DefaultResponse> freezerOut(int taskID){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskID);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.freezerOut(body);
    }

    public Single<SampleResponse> getSamplesOfBag(int taskID, String bagCode){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskID);
            requestParam.put("bag_code", bagCode);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.getSamplesOfBag(body);
    }

    public Single<SampleResponse> getSamplesOfBagWithType(int taskID, String bagCode, String containerType){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskID);
            requestParam.put("bag_code", bagCode);
            requestParam.put("container_type", containerType);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.getSamplesOfBag(body);
    }

    public Single<DefaultResponse> submitSamples(int taskID, int containerID, String bagCode){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskID);
            requestParam.put("container_id", containerID);
            requestParam.put("bag_code", bagCode);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.submitSamples(body);
    }

    public Single<DefaultResponse> closeTask(int taskID){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskID);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.closeTask(body);
    }

    public Single<DefaultResponse> closeFreezer(int taskID){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskID);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.closeFreezer(body);
    }

    public Single<DefaultResponse> removeBagFromContainer(int taskID, String bagCode, int containerId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskID);
            requestParam.put("bag_code", bagCode);
            requestParam.put("container_id", containerId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.removeBagFromContainer(body);
    }

    public Single<DefaultResponse> submitDriverSignature(int taskId, File file){
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part signBody = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        RequestBody idBody = RequestBody.create(String.valueOf(taskId), MediaType.parse("multipart/form-data"));

        return apiInterface.submitDriverSignature(idBody, signBody);
    }

    public Single<DefaultResponse> submitBoxDriverSignature(int taskId, int boxCount, int sampleCount, File file){
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part signBody = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        RequestBody idBody = RequestBody.create(String.valueOf(taskId), MediaType.parse("multipart/form-data"));
        RequestBody boxCountBody = RequestBody.create(String.valueOf(boxCount), MediaType.parse("multipart/form-data"));
        RequestBody sampleCountBody = RequestBody.create(String.valueOf(sampleCount), MediaType.parse("multipart/form-data"));

        return apiInterface. submitBoxDriverSignature(idBody, boxCountBody, sampleCountBody, signBody);
    }

    public Single<DefaultResponse> submitDriverWithoutSignature(int taskId){

        RequestBody idBody = RequestBody.create(String.valueOf(taskId), MediaType.parse("multipart/form-data"));
        return apiInterface.submitDriverWithoutSignature(idBody);
    }

    public Single<DefaultResponse> submitBoxDriverWithoutSignature(int taskId, int boxCount, int sampleCount){

        RequestBody idBody = RequestBody.create(String.valueOf(taskId), MediaType.parse("multipart/form-data"));
        RequestBody boxCountBody = RequestBody.create(String.valueOf(boxCount), MediaType.parse("multipart/form-data"));
        RequestBody sampleCountBody = RequestBody.create(String.valueOf(sampleCount), MediaType.parse("multipart/form-data"));

        return apiInterface.submitBoxDriverWithoutSignature(idBody, boxCountBody, sampleCountBody);
    }


    public Single<DefaultResponse> closeTaskWithoutSignature(int taskID, String confirmCode){
        // MultipartBody.Part is used to send also the actual file name
        RequestBody idBody = RequestBody.create(String.valueOf(taskID), MediaType.parse("multipart/form-data"));
        RequestBody codeBody = RequestBody.create(confirmCode, MediaType.parse("multipart/form-data"));

        return apiInterface.closeTaskWithoutSignature(idBody, codeBody);
    }

    public Single<NotificationResponse> getNotifications(int driverId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("driver_id", driverId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.getNotifications(body);
    }

    public Single<LoginResponse> getProfile(int driverId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("driver_id", driverId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.getProfile(body);
    }

    public Single<SampleResponse> getSamplesPerTask(int taskId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.getSamplesPerTask(body);
    }

    public Single<ContainerListResponse> getContainersOfTask(int taskId, String bagCode){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskId);
            requestParam.put("bag_code", bagCode);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.getContainersOfTask(body);
    }

    public Single<BagListResponse> getBagsOfContainer(int taskId, int containerId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskId);
            requestParam.put("container_id", containerId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.getBagsOfContainer(body);
    }

    public Single<DefaultResponse> sendLocationInfo(int taskId, int locationId, String taskToken, String locationType){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskId);
            requestParam.put("location_id", locationId);
            requestParam.put("takasi_number", taskToken);
            requestParam.put("location_type", locationType);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.sendLocationInfo(body);
    }

    public Single<OutFreezerTaskResponse> getOutFreezerTasks(int driverId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("driver_id", driverId);
            requestParam.put("status", "OUT_FREEZER");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.getOutFreezerTask(body);
    }

    public Single<OutFreezerTaskResponse> getInFreezerTasks(int driverId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("driver_id", driverId);
            requestParam.put("status", "IN_FREEZER");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.getOutFreezerTask(body);
    }

    public Single<OutFreezerLocationCheckResponse> checkOutFreezerLocation(List<Integer> taskIds, String taskToken, int toLocationId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("tasks", taskIds);
            requestParam.put("takasi_number", taskToken);
            requestParam.put("to_location", toLocationId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.checkOutFreezerLocation(body);
    }

    public Single<DefaultResponse> releaseCar(int carId, int driverId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("car_id", carId);
            requestParam.put("driver_id", driverId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.releaseCar(body);
    }

    public Single<DefaultResponse> acceptTerms(int driverId, File signFile){
        RequestBody driverIdBody = RequestBody.create(String.valueOf(driverId), MediaType.parse("multipart/form-data"));
        RequestBody requestFile = RequestBody.create(signFile, MediaType.parse("multipart/form-data"));
        MultipartBody.Part signBody = MultipartBody.Part.createFormData("signature", signFile.getName(), requestFile);
        return apiInterface.acceptTerms(driverIdBody, signBody);
    }

    public Single<DefaultResponse> uploadImages(int carId, int driverId, File signFile, File file1, File file2, File file3, File file4, File file5, File file6){
        RequestBody carIdBody = RequestBody.create(String.valueOf(carId), MediaType.parse("multipart/form-data"));
        RequestBody driverIdBody = RequestBody.create(String.valueOf(driverId), MediaType.parse("multipart/form-data"));

        RequestBody requestFile = RequestBody.create(signFile, MediaType.parse("multipart/form-data"));
        MultipartBody.Part signBody = MultipartBody.Part.createFormData("signature", signFile.getName(), requestFile);

        MultipartBody.Part body1 = null;
        MultipartBody.Part body2 = null;
        MultipartBody.Part body3 = null;
        MultipartBody.Part body4 = null;
        MultipartBody.Part body5 = null;
        MultipartBody.Part body6 = null;
        if(file1 != null){
            RequestBody requestFile1 = RequestBody.create(file1, MediaType.parse("multipart/form-data"));
            body1 = MultipartBody.Part.createFormData("image1", file1.getName(), requestFile1);
        }
        if(file2 != null){
            RequestBody requestFile2 = RequestBody.create(file2, MediaType.parse("multipart/form-data"));
            body2 = MultipartBody.Part.createFormData("image2", file2.getName(), requestFile2);
        }
        if(file3 != null){
            RequestBody requestFile3 = RequestBody.create(file3, MediaType.parse("multipart/form-data"));
            body3 = MultipartBody.Part.createFormData("image3", file3.getName(), requestFile3);
        }
        if(file4 != null){
            RequestBody requestFile4 = RequestBody.create(file4, MediaType.parse("multipart/form-data"));
            body4 = MultipartBody.Part.createFormData("image4", file4.getName(), requestFile4);
        }
        if(file5 != null){
            RequestBody requestFile5 = RequestBody.create(file5, MediaType.parse("multipart/form-data"));
            body5 = MultipartBody.Part.createFormData("image5", file5.getName(), requestFile5);
        }
        if(file6 != null){
            RequestBody requestFile6 = RequestBody.create(file6, MediaType.parse("multipart/form-data"));
            body6 = MultipartBody.Part.createFormData("image6", file6.getName(), requestFile6);
        }

        return apiInterface.uploadImages(driverIdBody, carIdBody, signBody, body1, body2, body3, body4, body5, body6);
    }

    public Single<DefaultResponse> closeFreezerOutTaskWithoutSignature(List<Integer> taskIDs, String confirmCode){
//        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));

        // MultipartBody.Part is used to send also the actual file name
//        MultipartBody.Part signBody = MultipartBody.Part.createFormData("deliver_signature", file.getName(), requestFile);
        RequestBody idBody = RequestBody.create(String.valueOf(taskIDs), MediaType.parse("multipart/form-data"));
        RequestBody codeBody = RequestBody.create(confirmCode, MediaType.parse("multipart/form-data"));

        return apiInterface.closeFreezerOutTaskWithoutSignature(idBody, codeBody);
    }

    public Single<DefaultResponse> closeInFreezer(List<Integer> taskIds){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("tasks", taskIds);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.closeInFreezer(body);
    }

    public Single<DefaultResponse> updateLocation(String lat, String lng){

        HashMap<String, Object> params = new HashMap<>();
        try {
            int driverId = UserInfo.getInstance().loginInfo.getId();
            params.put("lat", lat);
            params.put("lng", lng);
            params.put("driver_id", driverId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return apiInterface.updateLocation(params);
    }

    public Single<TermsResponse> getTerms(){
        return apiInterface.getTerms();
    }

    public Single<ScheduleResponse> getScheduleList(){
        HashMap<String, Object> params = new HashMap<>();
        try {
            int driverId = UserInfo.getInstance().loginInfo.getId();
            params.put("driver_id", driverId);
            params.put("day_of_week",getDayOfWeek());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return apiInterface.getScheduleList(params);
    }

    private String getDayOfWeek(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfWeek = simpleDateFormat.format(new Date());

        // Capitalize the first letter and make the rest lowercase
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();


        return  dayOfWeek;
    }

    // Swap Task List
    public Single<SwapTaskResponse> getSwapTaskList(int driverId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("driver_id", driverId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.getSwapTaskList(body);
    }

    // Reject the Swap task
    public Single<DefaultResponse> rejectSwapTask(int swapTaskId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("swap_id", swapTaskId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.rejectSwapTask(body);
    }

    // Accept the Swap task
    public Single<DefaultResponse> acceptSwapTask(int swapTaskId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("swap_id", swapTaskId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.acceptSwapTask(body);
    }

    // Create the swap
    public Single<DefaultResponse> createSwap(int driverId, int taskId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("driver_id", driverId);
            requestParam.put("task_id", taskId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.createSwapTask(body);
    }

    // Money Transfer Task List
    public Single<MoneyTransferResponse> getMoneyTransferTaskList(int driverId){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("driver_id", driverId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.getMoneyTransferList(body);
    }

    // Verify from_location_otp
    public Single<VerifyLocationOtpResponse> verifyFromLocationOTP(int taskId, String otp){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskId);
            requestParam.put("from_location_otp", otp);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.verifyFromLocationOTP(body);
    }

    // Verify to_location_otp
    public Single<VerifyLocationOtpResponse> verifyToLocationOTP(int taskId, String otp){
        JSONObject requestParam = new JSONObject();
        try {
            requestParam.put("task_id", taskId);
            requestParam.put("to_location_otp", otp);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body
        RequestBody body = RequestBody.create(requestParam.toString(), MediaType.parse("application/json; charset=utf-8"));

        return apiInterface.verifyToLocationOTP(body);
    }

    //Update from location start by driver
    public Single<DefaultResponse> reachFromLocationByDriver(DriverTask driverTask){

        int driverId = UserInfo.getInstance().loginInfo.getId();
        return apiInterface.fromLocationConfirmByDriver(driverTask.getId(),driverId,driverTask.getFromLocation(),driverTask.getFromLocationLat(),driverTask.getFromLocationLng());
    }
    //Update to location end by driver
    public Single<DefaultResponse> reachToLocationByDriver(OutFreezerTaskModel outFreezerTaskModel){

        int driverId = UserInfo.getInstance().loginInfo.getId();
        return apiInterface.toLocationConfirmByDriver(outFreezerTaskModel.taskIds,driverId,outFreezerTaskModel.toLocationId,outFreezerTaskModel.latitude,outFreezerTaskModel.longitude);
    }

    //Update to location end by driver
    public Single<DefaultResponse> acceptAllSchedule(){

        int driverId = UserInfo.getInstance().loginInfo.getId();
        Car car = UserInfo.getInstance().loginInfo.getCar();
        return apiInterface.acceptAllSchedule(driverId,car.getId());
    }
}
