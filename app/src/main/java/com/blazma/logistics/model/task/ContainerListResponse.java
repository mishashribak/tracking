package com.blazma.logistics.model.task;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ContainerListResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public List<ContainerModel> containers = new ArrayList<>();
}
