package com.abb.bye.web;

import com.abb.bye.Constants;
import com.abb.bye.client.domain.*;
import com.abb.bye.client.exception.AuthException;
import com.abb.bye.client.service.FlowService;
import com.abb.bye.client.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
    @Resource
    private FlowService flowService;
    private static final String PROCESS_DEFINITION_KEY = "holidayRequest";

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
            UserDTO userDTO = userService.getById(getLoginUser(request), new UserOptions().setWithBoss(true)).getData();
            FlowSubmitDTO flowSubmitDTO = new FlowSubmitDTO();
            flowSubmitDTO.setUserId(userDTO.getUserId());
            flowSubmitDTO.setUserName(userDTO.getUserName());
            flowSubmitDTO.setTitle(String.format("%s申请休假%s天", userDTO.getUserName(), days));
            flowSubmitDTO.setDescription(description);
            flowSubmitDTO.addVariable("days", days);
            /**
             * 没有上级则跳过其余审批节点
             */
            UserDTO leader = null;
            if (CollectionUtils.isNotEmpty(userDTO.getBosses())) {
                leader = userDTO.getBosses().get(0);
            }
            if (leader == null) {
                flowSubmitDTO.setSkip(true);
                flowSubmitDTO.setPass(true);
            } else {
                flowSubmitDTO.setAssignee(String.valueOf(leader.getUserId()));
                flowSubmitDTO.setAssigneeName(leader.getUserName());
            }
            ResultDTO<ProcessInstanceDTO> result = flowService.submitProcessor(PROCESS_DEFINITION_KEY, flowSubmitDTO);
            if (!result.isSuccess()) {
                model.addAttribute("errorMsg", result.getErrMsg());
            }
        } catch (Throwable e) {
            logger.error("Error addHoliday", e);
            model.addAttribute("errorMsg", "system error");
        }
        return vm;
    }

    @RequestMapping(value = "approve_holiday.htm", method = {RequestMethod.POST, RequestMethod.GET})
    String approveHoliday(Model model, HttpServletRequest request, @RequestParam String taskId, @RequestParam(required = false) Integer approve, @RequestParam(required = false) Long confirmUser) {
        String vm = "holiday/approve_holiday";
        try {
            FlowTaskDTO flowTaskDTO = flowService.getTask(taskId, new FlowOptions().setWithVariables(true)).getData();
            if (flowTaskDTO == null) {
                throw new IllegalArgumentException("task not found!");
            }
            Long loginUserId = getLoginUser(request);
            if (!loginUserId.equals(NumberUtils.toLong(flowTaskDTO.getAssignee(), 0))) {
                throw new AuthException("permission deny");
            }
            if (confirmUser != null && confirmUser < 0) {
                confirmUser = null;
            }
            Map<String, Object> variables = flowTaskDTO.getVariables();
            UserDTO userDTO = userService.getById(loginUserId, new UserOptions().setWithBoss(true)).getData();
            model.addAttribute("leaders", userDTO.getBosses());
            model.addAttribute("confirmUser", confirmUser);
            model.addAttribute("taskId", taskId);
            model.addAttribute("days", variables.get("days"));
            model.addAttribute("description", flowTaskDTO.getDescription());
            model.addAttribute("userId", flowTaskDTO.getUserId());
            model.addAttribute("userName", flowTaskDTO.getUserName());
            model.addAttribute(Constants.TASK_PASS, approve);
            if (approve != null) {
                FlowSubmitDTO submitDTO = new FlowSubmitDTO();
                boolean pass = approve == 1;
                /**
                 * 如果驳回或没有加签，则跳过其余审批节点
                 */
                if (!pass || confirmUser == null) {
                    submitDTO.setSkip(true);
                } else {
                    submitDTO.setAssignee(String.valueOf(confirmUser));
                    submitDTO.setAssigneeName(userService.getById(confirmUser, new UserOptions()).getData().getUserName());
                }
                submitDTO.setPass(true);
                ResultDTO<Void> result = flowService.complete(taskId, submitDTO);
                if (!result.isSuccess()) {
                    model.addAttribute("errorMsg", result.getErrMsg());
                    return vm;
                }
                return "redirect:/task_list.htm";
            }
        } catch (Throwable e) {
            logger.error("Error addHoliday", e);
            model.addAttribute("errorMsg", "system error");
        }
        return vm;
    }

    @RequestMapping(value = "show_holiday.htm", method = {RequestMethod.POST, RequestMethod.GET})
    String showHoliday(Model model, HttpServletRequest request, @RequestParam String processInstanceId) {
        String vm = "holiday/show_holiday";
        try {
            List<ProcessNodeDTO> processNodes = flowService.getByInstanceId(processInstanceId, new FlowOptions().setWithVariables(true)).getData();
            ProcessNodeDTO processNodeDTO = processNodes.get(0);
            model.addAttribute("taskId", processInstanceId);
            model.addAttribute("days", processNodeDTO.getVariables().get("days"));
            model.addAttribute("description", processNodeDTO.getDescription());
            model.addAttribute("userId", processNodeDTO.getUserId());
            model.addAttribute("userName", processNodeDTO.getUserName());
            model.addAttribute("nodes", processNodes);
        } catch (Throwable e) {
            logger.error("Error addHoliday", e);
            model.addAttribute("errorMsg", "system error");
        }
        return vm;
    }
}
