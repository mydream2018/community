package com.code.community.enums;

public enum  NotificationStatusEnum {
    UNREAD(0),
    READ(1);
    private long status;

    public long getStatus() {
        return status;
    }

    NotificationStatusEnum(long status){
        this.status = status;
    }
}
