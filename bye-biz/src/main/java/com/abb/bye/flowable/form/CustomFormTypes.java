package com.abb.bye.flowable.form;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FormProperty;
import org.flowable.bpmn.model.FormValue;
import org.flowable.engine.common.api.FlowableIllegalArgumentException;
import org.flowable.engine.form.AbstractFormType;
import org.flowable.engine.impl.form.DateFormType;
import org.flowable.engine.impl.form.EnumFormType;
import org.flowable.engine.impl.form.FormTypes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/7
 */
public class CustomFormTypes extends FormTypes {
    @Override
    public AbstractFormType parseFormPropertyType(FormProperty formProperty) {
        AbstractFormType formType = null;
        if ("date".equals(formProperty.getType()) && StringUtils.isNotEmpty(formProperty.getDatePattern())) {
            formType = new DateFormType(formProperty.getDatePattern());
        } else if ("enum".equals(formProperty.getType())) {
            Map<String, String> values = new LinkedHashMap<>();
            for (FormValue formValue : formProperty.getFormValues()) {
                values.put(formValue.getId(), formValue.getName());
            }
            formType = new EnumFormType(values);
        } else if ("checkbox".equals(formProperty.getType())) {
            List<String> list = new ArrayList<>();
            for (FormValue formValue : formProperty.getFormValues()) {
                list.add(formValue.getName());
            }
            formType = new FormTypeCheckbox(list);
        } else if ("radio".equals(formProperty.getType())) {
            List<String> list = new ArrayList<>();
            for (FormValue formValue : formProperty.getFormValues()) {
                list.add(formValue.getName());
            }
            formType = new FormTypeRadio(list);
        } else if (StringUtils.isNotEmpty(formProperty.getType())) {
            formType = formTypes.get(formProperty.getType());
            if (formType == null) {
                throw new FlowableIllegalArgumentException("unknown type '" + formProperty.getType() + "' " + formProperty.getId());
            }
        }
        return formType;
    }
}
