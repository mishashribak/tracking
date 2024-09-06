package com.blazma.logistics.model.outfreezer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OutFreezerTaskItemModel {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("samples_summary")
    @Expose
    public List<OutFreezerSummaryItemModel> bagList = new ArrayList<>();
}
