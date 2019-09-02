package com.abb.flowable.api.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public class DefaultFormRequest implements FormRequest, Serializable {
    private Map<String, Object> context;
    private final Map<String, String[]> parameters;

    public DefaultFormRequest(Map<String, String[]> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String getParameter(String name) {
        String[] value = parameters.get(name);
        if (value == null) {
            return null;
        }
        return value[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    @Override
    public Map<String, Object> getContext() {
        return context;
    }

    @Override
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public void addContext(String name, Object value) {
        if (context == null) {
            context = new HashMap<>(8);
        }
        context.put(name, value);
    }

    @Override
    public Object getContextValue(String contextName) {
        return context == null ? null : context.get(contextName);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
