package com.abb.bye.client.domain.enums;

/**
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
public enum UserRelationType {
    PARENT(1001);
    private int type;

    UserRelationType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
