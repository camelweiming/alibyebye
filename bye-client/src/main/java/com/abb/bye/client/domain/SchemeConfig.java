package com.abb.bye.client.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2018/11/13
 */
public class SchemeConfig {
    private String[] keywords;
    private String[] excludeKeywords;
    private String script;
    private String env;

    public String getEnv() {
        return env;
    }

    public SchemeConfig setEnv(String env) {
        this.env = env;
        return this;
    }

    public String[] getExcludeKeywords() {
        return excludeKeywords;
    }

    public SchemeConfig setExcludeKeywords(String[] excludeKeywords) {
        this.excludeKeywords = excludeKeywords;
        return this;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public SchemeConfig setKeywords(String[] keywords) {
        this.keywords = keywords;
        return this;
    }

    public String getScript() {
        return script;
    }

    public SchemeConfig setScript(String script) {
        this.script = script;
        return this;
    }

    public boolean match(String url) {
        if (keywords == null) {
            return false;
        }
        for (String key : keywords) {
            if (url.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean exclude(String url) {
        if (excludeKeywords == null) {
            return false;
        }
        for (String key : excludeKeywords) {
            if (url.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public static SchemeConfig getConfig(String env, String url, List<SchemeConfig> schemeConfigs) {
        if (schemeConfigs == null) {
            return null;
        }
        for (SchemeConfig schemeConfig : schemeConfigs) {
            if (!StringUtils.equals(env, schemeConfig.getEnv())) {
                continue;
            }
            if (schemeConfig.match(url) && !schemeConfig.exclude(url)) {
                return schemeConfig;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
