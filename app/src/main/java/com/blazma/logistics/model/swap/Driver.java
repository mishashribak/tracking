package com.blazma.logistics.model.swap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Driver {

    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("mobile")
    @Expose
    public String mobile;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("language")
    @Expose
    public String language;

    @SerializedName("status")
    @Expose
    public Integer status;

    @SerializedName("created_at")
    @Expose
    public String createdAt;

    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @SerializedName("deleted_at")
    @Expose
    public String deletedAt;

    @SerializedName("lat")
    @Expose
    public String lat;

    @SerializedName("lng")
    @Expose
    public String lng;

    @SerializedName("termAccepted")
    @Expose
    public Integer termAccepted;
}
