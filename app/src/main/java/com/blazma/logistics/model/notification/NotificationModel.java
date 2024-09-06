package com.blazma.logistics.model.notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationModel {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("title")
    @Expose
    public String enTitle;

    @SerializedName("arabicTitle")
    @Expose
    public String arabicTitle;

    @SerializedName("description")
    @Expose
    public String enDescription;

    @SerializedName("arabicDescription")
    @Expose
    public String arabicDescription;

    @SerializedName("ago")
    @Expose
    public String enAgo;

    @SerializedName("agoArabic")
    @Expose
    public String arabicAgo;

    @SerializedName("taskType")
    @Expose
    public Integer taskType;
}
