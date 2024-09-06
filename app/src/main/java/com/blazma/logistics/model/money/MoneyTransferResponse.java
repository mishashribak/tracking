package com.blazma.logistics.model.money;

import com.blazma.logistics.model.BaseModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MoneyTransferResponse extends BaseModel {
    @SerializedName("data")
    @Expose
    public List<MoneyTransferTask> tasks = new ArrayList<>();
}
