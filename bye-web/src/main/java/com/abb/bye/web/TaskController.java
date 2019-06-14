package com.abb.bye.web;

import com.abb.bye.Constants;
import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.domain.UserOptions;
import com.abb.bye.client.service.UserService;
import com.abb.bye.client.vo.NodeVO;
import com.abb.bye.utils.LoginUtil;
import com.abb.flowable.domain.*;
import com.abb.flowable.domain.component.HiddenComponent;
import com.abb.flowable.service.FlowService;
import com.abb.flowable.service.Form;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author cenpeng.lwm
 * @since 2019/5/27
 */
@Controller
public class TaskController {
    private static Logger logger = LoggerFactory.getLogger(TaskController.class);
    @Resource
    private FlowService flowService;
    @Resource
    private UserService userService;
    private Set<String> SHOW_TASK_TYPES = Sets.newHashSet("userTask", "startEvent");

    @RequestMapping(value = "task_list.htm", method = {RequestMethod.GET, RequestMethod.POST})
    String taskList(HttpServletRequest request,
                    Model model,
                    @RequestParam(required = false) Integer type,
                    @RequestParam(required = false) String initiator,
                    @RequestParam(required = false) String processDefinitionKey,
                    @RequestParam(required = false) String title

    ) {
        String vm = "task_list";
        Long loginUserId = LoginUtil.getLoginUserSilent(request);
        if (type == null) {
            type = 0;
        }
        model.addAttribute("initiator", initiator);
        model.addAttribute("processDefinitionKey", processDefinitionKey);
        model.addAttribute("title", title);
        TaskQuery.TYPE queryType = null;
        switch (type) {
            case 0:
                queryType = TaskQuery.TYPE.WAITING_PROCESS;
                break;
            case 1:
                queryType = TaskQuery.TYPE.INITIATE;
                break;
            case 2:
                queryType = TaskQuery.TYPE.PROCESSED;
                break;
        }
        model.addAttribute("type", type);
        TaskQuery q = new TaskQuery().setType(queryType).setUserId(String.valueOf(loginUserId)).setLimit(Integer.MAX_VALUE);
        if (StringUtils.isNotBlank(processDefinitionKey)) {
            q.setProcessDefinitionKey(processDefinitionKey);
        }
        if (StringUtils.isNotBlank(title)) {
            q.setTitle("%" + title + "%");
        }
        if (initiator != null) {
            UserDTO userDTO = userService.getByName(initiator, new UserOptions()).getData();
            if (userDTO != null) {
                q.setInitiatorId(userDTO.getUserId());
            }
        }
        com.abb.flowable.domain.ResultDTO<List<TaskDTO>> list = flowService.query(q);
        model.addAttribute("tasks", list.getData());
        return vm;
    }

    @RequestMapping(value = "task_form.htm", method = RequestMethod.GET)
    String taskForm(HttpServletRequest request, Model model, @RequestParam(required = false) String processKey) {
        String formKey = flowService.getStartFormKey(processKey).getData();
        try {
            Form form = flowService.getFrom(formKey);
            FormRequest requestDTO = new DefaultFormRequest(request.getParameterMap());
            requestDTO.addContext(Constants.REQUEST_CXT_LOGIN_USER_ID, LoginUtil.getLoginUserSilent(request));
            ComponentForm componentForm = form.render(requestDTO).getData();
            componentForm.addComponent(new HiddenComponent().setValue(processKey).setName("processKey"));
            model.addAttribute("fields", componentForm.getComponents());
        } catch (Throwable e) {
            model.addAttribute("errorMsg", "系统异常");
            logger.error("Error form", e);
        }
        return "task_form";
    }

