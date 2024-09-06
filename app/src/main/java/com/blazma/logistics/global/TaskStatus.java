package com.blazma.logistics.global;

public enum TaskStatus {
    NEW(0), COLLECTED(1), IN_FREEZER(2), CLOSED(3), NO_SAMPLES(4), OUT_FREEZER(5);

    private int mValue;
    TaskStatus(int value) {
        this.mValue = value;
    }

    public int id(){
        return mValue;
    }
}
