package com.abb.flowable.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/8
 */
public class CompleteDTO extends RequestDTO {
    private static final long serialVersionUID = 4196449326985418273L;
    /**
     * 当前task变量
     */
    private Map<String, Object> taskVariables;

    public Map<String, Object> getTaskVariables() {
        return taskVariables;
    }

    public void setTaskVariables(Map<String, Object> taskVariables) {
        this.taskVariables = taskVariables;
    }

    public void addTaskVariables(String name, Object value) {
        if (this.taskVariables == null) {
            this.taskVariables = new HashMap<>(8);
        }
        this.taskVariables.put(name, value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
