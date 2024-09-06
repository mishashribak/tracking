package com.blazma.logistics.model.task;

import com.blazma.logistics.model.login.DriverTask;
import com.blazma.logistics.model.login.LoginData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DriverTasksResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public List<DriverTask> tasks = new ArrayList<>();


}
