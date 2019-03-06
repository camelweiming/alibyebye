package com.abb.bye.client.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author cenpeng.lwm
 * @since 2018/10/26
 */
public class SiteConfig {
    private String iosAppUrl;
    private String itunesUrl;
    private String androidAppUrl;
    private String iosSchema;
    private String androidSchema;
    private String androidPkg;
    private String guideImg;
    private String guideInstalledImg;
    private String guideTitle;
    private String guideDesc;

    public String getItunesUrl() {
        return itunesUrl;
    }

    public void setItunesUrl(String itunesUrl) {
        this.itunesUrl = itunesUrl;
    }

    public String getIosAppUrl() {
        return iosAppUrl;
    }

    public void setIosAppUrl(String iosAppUrl) {
        this.iosAppUrl = iosAppUrl;
    }

    public String getAndroidAppUrl() {
        return androidAppUrl;
    }

    public void setAndroidAppUrl(String androidAppUrl) {
        this.androidAppUrl = androidAppUrl;
    }

    public String getIosSchema() {
        return iosSchema;
    }

    public void setIosSchema(String iosSchema) {
        this.iosSchema = iosSchema;
    }

    public String getAndroidSchema() {
        return androidSchema;
    }

    public void setAndroidSchema(String androidSchema) {
        this.androidSchema = androidSchema;
    }

    public String getAndroidPkg() {
        return androidPkg;
    }

    public void setAndroidPkg(String androidPkg) {
        this.androidPkg = androidPkg;
    }

    public String getGuideImg() {
        return guideImg;
    }

    public void setGuideImg(String guideImg) {
        this.guideImg = guideImg;
    }

    public String getGuideInstalledImg() {
        return guideInstalledImg;
    }

    public void setGuideInstalledImg(String guideInstalledImg) {
        this.guideInstalledImg = guideInstalledImg;
    }

    public String getGuideTitle() {
        return guideTitle;
    }

    public void setGuideTitle(String guideTitle) {
        this.guideTitle = guideTitle;
    }

    public String getGuideDesc() {
        return guideDesc;
    }

    public void setGuideDesc(String guideDesc) {
        this.guideDesc = guideDesc;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
