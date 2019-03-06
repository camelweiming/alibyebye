package com.abb.bye.client.domain;

import java.io.Serializable;

/**
 * @author cenpeng.lwm
 * @since 2019/1/18
 */
public class SiteConfigsDO implements Serializable {
    private static final long serialVersionUID = 6397708700103837635L;
    public static final int STATUS_ENABLE = 1;
    public static final int STATUS_UNABLE = 0;
    private Long id;
    private String configKey;
    private Integer site;
    private String content;
    private String env;
    private Integer status;
    private String domains;
    private String name;
    private String attributes;
    private String platform;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public Integer getSite() {
        return site;
    }

    public void setSite(Integer site) {
        this.site = site;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDomains() {
        return domains;
    }

    public void setDomains(String domains) {
        this.domains = domains;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("site=").append(site).append(",configKey=").append(configKey).append(",env=").append(env).append(",platform=").append(platform).toString();
    }
}
