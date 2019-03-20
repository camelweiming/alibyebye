package com.abb.bye.client.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * @author cenpeng.lwm
 * @since 2019/3/20
 */
public class TaskResult {
    private boolean success;
    private String errorMsg;
    private boolean giveUp = false;
    private Date nextExecuteTime;

    public boolean isSuccess() {
        return success;
    }

    public TaskResult(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public TaskResult setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public boolean isGiveUp() {
        return giveUp;
    }

    public TaskResult setGiveUp(boolean giveUp) {
        this.giveUp = giveUp;
        return this;
    }

    public Date getNextExecuteTime() {
        return nextExecuteTime;
    }

    public TaskResult setNextExecuteTime(Date nextExecuteTime) {
        this.nextExecuteTime = nextExecuteTime;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
