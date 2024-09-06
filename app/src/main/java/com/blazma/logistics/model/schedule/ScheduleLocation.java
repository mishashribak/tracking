package com.blazma.logistics.model.schedule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScheduleLocation {
    @SerializedName("lat")
    @Expose
    public double lat;

    @SerializedName("lng")
    @Expose
    public double lng;
    @SerializedName("name")
    @Expose
    public String name;

}

