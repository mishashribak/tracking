package com.blazma.logistics.model.task;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DefaultResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;

    @SerializedName("message")
    @Expose
    public String message;
}
