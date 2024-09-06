package com.blazma.logistics.model.schedule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Schedule {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("client")
    @Expose
    public String client;
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("visit_hour")
    @Expose
    public String time;
    @SerializedName("from_location")
    @Expose
    public ScheduleLocation fromLocation;
    @SerializedName("to_location")
    @Expose
    public ScheduleLocation toLocation;
    @SerializedName("driver")
    @Expose
    public String driver;
}

