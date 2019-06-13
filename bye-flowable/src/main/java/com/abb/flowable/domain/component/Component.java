package com.abb.flowable.domain.component;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 组件
 *
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public abstract class Component implements Serializable {
    private static final long serialVersionUID = 7378899908892239205L;
    protected final String type;
    protected boolean required;
    protected boolean readonly;
    protected String name;
    protected String label;
    protected String placeholder;

    public Component(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public Component setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public Component setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public String getName() {
        return name;
    }

    public Component setName(String name) {
        this.name = name;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public Component setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public Component setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
