package com.abb.flowable.api.domain;

/**
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public enum TaskState {
    WAITING((byte)0),
    PROCESSING((byte)1),
    END((byte)2);
    private byte type;

    TaskState(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
