package com.blazma.logistics.model.task;

import java.util.Calendar;
import java.util.Date;

public class SampleBarCodeModel implements Comparable<SampleBarCodeModel>{
    public String code = ""; // Bar Code
    public String container = ""; // ROOM, REFRIGERATE, FROZEN
    public String sampleType = "";
    public int count = 1;
    public Date scanDate;

    public SampleBarCodeModel(String pBarCode, String pContainer, String pSampleType){
        code        = pBarCode;
        container   = pContainer;
        sampleType  = pSampleType;
        count       = 1;
        scanDate    = Calendar.getInstance().getTime();
    }

    public SampleBarCodeModel(String pBarCode, String pContainer, String pSampleType, int barcodeCount, Date date){
        code        = pBarCode;
        container   = pContainer;
        sampleType  = pSampleType;
        count       = barcodeCount;
        scanDate    = date;
    }

    @Override
    public int compareTo(SampleBarCodeModel o) {
        if (scanDate == null || o.scanDate == null)
            return 0;
        return scanDate.compareTo(o.scanDate);
    }
}
