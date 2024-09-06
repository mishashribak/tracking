package com.blazma.logistics.model.freezer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BagItemModel {
    @SerializedName("bag_code")
    @Expose
    public String bagCode;

    @SerializedName("temperature_type")
    @Expose
    public String temperatureType;

    public Integer taskId;
}
