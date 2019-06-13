package com.abb.bye.web;

import com.abb.bye.Constants;
import com.abb.bye.utils.LoginUtil;
import com.abb.flowable.domain.*;
import com.abb.flowable.domain.component.Component;
import com.abb.flowable.domain.component.HiddenComponent;
import com.abb.flowable.service.FlowService;
import com.abb.flowable.service.Form;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Iterators;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/5/27
 */
@Controller
public class TaskController {
    private static Logger logger = LoggerFactory.getLogger(TaskController.class);
    @Resource
    private FlowService flowService;

    @RequestMapping(value = "task_list.htm", method = RequestMethod.GET)
    String taskList(HttpServletRequest request, Model model, @RequestParam(required = false) Integer type) {
        String vm = "task_list";
        Long loginUserId = LoginUtil.getLoginUserSilent(request);
        if (type == null) {
            type = 0;
        }
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
        TaskQuery q = new TaskQuery().setType(queryType).setUserId(String.valueOf(loginUserId)).setLimit(Integer.MAX_VALUE);
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
            requestDTO.addContext(Constants.REQUEST_CXT_LOGIN_USER_ID, LoginUtil.getLoginUser(request));
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

        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        try {
            FormRequest requestDTO = new DefaultFormRequest(request.getParameterMap());
            requestDTO.addContext(Constants.REQUEST_CXT_LOGIN_USER_ID, LoginUtil.getLoginUser(request));
            String formKey;
            if (taskId != null) {
                TaskDTO task = flowService.getTask(taskId, new Options()).getData();
                formKey = task.getFormKey();
            } else {
                formKey = flowService.getStartFormKey(processKey).getData();
            }
            Form form = flowService.getFrom(formKey);
            com.abb.flowable.domain.ResultDTO<Object> res = form.post(requestDTO);
            if (!res.isSuccess()) {
                data.put("success", false);
                data.put("errorMsg", res.getErrMsg());
            } else {
                data.put("data", res.getData());
            }
        } catch (Throwable e) {
            data.put("success", false);
            data.put("errorMsg", "系统异常");
            logger.error("Error form", e);
        }
        StringBuilder content = new StringBuilder();
        if (callback != null) {
            response.setHeader("Content-Type", "text/html");
            content.append("<script>").append(callback).append("(");
        } else {
            response.setHeader("Content-Type", "application/json");
        }
        content.append("'").append(JSON.toJSONString(data)).append("'");
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
            boolean finished = true;
            for (ProcessNodeDTO node : processNodes) {
                String formKey = node.getFormKey();
                NodeVO nodeVO = new NodeVO(node);
                boolean toEdit = (NumberUtils.toLong(node.getAssignee()) == loginUserId) && node.getState() == ProcessNodeDTO.STATE_PROCESSING;
                if (toEdit) {
                    nodeVO.edit = true;
                }
                if (node.getState() != ProcessNodeDTO.STATE_END) {
                    finished = false;
                }
                nodeVO.fields = new ArrayList<>(0);
                nodeVOS.add(nodeVO);
                if (formKey == null) {
                    continue;
                }
                Form form = flowService.getFrom(formKey);
                if (form == null) {
                    continue;
                }
                ComponentForm componentForm = toEdit ? mergeField4Edit(form, request, node) : mergeField4Show(form, node);
                nodeVO.fields = (componentForm == null || componentForm.getComponents() == null) ? new ArrayList<>(0) : componentForm.getComponents();
            }
            filter4show(nodeVOS);
            model.addAttribute("nodes", nodeVOS);
            long cost = finished
                ? (processNodes.get(processNodes.size() - 1).getEndTime().getTime() - processNodes.get(0).getStartTime().getTime())
                : (System.currentTimeMillis() - processNodes.get(0).getStartTime().getTime());
            model.addAttribute("finished", finished);
            model.addAttribute("cost", (cost / 1000 / 60));
        } catch (Throwable e) {
            logger.error("Error addHoliday", e);
            model.addAttribute("errorMsg", "system error");
        }
        return vm;
    }

    private ComponentForm mergeField4Edit(Form form, HttpServletRequest request, ProcessNodeDTO node) {
        FormRequest requestDTO = new DefaultFormRequest(request.getParameterMap());
        requestDTO.addContext(FormRequestDTO.CXT_LOGIN_USER_ID, LoginUtil.getLoginUserSilent(request));
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

    /**
     * 显示过滤
     *
     * @param nodes
     */
    private void filter4show(List<NodeVO> nodes) {
        Iterators.removeIf(nodes.iterator(), input -> !input.getNode().getActivityType().equals("userTask") && !input.getNode().getActivityType().equals("startEvent"));
    }

    public static class NodeVO {
        private ProcessNodeDTO node;
        private List<Component> fields;
        private boolean edit;
        private Long durationMin;

        public boolean isEdit() {
            return edit;
        }

        public NodeVO(ProcessNodeDTO node) {
            this.node = node;
            this.durationMin = node.getDurationInMillis() == null ? null : node.getDurationInMillis() / 1000 / 60;
        }

        public ProcessNodeDTO getNode() {
            return node;
        }

        public List<Component> getFields() {
            return fields;
        }

        public Long getDurationMin() {
            return durationMin;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
