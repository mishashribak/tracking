package com.blazma.logistics.model.swap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SwapTaskData {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("task_id")
    @Expose
    public Integer taskId;

    @SerializedName("driver_id")
    @Expose
    public Integer driverId;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("created_at")
    @Expose
    public String createdAt;

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @SerializedName("task")
    @Expose
    public SwapTask task;
}
