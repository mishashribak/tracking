package com.blazma.logistics.model.outfreezer;

import com.blazma.logistics.model.notification.NotificationModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OutFreezerTaskResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public List<OutFreezerTaskModel> data = new ArrayList<>();
}
