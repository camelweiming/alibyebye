package com.abb.flowable.domain;

import com.abb.flowable.domain.component.Component;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public class ComponentForm implements Serializable {
    private static final long serialVersionUID = -19879769267751819L;
    private List<Component> components;

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
