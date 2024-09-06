package com.blazma.logistics.model.swap;

import com.blazma.logistics.model.BaseModel;
import com.blazma.logistics.model.login.DriverTask;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SwapTaskResponse extends BaseModel {
    @SerializedName("data")
    @Expose
    public List<SwapTaskData> tasks = new ArrayList<>();
}
