package com.abb.bye.client.domain.enums;

/**
 * @author cenpeng.lwm
 * @since 2019/3/8
 */
public enum ProgrammeTag {
    FROM_SOURCE(1001, "源站复制");
    private int type;
    private String name;

    ProgrammeTag(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }
}
