package com.blazma.logistics.model.swap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SampleBarcode {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("barcode_id")
    @Expose
    public String barcodeId;

    @SerializedName("location_id")
    @Expose
    public Integer locationId;

    @SerializedName("task_id")
    @Expose
    public Integer taskId;

    @SerializedName("container_id")
    @Expose
    public Integer containerId;

    @SerializedName("bag_code")
    @Expose
    public String bagCode;

    @SerializedName("temperature_type")
    @Expose
    public String temperatureType;

    @SerializedName("sample_type")
    @Expose
    public String sampleType;

    @SerializedName("status")
    @Expose
    public Integer status;

    @SerializedName("created_at")
    @Expose
    public String createdAt;

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @SerializedName("box_count")
    @Expose
    public Integer boxCount;

    @SerializedName("sample_count")
    @Expose
    public Integer sampleCount;

    @SerializedName("confirmed_by_client")
    @Expose
    public String confirmedByClient;

    @SerializedName("confirmed_by")
    @Expose
    public String confirmedBy;
}
