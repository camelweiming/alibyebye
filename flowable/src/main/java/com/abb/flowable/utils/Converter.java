package com.abb.flowable.utils;

import com.abb.flowable.api.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */
public class Converter {
    private static final Logger logger = LoggerFactory.getLogger(Converter.class);

    public static ProcessNodeDTO convert(HistoricActivityInstance historicActivityInstance) {
        ProcessNodeDTO node = new ProcessNodeDTO();
        node.setId(historicActivityInstance.getId());
        node.setActivityId(historicActivityInstance.getActivityId());
        node.setActivityName(historicActivityInstance.getActivityName());
        node.setActivityType(historicActivityInstance.getActivityType());
        node.setExecutionId(historicActivityInstance.getExecutionId());
        node.setAssignee(historicActivityInstance.getAssignee());
        node.setTaskId(historicActivityInstance.getTaskId());
        node.setProcessInstanceId(historicActivityInstance.getProcessInstanceId());
        node.setProcessDefinitionId(historicActivityInstance.getProcessDefinitionId());
        node.setStartTime(historicActivityInstance.getStartTime());
        node.setEndTime(historicActivityInstance.getEndTime());
        node.setDurationInMillis(historicActivityInstance.getDurationInMillis());
        node.setDeleteReason(historicActivityInstance.getDeleteReason());
        setState(node);
        if (node.getState() == TaskState.END && node.getAssignee() == null && !StringUtils.equals(node.getActivityType(), "startEvent")) {
            node.setSkipped(true);
        }
        return node;
    }

    public static void setState(BaseDTO nodeDTO) {
        if (nodeDTO.getStartTime() == null) {
            nodeDTO.setState(TaskState.WAITING);
            return;
        }
        if (nodeDTO.getEndTime() != null) {
            nodeDTO.setState(TaskState.END);
            return;
        }
        nodeDTO.setState(TaskState.PROCESSING);
        return;
    }

    public static ProcessInstanceDTO convert(ProcessInstance processInstance) {
        ProcessInstanceDTO processInstanceDTO = new ProcessInstanceDTO();
        processInstanceDTO.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        return processInstanceDTO;
    }

    public static TaskDTO convert(HistoricProcessInstance task) {
        TaskDTO flowTaskDTO = new TaskDTO();
        flowTaskDTO.setProcessInstanceId(task.getId());
        flowTaskDTO.setStartTime(task.getStartTime());
        flowTaskDTO.setEndTime(task.getEndTime());
        flowTaskDTO.setDurationInMillis(task.getDurationInMillis());
        setState(flowTaskDTO);
        return flowTaskDTO;
    }

    public static TaskDTO convert(HistoricTaskInstance task) {
        TaskDTO flowTaskDTO = new TaskDTO();
        flowTaskDTO.setAssignee(task.getAssignee());
        flowTaskDTO.setTaskId(task.getId());
        flowTaskDTO.setProcessInstanceId(task.getProcessInstanceId());
        flowTaskDTO.setStartTime(task.getStartTime());
        flowTaskDTO.setEndTime(task.getEndTime());
        flowTaskDTO.setDurationInMillis(task.getDurationInMillis());
        if (flowTaskDTO.getStartTime() == null) {
            flowTaskDTO.setStartTime(task.getCreateTime());
        }
        setState(flowTaskDTO);
        return flowTaskDTO;
    }

    public static TaskDTO convert(Task task) {
        TaskDTO flowTaskDTO = new TaskDTO();
        flowTaskDTO.setAssignee(task.getAssignee());
        flowTaskDTO.setTaskId(task.getId());
        flowTaskDTO.setProcessInstanceId(task.getProcessInstanceId());
        flowTaskDTO.setProcessDefinitionId(task.getProcessDefinitionId());
        flowTaskDTO.setTaskDefinitionKey(task.getTaskDefinitionKey());
        flowTaskDTO.setFormKey(task.getFormKey());
        setState(flowTaskDTO);
        return flowTaskDTO;
    }

    public static void setVariables(BaseDTO baseDTO, Map<String, Object> variables) {
        try {
            baseDTO.setAssigneeName((String)variables.get(Constants.TASK_ASSIGNEE_NAME));
            baseDTO.setUserId((Long)variables.get(Constants.TASK_USER_ID));
            baseDTO.setUserName((String)variables.get(Constants.TASK_USER_NAME));
            baseDTO.setTitle((String)variables.get(Constants.TASK_TITLE));
            variables.remove(Constants.TASK_TITLE);
            baseDTO.setVariables(variables);
        } catch (Throwable e) {
            logger.error("Error setVariables:" + baseDTO + " variables:" + variables, e);

        }
    }
}
