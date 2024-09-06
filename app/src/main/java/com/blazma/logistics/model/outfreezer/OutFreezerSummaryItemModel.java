package com.blazma.logistics.model.outfreezer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OutFreezerSummaryItemModel {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("bag_code")
    @Expose
    public String bagCode;

    @SerializedName("temperature_type")
    @Expose
    public String temperatureType;

    @SerializedName("sample_type")
    @Expose
    public String sampleType;

    @SerializedName("container_id")
    @Expose
    public int containerId;
}
