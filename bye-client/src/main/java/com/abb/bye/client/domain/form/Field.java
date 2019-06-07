package com.abb.bye.client.domain.form;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/6/7
 */
public class Field {
    private String type;
    private String name;
    private String label;
    private boolean required;
    private boolean readonly;
    private String value;
    private List<String> options;

    public String getType() {
        return type;
    }

    public Field setType(String type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public Field setName(String name) {
        this.name = name;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public Field setLabel(String label) {
        this.label = label;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public Field setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public Field setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Field setValue(String value) {
        this.value = value;
        return this;
    }

    public List<String> getOptions() {
        return options;
    }

    public Field setOptions(List<String> options) {
        this.options = options;
        return this;
    }
}
