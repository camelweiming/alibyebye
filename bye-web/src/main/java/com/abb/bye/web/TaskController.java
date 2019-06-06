package com.abb.bye.web;

import com.abb.bye.Constants;
import com.abb.bye.utils.CommonUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
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

    @RequestMapping(value = "task_list.htm", method = RequestMethod.GET)
    String taskList(HttpServletRequest request, Model model) {
        String vm = "task_list";
        Long loginUserId = getLoginUser(request);
        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(String.valueOf(loginUserId)).list();
        List<TaskVO> list = new ArrayList<>();
        tasks.forEach(t -> {
            Map<String, Object> variables = taskService.getVariables(t.getId());
            TaskVO vo = new TaskVO();
            vo.setId(t.getId());
            vo.setTitle((String)variables.get(Constants.TASK_TITLE));
            vo.setLink(buildLink(t));
            list.add(vo);
        });
        model.addAttribute("tasks", list);
        return vm;
    }

    @RequestMapping(value = "his_task_list.htm", method = RequestMethod.GET)
    String hisTaskList(HttpServletRequest request, Model model) {
        String vm = "his_task_list";
        Long loginUserId = getLoginUser(request);
        HistoryService taskService = ProcessEngines.getDefaultProcessEngine().getHistoryService();
        List<HistoricTaskInstance> tasks = taskService.createHistoricTaskInstanceQuery().taskAssignee(String.valueOf(loginUserId)).list();
        List<TaskVO> list = new ArrayList<>();
        tasks.forEach(t -> {
            List<HistoricVariableInstance> histories = taskService.createHistoricVariableInstanceQuery().processInstanceId(t.getProcessInstanceId()).list();
            Map<String, Object> variables = new HashMap<>();
            histories.forEach(his -> variables.put(his.getVariableName(), his.getValue()));
            TaskVO vo = new TaskVO();
            vo.setId(t.getId());
            vo.setTitle((String)variables.get(Constants.TASK_TITLE));
            vo.setLink(buildLink(t));
            list.add(vo);
        });
        model.addAttribute("tasks", list);
        return vm;
    }

    //@RequestMapping(value = "task_form.htm", method = RequestMethod.GET)
    //String form(HttpServletRequest request, Model model, @RequestParam(required = false) String processKey) {
    //    String processDefinitionId = ProcessEngines.getDefaultProcessEngine().getRepositoryService()
    //        .createProcessDefinitionQuery().processDefinitionKey(processKey).latestVersion().singleResult().getId();
    //    StartFormData startFormData = ProcessEngines.getDefaultProcessEngine().getFormService().getStartFormData(processDefinitionId);
    //    List<FormProperty> properties = startFormData.getFormProperties();
    //    return "task_form";
    //}

    private static String buildLink(Task task) {
        Map<String, String> replaceValue = Maps.newHashMap();
        replaceValue.put("taskId", task.getId());
        return CommonUtils.formatText(task.getFormKey(), replaceValue);
    }

    private static String buildLink(HistoricTaskInstance task) {
        Map<String, String> replaceValue = Maps.newHashMap();
        replaceValue.put("taskId", task.getId());
        return CommonUtils.formatText(task.getFormKey(), replaceValue);
    }

    public static class TaskVO implements Serializable {
        private static final long serialVersionUID = -4089711370708108603L;
        private String id;
        private String title;
        private String link;
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
