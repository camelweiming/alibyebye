package com.abb.bye.web;

import com.abb.bye.client.domain.*;
import com.abb.bye.client.flow.Form;
import com.abb.bye.client.flow.FormField;
import com.abb.bye.client.service.FlowService;
import com.abb.bye.utils.FormUtils;
import com.abb.bye.web.form.FormLoader;
import com.alibaba.fastjson.JSON;
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
public class TaskController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(TaskController.class);
    @Resource
    private FlowService flowService;

    @RequestMapping(value = "task_list.htm", method = RequestMethod.GET)
    String taskList(HttpServletRequest request, Model model, @RequestParam(required = false) Integer type) {
        String vm = "task_list";
        Long loginUserId = getLoginUser(request);
        if (type == null) {
            type = 0;
        }
        FlowTaskQuery.TYPE queryType = null;
        switch (type) {
            case 0:
                queryType = FlowTaskQuery.TYPE.WAITING_PROCESS;
                break;
            case 1:
                queryType = FlowTaskQuery.TYPE.INITIATE;
                break;
            case 2:
                queryType = FlowTaskQuery.TYPE.PROCESSED;
                break;
        }
        FlowTaskQuery q = new FlowTaskQuery().setType(queryType).setUserId(String.valueOf(loginUserId)).setLimit(Integer.MAX_VALUE);
        ResultDTO<List<FlowTaskDTO>> list = flowService.query(q);
        model.addAttribute("tasks", list.getData());
        return vm;
    }

    @RequestMapping(value = "task_form.htm", method = RequestMethod.GET)
    String taskForm(HttpServletRequest request, Model model, @RequestParam(required = false) String processKey) {
        String formKey = flowService.getStartFormKey(processKey).getData();
        try {
            Form form = FormLoader.load(formKey);
            List<FormField> formFields = FormUtils.getFields(form);
            formFields.add(new FormField().setType("hidden").setName("processKey").setValue(processKey));
            model.addAttribute("fields", formFields);
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
                      @RequestParam(required = false) String processInstanceId) {
        String formKey = flowService.getStartFormKey(processKey).getData();
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        try {
            Form form = FormLoader.load(formKey);
            ResultDTO<Object> res = form.post(request);
            if (!res.isSuccess()) {
                data.put("success", false);
                data.put("errorMsg", res.getErrMsg());
            } else {
                data.put("data", res.getData());
            }
        } catch (Throwable e) {
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
            Long loginUserId = getLoginUser(request);
            List<ProcessNodeDTO> processNodes = flowService.getByInstanceId(processInstanceId, new FlowOptions().setWithVariables(true).setWithFormKey(true)).getData();
            List<NodeVO> nodeVOS = new ArrayList<>(processNodes.size());
            for (ProcessNodeDTO node : processNodes) {
                String formKey = node.getFormKey();
                NodeVO nodeVO = new NodeVO(node);
                nodeVOS.add(nodeVO);
                Form form = FormLoader.load(formKey);
                if (form == null) {
                    nodeVO.fields = new ArrayList<>();
                    continue;
                }
                form.render(node.getVariables());
                nodeVO.fields = FormUtils.getFieldsOnlyPersistent(form);
                if (NumberUtils.toLong(node.getAssignee()) == loginUserId) {
                    nodeVO.edit = true;
                }
            }
            model.addAttribute("nodes", nodeVOS);
        } catch (Throwable e) {
            logger.error("Error addHoliday", e);
            model.addAttribute("errorMsg", "system error");
        }
        return vm;
    }

    public static class NodeVO {
        private ProcessNodeDTO node;
        private List<FormField> fields;
        private boolean edit;

        public boolean isEdit() {
            return edit;
        }

        public NodeVO(ProcessNodeDTO node) {
            this.node = node;
        }

        public ProcessNodeDTO getNode() {
            return node;
        }

        public List<FormField> getFields() {
            return fields;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
