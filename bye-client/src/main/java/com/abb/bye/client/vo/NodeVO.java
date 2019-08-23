package com.abb.bye.client.vo;

import com.abb.flowable.api.domain.ProcessNodeDTO;
import com.abb.flowable.api.domain.component.Component;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public class NodeVO {
    private ProcessNodeDTO node;
    private List<Component> fields;
    private boolean edit;
    private Long durationMin;

    public boolean isEdit() {
        return edit;
    }

    public void setNode(ProcessNodeDTO node) {
        this.node = node;
    }

    public void setFields(List<Component> fields) {
        this.fields = fields;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public void setDurationMin(Long durationMin) {
        this.durationMin = durationMin;
    }

    public ProcessNodeDTO getNode() {
        return node;
    }

    public List<Component> getFields() {
        return fields;
    }

    public Long getDurationMin() {
        return durationMin;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
