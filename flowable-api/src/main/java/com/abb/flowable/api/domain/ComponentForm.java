package com.abb.flowable.api.domain;

import com.abb.flowable.api.domain.component.Component;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public class ComponentForm implements Serializable {
    private static final long serialVersionUID = -19879769267751819L;
    private List<Component> components;
    private String redirectUrl;
    private Map<String, Object> extra;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public ComponentForm setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public ComponentForm setExtra(Map<String, Object> extra) {
        this.extra = extra;
        return this;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public ComponentForm addComponent(Component component) {
        if (components == null) {
            components = new ArrayList<>();
        }
        components.add(component);
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
