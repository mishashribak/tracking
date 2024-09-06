package com.blazma.logistics.global;

public class EventBusMessage {
    public static class MessageType {
        public final static int GO_TO_MEDICAL_TASK      = 0X01;
        public final static int GO_TO_FREEZER_PLACEMENT = 0X02;
        public final static int ORDER_CREATED           = 0x03;
        public final static int SIGNED_OUT              = 0x04;
        public final static int NEW_ADDRESS_CREATED     = 0x05;
        public final static int SELECTED_TAILOR         = 0x06;
        public final static int UPDATED_PROFILE         = 0x07;
        public final static int LANGUAGE_UPDATED        = 0x08;
        public final static int HYPERPAY_ASYNC_CALLBACK = 0x09;
        public final static int GO_TO_FREEZER_OUT = 0X10;
        public final static int GO_TO_SAMPLES_PULL_OUT = 0X11;
        public final static int ON_APP_BACKGROUND = 0X12;
        public final static int LOCATION = 0X13;
        public final static int GO_TO_SWAP_TASK         = 0x14;
        public final static int REJECT_SWAP_TASK        = 0x15;
        public final static int GO_TO_MONEY_TRANSFER    = 0x16;
    }

    private int messageType;
    private String message;
    private Object object;


    public EventBusMessage(int messageType, String json) {
        this.messageType = messageType;
        this.message = json;
    }

    public EventBusMessage(int messageType) {
        this.messageType = messageType;
    }

    public EventBusMessage(int messageType, Object data) {
        this.messageType = messageType;
        this.object = data;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String json) {
        this.message = json;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

}
