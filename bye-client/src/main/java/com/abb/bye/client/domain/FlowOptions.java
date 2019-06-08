package com.abb.bye.client.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author cenpeng.lwm
 * @since 2019/6/7
 */
public class FlowOptions implements Serializable {
    private boolean withVariables;
    private boolean replaceLocalVariables = true;

    public boolean isReplaceLocalVariables() {
        return replaceLocalVariables;
    }

    public FlowOptions setReplaceLocalVariables(boolean replaceLocalVariables) {
        this.replaceLocalVariables = replaceLocalVariables;
        return this;
    }

    public boolean isWithVariables() {
        return withVariables;
    }

    public FlowOptions setWithVariables(boolean withVariables) {
        this.withVariables = withVariables;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
