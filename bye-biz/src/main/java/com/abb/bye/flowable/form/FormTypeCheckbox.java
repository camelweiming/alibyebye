package com.abb.bye.flowable.form;

import org.flowable.engine.form.AbstractFormType;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/6/7
 */
public class FormTypeCheckbox extends AbstractFormType {
    protected final List<String> values;

    public FormTypeCheckbox(List<String> values) {
        this.values = values;
    }

    @Override
    public Object convertFormValueToModelValue(String key) {
        if (key.equals("values")) {
            return values;
        }
        return null;
    }

    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        return null;
    }

    @Override
    public String getName() {
        return "checkbox";
    }
}
