package com.abb.bye.client.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/8
 */
public class FlowCompleteDTO extends FlowRequestDTO {
    private static final long serialVersionUID = 4196449326985418273L;
    private Map<String, Object> taskVariables;

    public Map<String, Object> getTaskVariables() {
        return taskVariables;
    }

    public void setTaskVariables(Map<String, Object> taskVariables) {
        this.taskVariables = taskVariables;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
