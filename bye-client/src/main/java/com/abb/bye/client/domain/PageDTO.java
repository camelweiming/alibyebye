package com.abb.bye.client.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/3/8
 */
public class PageDTO {
    private String url;
    private String html;
    private int statusCode;
    private boolean downloadSuccess;
    private boolean skip;
    private String charset;
    private List<String> targetRequests;
    private Map<String, Object> fields;

    public Map<String, Object> getFields() {
        return fields;
    }

    public void addField(String key, Object value) {
        if (fields == null) {
            fields = new HashMap<>();
        }
        fields.put(key, value);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isDownloadSuccess() {
        return downloadSuccess;
    }

    public void setDownloadSuccess(boolean downloadSuccess) {
        this.downloadSuccess = downloadSuccess;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public List<String> getTargetRequests() {
        return targetRequests;
    }

    public void setTargetRequests(List<String> targetRequests) {
        this.targetRequests = targetRequests;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
