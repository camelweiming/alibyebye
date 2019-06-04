package com.abb.bye.web;

import com.abb.bye.Constants;
import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.domain.UserOptions;
import com.abb.bye.client.domain.enums.TaskType;
import com.abb.bye.client.exception.AuthException;
import com.abb.bye.client.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/5/24
 */
@Controller
public class HolidayController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(HolidayController.class);
    @Resource
    private UserService userService;

    @RequestMapping(value = "add_holiday.htm", method = {RequestMethod.POST, RequestMethod.GET})
    String addHoliday(Model model, HttpServletRequest request, @RequestParam(required = false) Integer days, @RequestParam(required = false) String description) {
        String vm = "holiday/add_holiday";
        if (days == null) {
            return vm;
        }
        if (days <= 0) {
            model.addAttribute("errorMsg", "天数不能小于0");
            return vm;
        }
        try {
            RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();
            Long loginUserId = getLoginUser(request);
            UserDTO userDTO = userService.getById(loginUserId, new UserOptions().setWithBoss(true)).getData();
            Long assignee;
            if (CollectionUtils.isNotEmpty(userDTO.getBosses())) {
                assignee = userDTO.getBosses().get(0).getUserId();
            } else {
                assignee = loginUserId;
            }
            Map<String, Object> variables = new HashMap<>(8);
            variables.put(Constants.TASK_ASSIGNEE, "" + assignee);
            variables.put(Constants.TASK_USER_ID, userDTO.getUserId());
            variables.put(Constants.TASK_USER_NAME, userDTO.getUserName());
            variables.put(Constants.TASK_TYPE, TaskType.HOLIDAY.getType());
            variables.put(Constants.TASK_DESCRIPTION, description);
            variables.put(Constants.TASK_TITLE, String.format("%s申请休假%s天", userDTO.getUserName(), days));
            variables.put("days", days);
            runtimeService.startProcessInstanceByKey("holidayRequest", variables);
        } catch (Throwable e) {
            logger.error("Error addHoliday", e);
            model.addAttribute("errorMsg", "system error");
        }
        return vm;
    }

    @RequestMapping(value = "approve_holiday.htm", method = {RequestMethod.POST, RequestMethod.GET})
    String approveHoliday(Model model, HttpServletRequest request, @RequestParam String taskId, @RequestParam(required = false) Integer approve) {
        String vm = "holiday/approve_holiday";
        try {
            TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                throw new IllegalArgumentException("task not found!");
            }
            Long loginUserId = getLoginUser(request);
            if (!loginUserId.equals(NumberUtils.toLong(task.getAssignee(), 0))) {
                throw new AuthException("permission deny");
            }
            model.addAttribute("taskId", taskId);
            Map<String, Object> variables = taskService.getVariables(taskId);
            model.addAttribute("days", variables.get("days"));
            model.addAttribute("description", variables.get("description"));
            model.addAttribute("userName", variables.get(Constants.TASK_USER_NAME));
            model.addAttribute("userId", variables.get(Constants.TASK_USER_ID));
            model.addAttribute("approve", approve);
            if (approve != null) {
                variables = new HashMap<>(8);
                variables.put(Constants.TASK_APPROVE, approve == 1);
                taskService.complete(taskId, variables);
                return "redirect:/task_list.htm";
            }
        } catch (Throwable e) {
            logger.error("Error addHoliday", e);
            model.addAttribute("errorMsg", "system error");
        }
        return vm;
    }
}
