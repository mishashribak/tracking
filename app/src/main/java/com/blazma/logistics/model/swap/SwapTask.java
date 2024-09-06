package com.blazma.logistics.model.swap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SwapTask {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("from_location_name")
    @Expose
    public String fromLocationName = "";

    @SerializedName("to_location_name")
    @Expose
    public String toLocationName = "";

    @SerializedName("dateString")
    @Expose
    public String taskDate = "";

    @SerializedName("timeString")
    @Expose
    public String taskTime = "";

    @SerializedName("from_location")
    @Expose
    public Integer fromLocation;

    @SerializedName("to_location")
    @Expose
    public Integer toLocation;

    @SerializedName("cost")
    @Expose
    public Integer cost;

    @SerializedName("billing_client")
    @Expose
    public Integer billingClient;

    @SerializedName("driver_id")
    @Expose
    public Integer driverId;

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("task_type")
    @Expose
    public String taskType;

    @SerializedName("ayenati")
    @Expose
    public String ayenati;

    @SerializedName("takasi")
    @Expose
    public String takasi;

    @SerializedName("signature")
    @Expose
    public String signature;

    @SerializedName("time_of_visit")
    @Expose
    public String timeOfVisit;

    @SerializedName("start_date")
    @Expose
    public String startDate;

    @SerializedName("end_date")
    @Expose
    public String endDate;

    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @SerializedName("close_date")
    @Expose
    public String closeDate;

    @SerializedName("collection_date")
    @Expose
    public String collectionDate;

    @SerializedName("freezer_date")
    @Expose
    public String freezerDate;

    @SerializedName("freezer_out_date")
    @Expose
    public String freezerOutDate;

    @SerializedName("deliver_confirmationCode")
    @Expose
    public String deliverConfirmationCode;

    @SerializedName("deliver_signature")
    @Expose
    public String deliverSignature;

    @SerializedName("confirmationCode")
    @Expose
    public String confirmationCode;

    @SerializedName("deleted_at")
    @Expose
    public String deletedAt;
    @SerializedName("from_location_arrival_time")
    @Expose
    public String fromLocationArrivalTime;

    @SerializedName("to_location_arrival_time")
    @Expose
    public String toLocationArrivalTime;

    @SerializedName("box_count")
    @Expose
    public Integer boxCount;

    @SerializedName("sample_count")
    @Expose
    public Integer sampleCount = 0;

    @SerializedName("takasi_number")
    @Expose
    public Object takasiNumber;

    @SerializedName("to_takasi_number")
    @Expose
    public Object toTakasiNumber;

    @SerializedName("car_id")
    @Expose
    public Integer carId;

    @SerializedName("confirmed_by_client")
    @Expose
    public String confirmedByClient;

    @SerializedName("added_by")
    @Expose
    public String addedBy;

    @SerializedName("close_hour")
    @Expose
    public Integer closeHour;

    @SerializedName("confirmation_time")
    @Expose
    public String confirmationTime;

    @SerializedName("collect_lat")
    @Expose
    public Double collectLat;

    @SerializedName("collect_lng")
    @Expose
    public Double collectLng;

    @SerializedName("close_lat")
    @Expose
    public Double closeLat;

    @SerializedName("close_lng")
    @Expose
    public Double closeLng;

    @SerializedName("driver")
    @Expose
    public Driver driver;

    @SerializedName("samples")
    @Expose
    public List<SampleBarcode> samples = new ArrayList<>();
}
