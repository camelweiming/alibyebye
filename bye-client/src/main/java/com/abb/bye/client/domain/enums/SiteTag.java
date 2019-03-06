package com.abb.bye.client.domain.enums;

/**
 * @author cenpeng.lwm
 * @since 2018/10/2
 */
public enum SiteTag {
    APP(2, "TEST"),
    ONLINE(4, "ONLINE"),
    IOS(8, "IOS"),
    ANDROID(16, "ANDROID"),
    H5_SUPPORT(32, "H5");
    private long value;
    private String name;

    SiteTag(long value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getValue() {
        return value;
    }

    public static SiteTag valueOf(long value) {
        for (SiteTag t : values()) {
            if (t.getValue() == value) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown");
    }
}
