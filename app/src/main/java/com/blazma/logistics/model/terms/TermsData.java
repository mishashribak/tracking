package com.blazma.logistics.model.terms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TermsData {
    @SerializedName("arabicLink")
    @Expose
    public String arabicLink;

    @SerializedName("englishLink")
    @Expose
    public String englishLink;
}
