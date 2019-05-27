package com.abb.bye.web;

import com.abb.bye.client.domain.UserDTO;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/5/24
 */
@Controller
public class HolidayController extends BaseController {

    @RequestMapping(value = "add_holiday.htm", method = {RequestMethod.POST, RequestMethod.GET})
    String holidayRequest(Model model, HttpServletRequest request, @RequestParam(required = false) Integer days, @RequestParam(required = false) String description) {
        String vm = "holiday/add_holiday";
        if (days == null) {
            return vm;
        }
        if (days <= 0) {
            model.addAttribute("errorMsg", "天数不能小于0");
            return vm;
        }
        RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();
        UserDTO userDTO = getLoginUser(request);
        Map<String, Object> variables = new HashMap<>(8);
        variables.put("assignee", "75001");
        variables.put("userId", userDTO.getUserId());
        variables.put("userName", userDTO.getUserName());
        variables.put("days", days);
        variables.put("description", description);
        runtimeService.startProcessInstanceByKey("holidayRequest", variables);
        return vm;
    }
}
