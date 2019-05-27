package com.abb.bye.web;

import com.alibaba.boot.velocity.annotation.VelocityLayout;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/5/24
 */
@Controller
public class HolidayController {

    @RequestMapping(value = "add_holiday", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/default.vm")
    void holidayRequest(@RequestParam int days, @RequestParam String name, @RequestParam String description) {
        String vm = "holiday/holiday_list.vm";
        RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("days", days);
        variables.put("description", description);
        runtimeService.startProcessInstanceByKey("holidayRequest", variables);

    }
}
