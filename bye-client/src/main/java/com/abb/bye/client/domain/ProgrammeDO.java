package com.abb.bye.client.domain;

import java.io.Serializable;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public class ProgrammeDO extends ProgrammeBaseDO implements Serializable {
    private static final long serialVersionUID = -2151284983855939932L;
    public static final String ATTRS_SOURCE_ID = "SOURCE_ID";
    public static final String ATTRS_SOURCE_SITE = "SOURCE_SITE";
    private String tags;
    private String attributes;
    private String uniqueKey;
    private String sites;
    private String keywords;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getSites() {
        return sites;
    }

    public void setSites(String sites) {
        this.sites = sites;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
