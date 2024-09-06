package com.blazma.logistics.model.terms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TermsResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public TermsData data;
}
