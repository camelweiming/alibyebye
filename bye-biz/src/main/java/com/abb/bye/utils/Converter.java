package com.abb.bye.utils;

import com.abb.bye.Constants;
import com.abb.bye.client.domain.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */
public class Converter {
    private static final Logger logger = LoggerFactory.getLogger(Converter.class);

    public static List<UserDTO> convert(List<UserDO> list) {
        List<UserDTO> userDTOS = new ArrayList<>(list.size());
        list.forEach(l -> userDTOS.add(convert(l)));
        return userDTOS;
    }

    public static UserDTO convert(UserDO userDO) {
        UserDTO u = new UserDTO();
        u.setUserId(userDO.getId());
        u.setUserName(userDO.getName());
        return u;
    }

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
        return node;
    }

    public static ProcessInstanceDTO convert(ProcessInstance processInstance) {
        ProcessInstanceDTO processInstanceDTO = new ProcessInstanceDTO();
        processInstanceDTO.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        return processInstanceDTO;
    }

    public static FlowTaskDTO convert(HistoricProcessInstance task) {
        FlowTaskDTO flowTaskDTO = new FlowTaskDTO();
        flowTaskDTO.setProcessInstanceId(task.getId());
        return flowTaskDTO;
    }

    public static FlowTaskDTO convert(HistoricTaskInstance task) {
        FlowTaskDTO flowTaskDTO = new FlowTaskDTO();
        flowTaskDTO.setAssignee(task.getAssignee());
        flowTaskDTO.setTaskId(task.getId());
        flowTaskDTO.setProcessInstanceId(task.getProcessInstanceId());
        return flowTaskDTO;
    }

    public static FlowTaskDTO convert(Task task) {
        FlowTaskDTO flowTaskDTO = new FlowTaskDTO();
        flowTaskDTO.setAssignee(task.getAssignee());
        flowTaskDTO.setTaskId(task.getId());
        flowTaskDTO.setProcessInstanceId(task.getProcessInstanceId());
        return flowTaskDTO;
    }

    public static void setVariables(FlowBaseDTO flowBaseDTO, Map<String, Object> variables) {
        try {
            flowBaseDTO.setAssigneeName((String)variables.get(Constants.TASK_ASSIGNEE_NAME));
            flowBaseDTO.setUserId((Long)variables.get(Constants.TASK_USER_ID));
            flowBaseDTO.setUserName((String)variables.get(Constants.TASK_USER_NAME));
            flowBaseDTO.setTitle((String)variables.get(Constants.TASK_TITLE));
            variables.remove(Constants.TASK_TITLE);
            flowBaseDTO.setVariables(variables);
        } catch (Throwable e) {
            logger.error("Error setVariables:" + flowBaseDTO + " variables:" + variables, e);

        }
    }
}
