package com.blazma.logistics.model.freezer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SampleItemModel {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("barcode_id")
    @Expose
    public String barcodeId;

    @SerializedName("temperature_type")
    @Expose
    public String type;

    @SerializedName("sample_type")
    @Expose
    public String sampleType;
}