    @ResponseBody
    @RequestMapping(value = "task_submit.htm", method = {RequestMethod.POST})
    String taskSubmit(Model model,
                      HttpServletResponse response,
                      HttpServletRequest request,
                      @RequestParam(required = false) String callback,
                      @RequestParam(required = false) String processKey,
                      @RequestParam(required = false) String taskId
    ) {
        ResultDTO<Object> res;
        try {
            FormRequest requestDTO = new DefaultFormRequest(request.getParameterMap());
            requestDTO.addContext(Constants.REQUEST_CXT_LOGIN_USER_ID, LoginUtil.getLoginUserSilent(request));
            String formKey;
            if (taskId != null) {
                TaskDTO task = flowService.getTask(taskId, new Options()).getData();
                formKey = task.getFormKey();
            } else {
                formKey = flowService.getStartFormKey(processKey).getData();
            }
            Form form = flowService.getFrom(formKey);
            res = form.post(requestDTO);
        } catch (Throwable e) {
            res = ResultDTO.buildError("System error");
            logger.error("Error form", e);
        }
        StringBuilder content = new StringBuilder();
        if (callback != null) {
            response.setHeader("Content-Type", "text/html");
            content.append("<script>").append(callback).append("(");
        } else {
            response.setHeader("Content-Type", "application/json");
        }
        content.append("'").append(JSON.toJSONString(res)).append("'");
        if (callback != null) {
            content.append(");</script>");
        }
        return content.toString();
    }

    @RequestMapping(value = "task_show.htm", method = RequestMethod.GET)
    String taskShow(Model model, HttpServletRequest request, @RequestParam(required = false) String processInstanceId) {
        String vm = "task_show";
        try {
            Long loginUserId = LoginUtil.getLoginUserSilent(request);
            List<ProcessNodeDTO> processNodes = flowService.getByInstanceId(processInstanceId, new Options().setWithVariables(true).setWithFormKey(true)).getData();
            List<NodeVO> nodeVOS = new ArrayList<>(processNodes.size());
            ProcessNodeDTO firstNode = processNodes.get(0);
            ProcessNodeDTO lastNode = processNodes.get(processNodes.size() - 1);
            boolean finished = lastNode.getState() == TaskState.END;
            for (ProcessNodeDTO node : processNodes) {
                if (!SHOW_TASK_TYPES.contains(node.getActivityType()) || node.isSkipped()) {
                    continue;
                }
                NodeVO nodeVO = new NodeVO();
                nodeVO.setNode(node);
                boolean toEdit = (NumberUtils.toLong(node.getAssignee()) == loginUserId) && node.getState() == TaskState.PROCESSING;
                if (toEdit) {
                    nodeVO.setEdit(true);
                }
                nodeVO.setFields(new ArrayList<>(0));
                nodeVO.setDurationMin(node.getDurationInMillis() == null ? null : node.getDurationInMillis() / 1000 / 60);
                nodeVOS.add(nodeVO);

                String formKey = node.getFormKey();
                if (formKey == null) {
                    continue;
                }
                Form form = flowService.getFrom(formKey);
                if (form == null) {
                    continue;
                }
                ComponentForm componentForm = toEdit ? mergeField4Edit(form, request, node) : mergeField4Show(form, node);
                nodeVO.setFields((componentForm == null || componentForm.getComponents() == null) ? new ArrayList<>(0) : componentForm.getComponents());
            }
            long cost = finished ? (lastNode.getEndTime().getTime() - firstNode.getStartTime().getTime()) : (System.currentTimeMillis() - firstNode.getStartTime().getTime());
            model.addAttribute("finished", finished);
            model.addAttribute("cost", (cost / 1000 / 60));
            model.addAttribute("nodes", nodeVOS);
        } catch (Throwable e) {
            logger.error("Error addHoliday", e);
            model.addAttribute("errorMsg", "system error");
        }
        return vm;
    }

    private ComponentForm mergeField4Edit(Form form, HttpServletRequest request, ProcessNodeDTO node) {
        FormRequest requestDTO = new DefaultFormRequest(request.getParameterMap());
        requestDTO.addContext(Constants.REQUEST_CXT_LOGIN_USER_ID, LoginUtil.getLoginUserSilent(request));
        com.abb.flowable.domain.ResultDTO<ComponentForm> res = form.render(requestDTO);
        if (!res.isSuccess()) {
            throw new RuntimeException(res.getErrMsg());
        }
        ComponentForm componentForm = res.getData();
        componentForm.addComponent(new HiddenComponent().setValue(node.getTaskId()).setName("taskId"));
        return componentForm;
    }

    private ComponentForm mergeField4Show(Form form, ProcessNodeDTO node) throws IllegalAccessException {
        com.abb.flowable.domain.ResultDTO<ComponentForm> res = form.render(node.getVariables());
        if (!res.isSuccess()) {
            throw new RuntimeException(res.getErrMsg());
        }
        ComponentForm componentForm = res.getData();
        return componentForm;
    }

}
