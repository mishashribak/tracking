package com.blazma.logistics.model.login;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LatestTask {

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

    @SerializedName("dateString")
    @Expose
    private String taskDate = "";

    @SerializedName("timeString")
    @Expose
    private String taskTime = "";

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

    public Object getTimeOfVisit() {
        return timeOfVisit;
    }

    public void setTimeOfVisit(String timeOfVisit) {
        this.timeOfVisit = timeOfVisit;
    }

    public Object getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Object getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Object getDescription() {
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

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }
}
