package com.blazma.logistics.model.freezer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SampleResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public List<SampleItemModel> samples = new ArrayList<>();
}
