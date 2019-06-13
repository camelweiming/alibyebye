package com.abb.bye.web.test;

import com.abb.bye.flowable.holiday.HolidayApproveForm;
import com.abb.bye.flowable.holiday.HolidayRequestForm;
import org.junit.Test;

import java.util.HashMap;
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
    }

    @Test
    public void test2() throws IllegalAccessException {
        HolidayApproveForm form = new HolidayApproveForm();

    }
}
