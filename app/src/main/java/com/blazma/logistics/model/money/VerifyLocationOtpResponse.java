package com.blazma.logistics.model.money;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyLocationOtpResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public String data;
}
