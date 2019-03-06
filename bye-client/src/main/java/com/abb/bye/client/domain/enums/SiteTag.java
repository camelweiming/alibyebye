package com.abb.bye.client.domain.enums;

/**
 * @author cenpeng.lwm
 * @since 2018/10/2
 */
public enum SiteTag {
    APP(2, "APP"),
    ONLINE(4, "线上"),
    IOS(8, "IOS"),
    ANDROID(16, "ANDROID"),
    RUNTIME_PL_SCHEMA_URL_PARSE(32, "播放记录实时schemeUrl解析"),
    ENABLE_PL_SCHEMA_URL(64, "启用播放记录schemeUrl解析"),
    DIRECT_PLAY(128, "支持APP直接跳转播放"),
    OFF_PL_TITLE(256, "去掉播放历史标题"),
    HUA_WEI(512, "华为专用"),
    ENABLE_PLAY_SCHEME(1024, "开启外站播放跳转"),
    H5_SUPPORT(2048, "H5点播支持");
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
