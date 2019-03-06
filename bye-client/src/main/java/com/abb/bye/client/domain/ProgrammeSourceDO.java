package com.abb.bye.client.domain;

import java.io.Serializable;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public class ProgrammeSourceDO extends ProgrammeBaseDO implements Serializable {
    private static final long serialVersionUID = -4385904300519377327L;
    private Long programmeId;
    private Integer site;
    private String sourceId;
    private String showStatus;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getProgrammeId() {
        return programmeId;
    }

    public void setProgrammeId(Long programmeId) {
        this.programmeId = programmeId;
    }

    public Integer getSite() {
        return site;
    }

    public void setSite(Integer site) {
        this.site = site;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }
}
