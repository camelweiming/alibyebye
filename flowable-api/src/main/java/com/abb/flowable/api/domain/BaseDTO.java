package com.abb.flowable.api.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/7
 */
public abstract class BaseDTO implements Serializable {
    protected String assignee;
    protected String assigneeName;
    protected Long userId;
    protected String userName;
    protected String title;
    protected String description;
    /**
     * 状态
     */
    protected TaskState state;
    protected Date startTime;
    protected Date endTime;
    /**
     * 持续时间
     */
    private Long durationInMillis;

    public Long getDurationInMillis() {
        return durationInMillis;
    }

    public void setDurationInMillis(Long durationInMillis) {
        this.durationInMillis = durationInMillis;
    }

    public TaskState getState() {
        return state;
    }

    public byte getStateValue() {
        return state == null ? -1 : state.getType();
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    protected Map<String, Object> variables;

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
