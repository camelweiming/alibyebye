package com.abb.bye.client.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/7
 */
public abstract class FlowBaseDTO implements Serializable {
    protected String assignee;
    protected Long userId;
    protected String userName;
    protected String title;
    protected String description;
    protected Map<String, Object> variables;
    protected UserDTO assigneeInfo;

    public UserDTO getAssigneeInfo() {
        return assigneeInfo;
    }

    public void setAssigneeInfo(UserDTO assigneeInfo) {
        this.assigneeInfo = assigneeInfo;
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
