package com.blazma.logistics.model.task;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContainerModel {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("car_id")
    @Expose
    public Integer carId;

    @SerializedName("correctContainer")
    @Expose
    public boolean isCorrectContainer;
}
