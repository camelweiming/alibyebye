package com.abb.bye.client.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cenpeng.lwm
 * @since 2019/3/19
 */
public class TaskQueueDO implements Serializable {
    private static final long serialVersionUID = 684785351337804461L;
    public static final Byte STATUS_WAITING = 0;
    public static final Byte STATUS_RUNNING = 1;
    public static final Byte STATUS_FAILED = 2;
    public static final Byte STATUS_SUCCESS = 3;
    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    private Byte status;
    private Integer type;
    private Integer version;
    private String uniqueKey;
    private Date startTime;
    /**
     * 整个任务的超时时间
     */
    private Date timeout;
    /**
     * 单次执行的timeout，如执行任务超过10秒认为调用超时，下次重新执行 不需要设置,设置executeTimeoutSeconds即可
     */
    private Date executeTimeout;
    private String msg;
    private String ip;
    private Integer remainRetryCount;
    private Integer origRetryCount;
    private Integer executeIntervalSeconds;
    private Integer alarmThreshold;
    private String env;
    private Long parentId;
    private Integer childrenCount;
    private String attributes;

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getTimeout() {
        return timeout;
    }

    public void setTimeout(Date timeout) {
        this.timeout = timeout;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getRemainRetryCount() {
        return remainRetryCount;
    }

    public void setRemainRetryCount(Integer remainRetryCount) {
        this.remainRetryCount = remainRetryCount;
    }

    public Integer getOrigRetryCount() {
        return origRetryCount;
    }

    public void setOrigRetryCount(Integer origRetryCount) {
        this.origRetryCount = origRetryCount;
    }

    public Integer getExecuteIntervalSeconds() {
        return executeIntervalSeconds;
    }

    public void setExecuteIntervalSeconds(Integer executeIntervalSeconds) {
        this.executeIntervalSeconds = executeIntervalSeconds;
    }

    public Integer getAlarmThreshold() {
        return alarmThreshold;
    }

    public void setAlarmThreshold(Integer alarmThreshold) {
        this.alarmThreshold = alarmThreshold;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(Integer childrenCount) {
        this.childrenCount = childrenCount;
    }

    public Date getExecuteTimeout() {
        return executeTimeout;
    }

    public void setExecuteTimeout(Date executeTimeout) {
        this.executeTimeout = executeTimeout;
    }

    public int getRetryCount() {
        return origRetryCount - remainRetryCount;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
