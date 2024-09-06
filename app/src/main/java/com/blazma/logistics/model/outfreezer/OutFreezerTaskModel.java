package com.blazma.logistics.model.outfreezer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OutFreezerTaskModel {
    @SerializedName("to_location")
    @Expose
    public Integer toLocationId;

    @SerializedName("name")
    @Expose
    public String clientName;

    @SerializedName("arabic_name")
    @Expose
    public String arName;

    @SerializedName("takasi")
    @Expose
    public String takasi;

    @SerializedName("english_name")
    @Expose
    public String enName;


    @SerializedName("driver_confirm_to_location")
    @Expose
    public String driverConfirm;

    @SerializedName("drop_off_waiting_time")
    @Expose
    public Integer dropOffWaitingTime;

    @SerializedName("lat")
    @Expose
    public Double latitude;

    @SerializedName("lng")
    @Expose
    public Double longitude;

    @SerializedName("tasks")
    @Expose
    public List<OutFreezerTaskItemModel> taskList = new ArrayList<>();

    @SerializedName("taskIds")
    @Expose
    public List<Integer> taskIds = new ArrayList<>();

    @SerializedName("bag_codes")
    @Expose
    public List<String> bagIds = new ArrayList<>();


    public boolean isConfirmedByDriver(){
        return  (this.driverConfirm!=null && this.driverConfirm.equals("1"));
    }

}
