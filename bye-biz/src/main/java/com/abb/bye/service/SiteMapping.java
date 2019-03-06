package com.abb.bye.service;

import com.abb.bye.client.domain.SiteConfig;
import com.abb.bye.client.domain.SiteDO;
import com.abb.bye.client.domain.enums.SiteTag;
import com.abb.bye.utils.CommonUtils;
import com.abb.bye.utils.Tracer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public class SiteMapping {
    private static Tracer tracer = new Tracer("SITE_MAPPING");
    private static Map<Integer, SiteDO> localSiteMapping = new HashMap<>();
    private static SiteDO DEFAULT_APP = new SiteDO("其它");
    public static final String PKG_PREFIX = "$";

    static {
        DEFAULT_APP.setTags(0);
        DEFAULT_APP.setSite(0);
    }

    public static SiteDO getAppByLocal(int siteId) {
        SiteDO app = localSiteMapping.get(siteId);
        if (app != null) {
            return app;
        }
        return localSiteMapping.getOrDefault(0, DEFAULT_APP);
    }

    public static boolean match(int siteId, SiteTag tag) {
        return match(SiteMapping.getAppByLocal(siteId), tag);
    }

    public static boolean match(SiteDO SiteDO, SiteTag tag) {
        return SiteDO.bitSet(tag);
    }

    public static List<SiteDO> list() {
        return new ArrayList<>(localSiteMapping.values());
    }

    public static void reset(List<SiteDO> apps) {
        Map<Integer, SiteDO> tmpLocalSiteMapping = new HashMap<>();
        apps.forEach(siteDO -> {
            SiteConfig appConfig = new SiteConfig();
            siteDO.setSiteConfig(appConfig);
            AndroidSetting androidSetting = parseAndroidUrl(siteDO.getAndroidUrl());
            appConfig.setAndroidAppUrl(androidSetting.url);
            appConfig.setAndroidPkg(androidSetting.pkg);

            IosSetting iosSetting = parseIosUrl(siteDO.getIosUrl());
            appConfig.setIosAppUrl(iosSetting.url);
            appConfig.setItunesUrl(iosSetting.itunesUrl);
            siteDO.setCategoryList(CommonUtils.toLongArray(siteDO.getCategories(), ","));
            tmpLocalSiteMapping.put(siteDO.getSite(), siteDO);
        });
        localSiteMapping = tmpLocalSiteMapping;
        tracer.trace("finish reload localSiteMapping:" + localSiteMapping.keySet());
    }

    /**
     * homepage://homepageentry$com.youku.phone
     * <p>
     * homepage://homepageentry
     * <p>
     * $com.youku.phone
     *
     * @param url
     * @return
     */
    public static final AndroidSetting parseAndroidUrl(String url) {
        AndroidSetting setting = new AndroidSetting();
        url = StringUtils.trim(url);
        if (StringUtils.isBlank(url)) {
            return setting;
        }
        int flag = url.indexOf(PKG_PREFIX);
        if (flag < 0) {
            setting.url = url;
            return setting;
        }
        if (flag == 0) {
            setting.pkg = StringUtils.removeStart(url, PKG_PREFIX);
            return setting;
        }
        String[] array = StringUtils.split(url, PKG_PREFIX);
        setting.url = array[0];
        setting.pkg = array[1];
        return setting;
    }

    /**
     * homepage://homepageentry$https://itunes.apple.com/cn/app/id1437224544?mt=8
     * <p>
     * homepage://homepageentry
     * <p>
     * $com.youku.phone
     *
     * @param url
     * @return
     */
    public static final IosSetting parseIosUrl(String url) {
        IosSetting setting = new IosSetting();
        url = StringUtils.trim(url);
        if (StringUtils.isBlank(url)) {
            return setting;
        }
        int flag = url.indexOf(PKG_PREFIX);
        if (flag < 0) {
            setting.url = url;
            return setting;
        }
        if (flag == 0) {
            setting.itunesUrl = StringUtils.removeStart(url, PKG_PREFIX);
            return setting;
        }
        String[] array = StringUtils.split(url, PKG_PREFIX);
        setting.url = array[0];
        setting.itunesUrl = array[1];
        return setting;
    }

    public static class IosSetting {
        public String url;
        public String itunesUrl;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

    public static class AndroidSetting {
        public String url;
        public String pkg;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
