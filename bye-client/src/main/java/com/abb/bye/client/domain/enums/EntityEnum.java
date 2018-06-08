package com.abb.bye.client.domain.enums;

/**
 * @author camelweiming@163.com
 * @since 2018/6/7
 */
public enum EntityEnum {
    TEXT(0, "文字"), PIC(1, "图片");
    private int type;
    private String name;

    EntityEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }
}
