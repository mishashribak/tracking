package com.blazma.logistics.model.money;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MoneyTransferTask {
    @SerializedName("from_location_name")
    @Expose
    public String fromLocationName;

    @SerializedName("to_location_name")
    @Expose
    public String toLocationName;

    @SerializedName("from_location_lat")
    @Expose
    public Double fromLocationLat;

    @SerializedName("to_location_lat")
    @Expose
    public Double toLocationLat;

    @SerializedName("from_location_lng")
    @Expose
    public Double fromLocationLng;

    @SerializedName("to_location_lng")
    @Expose
    public Double toLocationLng;

    @SerializedName("from_mobile")
    @Expose
    public String fromMobile;

    @SerializedName("to_location_mobile")
    @Expose
    public String toLocationMobile;

    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("from_location_otp")
    @Expose
    public String fromLocationOtp;

    @SerializedName("to_location_otp")
    @Expose
    public String toLocationOtp;

    @SerializedName("created_at")
    @Expose
    public String createdAt;

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @SerializedName("deleted_at")
    @Expose
    public String deletedAt;

    @SerializedName("driver_id")
    @Expose
    public Integer driverId;

    @SerializedName("client_id")
    @Expose
    public Integer clientId;

    @SerializedName("from_location_id")
    @Expose
    public Integer fromLocationId;

    @SerializedName("to_location_id")
    @Expose
    public Integer toLocationId;

    @SerializedName("amount")
    @Expose
    public String amount;

    @SerializedName("client_arabic_name")
    @Expose
    public String clientArabicName;

    @SerializedName("client_english_name")
    @Expose
    public String clientEnglishName;
}
