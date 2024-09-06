package com.blazma.logistics.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Container {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("type")
    @Expose
    public String type;
}
