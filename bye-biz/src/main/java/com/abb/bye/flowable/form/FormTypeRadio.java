package com.abb.bye.flowable.form;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/6/7
 */
public class FormTypeRadio extends FormTypeCheckbox {
    public FormTypeRadio(List<String> values) {
        super(values);
    }

    @Override
    public String getName() {
        return "radio";
    }
}
