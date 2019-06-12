package com.abb.bye.client.flow;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author cenpeng.lwm
 * @since 2019/6/12
 */
public class FormField implements Serializable {
    private static final long serialVersionUID = 5726560593138776174L;
    private boolean required;
    private String name;
    private String label;
    private String type;
    private String value;

    public boolean isRequired() {
        return required;
    }

    public FormField setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public String getName() {
        return name;
    }

    public FormField setName(String name) {
        this.name = name;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public FormField setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getType() {
        return type;
    }

    public FormField setType(String type) {
        this.type = type;
        return this;
    }

    public String getValue() {
        return value;
    }

    public FormField setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
