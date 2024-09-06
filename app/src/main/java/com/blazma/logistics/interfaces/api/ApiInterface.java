package com.blazma.logistics.interfaces.api;

import com.blazma.logistics.model.money.MoneyTransferResponse;
import com.blazma.logistics.model.money.VerifyLocationOtpResponse;
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

import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    // Login
    @POST("driver/login")
    Single<LoginResponse> driverLogin(@Body RequestBody body);

    // Login with mobile
    @POST("driver/loginWithMobile")
    Single<LoginResponse> driverLoginWithMobile(@Body RequestBody body);

    // Driver Tasks (List Task)
    @POST("driver/tasks")
    Single<DriverTasksResponse> getDriverTaskList(@Body RequestBody body);

    // No Samples (List Task)
    @POST("task/nosamples")
    Single<DefaultResponse> noSample(@Body RequestBody body);

    // Send Samples (List Task)
    @POST("samples/add")
    Single<DefaultResponse> sendSamples(@Body HashMap<String, Object> body);

    // Send Samples (List Task)
    @POST("samples/box/add")
    Single<DefaultResponse> sendBoxes(@Body HashMap<String, Object> body);

    // Send Signature (List Task)
    @POST("task/collect")
    Single<DefaultResponse> changeTaskStatusToCollect(@Body RequestBody body);

    // Get Samples (Freezer Task)
    @POST("samples/bag")
    Single<SampleResponse> getSamplesOfBag(@Body RequestBody body);

    // Get Samples (Freezer Task)
    @POST("samples/bag/type")
    Single<SampleResponse> getSamplesOfBagType(@Body RequestBody body);

    // Submit Samples (Freezer Task)
    @POST("samples/container/add")
    Single<DefaultResponse> submitSamples(@Body RequestBody body);

    // Close Freezer task (Freezer Task)
    @POST("task/close")
    Single<DefaultResponse> closeTask(@Body RequestBody body);

    // Remove Bag from container (Deliver Task)
    @POST("bag/container/remove")
    Single<DefaultResponse> removeBagFromContainer(@Body RequestBody body);

    // Upload the driver signature image
    @Multipart
    @POST("task/collect")
    Single<DefaultResponse> submitDriverSignature(@Part("task_id") RequestBody taskId, @Part MultipartBody.Part imageBody);

    // Upload the driver signature image
    @Multipart
    @POST("task/collect")
    Single<DefaultResponse> submitBoxDriverSignature(@Part("task_id") RequestBody taskId, @Part("box_count") RequestBody boxCountBody, @Part("sample_count") RequestBody sampleCountBody, @Part MultipartBody.Part imageBody);

    @Multipart
    @POST("task/collect")
    Single<DefaultResponse> submitDriverWithoutSignature(@Part("task_id") RequestBody taskId);

    @Multipart
    @POST("task/collect")
    Single<DefaultResponse> submitBoxDriverWithoutSignature(@Part("task_id") RequestBody taskId, @Part("box_count") RequestBody boxCountBody, @Part("sample_count") RequestBody sampleCountBody);

    // Remove Bag from container (Deliver Task)
    @POST("driver/notifications")
    Single<NotificationResponse> getNotifications(@Body RequestBody body);

    // Remove Bag from container (Deliver Task)
    @POST("driver/profile")
    Single<LoginResponse> getProfile(@Body RequestBody body);

    // Close Freezer
    @POST("task/freezer")
    Single<DefaultResponse> closeFreezer(@Body RequestBody body);

    // Freezer Out
    @POST("task/freezer/out")
    Single<DefaultResponse> freezerOut(@Body RequestBody body);

    // Get Samples Per task
    @POST("samples/list")
    Single<SampleResponse> getSamplesPerTask(@Body RequestBody body);

    // Get Container List
    @POST("task/containers/bag")
    Single<ContainerListResponse> getContainersOfTask(@Body RequestBody body);

    // Upload the driver signature image
    @Multipart
    @POST("task/close")
    Single<DefaultResponse> closeTaskWithoutSignature(@Part("task_id") RequestBody taskId, @Part("deliver_confirmationCode") RequestBody confirmCode);

    // Get Bag List
    @POST("container/bags")
    Single<BagListResponse> getBagsOfContainer(@Body RequestBody body);

    // Send Location Info
    @POST("task/location/check")
    Single<DefaultResponse> sendLocationInfo(@Body RequestBody body);

    // Get OutFreezer Task List
    @POST("driver/client/tasks")
    Single<OutFreezerTaskResponse> getOutFreezerTask(@Body RequestBody body);

    // Check OutFreezer Location
    @POST("tasks/location/check")
    Single<OutFreezerLocationCheckResponse> checkOutFreezerLocation(@Body RequestBody body);

    // Release Car
    @POST("car/release")
    Single<DefaultResponse> releaseCar(@Body RequestBody body);

    //upload images
    @POST("driver/car/images")
    @Multipart
    Single<DefaultResponse> uploadImages( @Part("driver_id") RequestBody driverId,
                                        @Part("car_id") RequestBody carId,
                                        @Part MultipartBody.Part signature,
                                        @Part MultipartBody.Part image1,
                                        @Part MultipartBody.Part image2,
                                        @Part MultipartBody.Part image3,
                                        @Part MultipartBody.Part image4,
                                        @Part MultipartBody.Part image5,
                                        @Part MultipartBody.Part image6);

    // Upload the driver signature image
    @Multipart
    @POST("tasks/close")
    Single<DefaultResponse> closeFreezerOutTaskWithoutSignature(@Part("tasks") RequestBody taskId, @Part("deliver_confirmationCode") RequestBody confirmCode);

    // Close In Freezer to Out Freezer
    @POST("tasks/freezer/out")
    Single<DefaultResponse> closeInFreezer(@Body RequestBody body);

    @POST("driver/location")
    Single<DefaultResponse> updateLocation(@Body HashMap<String, Object> body);

    @POST("driver/terms/accept")
    @Multipart
    Single<DefaultResponse> acceptTerms(@Part("driver_id") RequestBody driverId, @Part MultipartBody.Part signature);

    @POST("driver-schedule")
    Single<ScheduleResponse> getScheduleList(@Body HashMap<String, Object> body);

    @GET("driver/terms/get")
    Single<TermsResponse> getTerms();

    // Swap Tasks (List Task)
    @POST("swap/list")
    Single<SwapTaskResponse> getSwapTaskList(@Body RequestBody body);

    // Reject Swap task
    @POST("swap/reject")
    Single<DefaultResponse> rejectSwapTask(@Body RequestBody body);

    // Accept Swap task
    @POST("swap/accept")
    Single<DefaultResponse> acceptSwapTask(@Body RequestBody body);

    // Create the swap
    @POST("swap/create")
    Single<DefaultResponse> createSwapTask(@Body RequestBody body);

    // Money Transfer List
    @POST("money/transfer/list")
    Single<MoneyTransferResponse> getMoneyTransferList(@Body RequestBody body);

    // Verify FromLocation OTP
    @POST("money/transfer/otp/from/verifiy")
    Single<VerifyLocationOtpResponse> verifyFromLocationOTP(@Body RequestBody body);

    // Verify ToLocation OTP
    @POST("money/transfer/otp/to/verifiy")
    Single<VerifyLocationOtpResponse> verifyToLocationOTP(@Body RequestBody body);

    //Task Confirm by Driver
    @FormUrlEncoded
    @POST("driver/tasks/confirm")
    Single<DefaultResponse> confirmTaskByDriver(@Field("task_ids[]") List<Integer> taskIds);

    //From location Confirm by Driver
    @FormUrlEncoded
    @POST("driver/task/fromlocation/confirm")
    Single<DefaultResponse> fromLocationConfirmByDriver(@Field("task_id") int taskId,@Field("driver_id") int driverId,
                                                        @Field("from_location") int fromLocation,
                                                        @Field("lat") double lat,@Field("lng") double lng);


    //To location Confirm by Driver
    @FormUrlEncoded
    @POST("driver/task/tolocation/confirm")
    Single<DefaultResponse> toLocationConfirmByDriver(@Field("task_ids[]") List<Integer> taskId, @Field("driver_id") int driverId,
                                                      @Field("to_location") int fromLocation,
                                                      @Field("lat") double lat, @Field("lng") double lng);


    //Accept all schedule by Driver
    @FormUrlEncoded
    @POST("driver/schedule/acceptall")
    Single<DefaultResponse> acceptAllSchedule( @Field("driver_id") int driverId,
                                                      @Field("car_id") int carId);
}
