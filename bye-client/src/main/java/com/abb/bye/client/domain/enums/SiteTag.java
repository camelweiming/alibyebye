package com.abb.bye.client.domain.enums;

/**
 * @author cenpeng.lwm
 * @since 2018/10/2
 */
public enum SiteTag {
    APP(2, "测试环境"),
    ONLINE(4, "线上环境"),
    IOS(8, "IOS"),
    ANDROID(16, "ANDROID"),
    H5_SUPPORT(32, "H5"),
    ENABLE_SPIDER(64, "开启爬虫"),
    AUTO_PASS(128, "资源自动录入");
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
