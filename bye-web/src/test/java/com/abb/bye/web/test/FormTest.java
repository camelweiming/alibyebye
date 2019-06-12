package com.abb.bye.web.test;

import com.abb.bye.client.flow.FormField;
import com.abb.bye.client.flow.FormFieldOption;
import com.abb.bye.utils.FormUtils;
import com.abb.bye.web.form.HolidayApproveForm;
import com.abb.bye.web.form.HolidayRequestForm;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/12
 */
public class FormTest {
    @Test
    public void test() throws IllegalAccessException {
        HolidayRequestForm holidayRequestForm = new HolidayRequestForm();
        Map<String, Object> m = new HashMap<>();
        m.put("days", 33);
        m.put("description", "hhhh");
        FormUtils.setFieldsFromVariables(holidayRequestForm, m);
        System.out.println(holidayRequestForm.getDays());
        System.out.println(holidayRequestForm.getDescription());
    }

    @Test
    public void test2() throws IllegalAccessException {
        HolidayApproveForm form = new HolidayApproveForm();
        List<FormFieldOption> approve = new ArrayList<>();
        approve.add(new FormFieldOption("请选择", "-1"));
        approve.add(new FormFieldOption("通过", "1"));
        approve.add(new FormFieldOption("驳回", "2"));
        form.setApprove(approve);
        List<FormField> formFields = FormUtils.getFieldsOnlyPersistent(form);
        System.out.println(formFields);
    }
}
