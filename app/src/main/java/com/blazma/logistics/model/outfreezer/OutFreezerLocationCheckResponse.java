package com.blazma.logistics.model.outfreezer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OutFreezerLocationCheckResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;

    @SerializedName("message")
    @Expose
    public String message;
}
