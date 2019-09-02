package com.abb.flowable.api.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/11
 */
public class TaskQuery implements Serializable {
    public enum TYPE {
        /**
         * 发起的任务
         */
        INITIATE,
        /**
         * 待处理任务
         */
        WAITING_PROCESS,
        /**
         * 处理过的任务
         */
        PROCESSED
    }

    public enum STATE {
        ALL,
        FINISHED,
        UNFINISHED
    }

    private boolean withVariables = true;
    /**
     * 用户id
     */
    private String userId;
    private TYPE type;
    private STATE state = STATE.ALL;
    private String processDefinitionKey;
    private int start;
    private int limit;
    private boolean needTotal;
    /**
     * 发起人过滤，通过内置variable：initiator_id 过滤
     */
    private Long initiatorId;
    /**
     * 标题过滤，通过内置variable：title过滤
     */
    private String title;
    private Map<String, Object> processVariableValueEquals;
    private Map<String, Object> processVariableValueNotEquals;

    public String getTitle() {
        return title;
    }

    public TaskQuery setTitle(String title) {
        this.title = title;
        return this;
    }

    public Long getInitiatorId() {
        return initiatorId;
    }

    public TaskQuery setInitiatorId(Long initiatorId) {
        this.initiatorId = initiatorId;
        return this;
    }

    public Map<String, Object> getProcessVariableValueEquals() {
        return processVariableValueEquals;
    }

    public TaskQuery setProcessVariableValueEquals(Map<String, Object> processVariableValueEquals) {
        this.processVariableValueEquals = processVariableValueEquals;
        return this;
    }

    public Map<String, Object> getProcessVariableValueNotEquals() {
        return processVariableValueNotEquals;
    }

    public TaskQuery setProcessVariableValueNotEquals(Map<String, Object> processVariableValueNotEquals) {
        this.processVariableValueNotEquals = processVariableValueNotEquals;
        return this;
    }

    public boolean isNeedTotal() {
        return needTotal;
    }

    public TaskQuery setNeedTotal(boolean needTotal) {
        this.needTotal = needTotal;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public TaskQuery setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public TYPE getType() {
        return type;
    }

    public TaskQuery setType(TYPE type) {
        this.type = type;
        return this;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public TaskQuery setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
        return this;
    }

    public boolean isWithVariables() {
        return withVariables;
    }

    public int getStart() {
        return start;
    }

    public TaskQuery setStart(int start) {
        this.start = start;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public TaskQuery setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public TaskQuery setWithVariables(boolean withVariables) {
        this.withVariables = withVariables;
        return this;
    }

    public STATE getState() {
        return state;
    }

    public TaskQuery setState(STATE state) {
        this.state = state;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
