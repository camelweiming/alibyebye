package com.abb.flowable.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author cenpeng.lwm
 * @since 2019/6/7
 */
public class Options implements Serializable {
    private static final long serialVersionUID = 8820090730043661858L;
    private boolean withVariables;
    private boolean replaceLocalVariables = true;
    private boolean withFormKey;

    public boolean isWithFormKey() {
        return withFormKey;
    }

    public Options setWithFormKey(boolean withFormKey) {
        this.withFormKey = withFormKey;
        return this;
    }

    public boolean isReplaceLocalVariables() {
        return replaceLocalVariables;
    }

    public Options setReplaceLocalVariables(boolean replaceLocalVariables) {
        this.replaceLocalVariables = replaceLocalVariables;
        return this;
    }

    public boolean isWithVariables() {
        return withVariables;
    }

    public Options setWithVariables(boolean withVariables) {
        this.withVariables = withVariables;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
