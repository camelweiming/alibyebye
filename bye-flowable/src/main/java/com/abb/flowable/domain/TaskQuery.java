package com.abb.flowable.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

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

    private boolean withVariables = true;
    private String userId;
    private TYPE type;
    private String processDefinitionKey;
    private int start;
    private int limit;
    private boolean needTotal;

    public boolean isNeedTotal() {
        return needTotal;
    }

    public void setNeedTotal(boolean needTotal) {
        this.needTotal = needTotal;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
