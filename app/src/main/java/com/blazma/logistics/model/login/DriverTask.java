package com.blazma.logistics.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverTask {
    @SerializedName("from_location_name")
    @Expose
    private String fromLocationName = "";

    @SerializedName("to_location_name")
    @Expose
    private String toLocationName = "";

    @SerializedName("from_location_lat")
    @Expose
    private double fromLocationLat;

    @SerializedName("to_location_lat")
    @Expose
    private double toLocationLat;

    @SerializedName("from_location_lng")
    @Expose
    private double fromLocationLng;

    @SerializedName("to_location_lng")
    @Expose
    private double toLocationLng;

    @SerializedName("confirmed_received_by_driver")
    @Expose
    private Integer confirmedReceivedByDriver;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("from_location")
    @Expose
    private Integer fromLocation;

    @SerializedName("to_location")
    @Expose
    private Integer toLocation;

    @SerializedName("cost")
    @Expose
    private Integer cost;

    @SerializedName("billing_client")
    @Expose
    private Integer billingClient;

    @SerializedName("driver_id")
    @Expose
    private Integer driverId;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("task_type")
    @Expose
    public String taskType;

    @SerializedName("takasi")
    @Expose
    public String takasi;

    @SerializedName("time_of_visit")
    @Expose
    private String timeOfVisit = "";

    @SerializedName("start_date")
    @Expose
    private String startDate = "";

    @SerializedName("end_date")
    @Expose
    private String endDate = "";

    @SerializedName("description")
    @Expose
    private String description = "";

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("created_at")
    @Expose
    private String createdAt = "";

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @SerializedName("client_arabic_name")
    @Expose
    private String clientArabicName = "";

    @SerializedName("client_english_name")
    @Expose
    private String clientEnglishName = "";

    @SerializedName("from_mobile")
    @Expose
    private String fromMobile = "";

    @SerializedName("to_location_mobile")
    @Expose
    private String toMobile = "";

    @SerializedName("dateString")
    @Expose
    private String taskDate = "";

    @SerializedName("timeString")
    @Expose
    private String taskTime = "";

    @SerializedName("numberOfBags")
    @Expose
    public int numberOfBags = 0;

    @SerializedName("rtCount")
    @Expose
    public int rtCount = 0;

    @SerializedName("refCount")
    @Expose
    public int refCount = 0;

    @SerializedName("frzCount")
    @Expose
    public int frzCount = 0;
    @SerializedName("pickup_waiting_time")
    @Expose
    public int pickupWaitingTime = 0;
    @SerializedName("drop_off_waiting_time")
    @Expose
    public int dropOffWaitingTime = 0;

    public int getPickupWaitingTime() {
        return pickupWaitingTime;
    }

    public void setPickupWaitingTime(int pickupWaitingTime) {
        this.pickupWaitingTime = pickupWaitingTime;
    }

    public int getDropOffWaitingTime() {
        return dropOffWaitingTime;
    }

    public void setDropOffWaitingTime(int dropOffWaitingTime) {
        this.dropOffWaitingTime = dropOffWaitingTime;
    }

    @SerializedName("otp")
    @Expose
    public String otp = null;

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getFromLocationName() {
        return fromLocationName;
    }

    public void setFromLocationName(String fromLocationName) {
        this.fromLocationName = fromLocationName;
    }

    public String getToLocationName() {
        return toLocationName;
    }

    public void setToLocationName(String toLocationName) {
        this.toLocationName = toLocationName;
    }

    public double getFromLocationLat() {
        return fromLocationLat;
    }

    public void setFromLocationLat(double fromLocationLat) {
        this.fromLocationLat = fromLocationLat;
    }

    public double getToLocationLat() {
        return toLocationLat;
    }

    public void setToLocationLat(double toLocationLat) {
        this.toLocationLat = toLocationLat;
    }

    public double getFromLocationLng() {
        return fromLocationLng;
    }

    public void setFromLocationLng(double fromLocationLng) {
        this.fromLocationLng = fromLocationLng;
    }

    public double getToLocationLng() {
        return toLocationLng;
    }

    public void setToLocationLng(double toLocationLng) {
        this.toLocationLng = toLocationLng;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(Integer fromLocation) {
        this.fromLocation = fromLocation;
    }

    public Integer getToLocation() {
        return toLocation;
    }

    public void setToLocation(Integer toLocation) {
        this.toLocation = toLocation;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getBillingClient() {
        return billingClient;
    }

    public void setBillingClient(Integer billingClient) {
        this.billingClient = billingClient;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeOfVisit() {
        return timeOfVisit;
    }

    public void setTimeOfVisit(String timeOfVisit) {
        this.timeOfVisit = timeOfVisit;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getClientArabicName() {
        return clientArabicName;
    }

    public void setClientArabicName(String clientArabicName) {
        this.clientArabicName = clientArabicName;
    }

    public String getClientEnglishName() {
        return clientEnglishName;
    }

    public void setClientEnglishName(String clientEnglishName) {
        this.clientEnglishName = clientEnglishName;
    }

    public String getFromMobile() {
        return fromMobile;
    }

    public void setFromMobile(String fromMobile) {
        this.fromMobile = fromMobile;
    }

    public String getToMobile() {
        return toMobile;
    }

    public void setToMobile(String toMobile) {
        this.toMobile = toMobile;
    }

    public Integer getConfirmedReceivedByDriver() {
        return confirmedReceivedByDriver;
    }

    public boolean isConfirmedByDriver(){
        return  (this.confirmedReceivedByDriver!=null && this.confirmedReceivedByDriver==1);
    }

    public void setConfirmedReceivedByDriver(Integer confirmedReceivedByDriver) {
        this.confirmedReceivedByDriver = confirmedReceivedByDriver;
    }
}
