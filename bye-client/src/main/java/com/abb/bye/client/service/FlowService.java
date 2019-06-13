package com.abb.bye.client.service;

import com.abb.bye.client.domain.*;
import com.abb.bye.client.flow.FlowForm;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/6/7
 */
public interface FlowService {
    /**
     * 获取form
     *
     * @param formKey
     * @return
     */
    FlowForm getFrom(String formKey);

    /**
     * 查询任务
     *
     * @param taskId
     * @param options
     * @return
     */
    ResultDTO<FlowTaskDTO> getTask(String taskId, FlowOptions options);

    /**
     * 查询
     *
     * @param query
     * @return
     */
    ResultDTO<List<FlowTaskDTO>> query(FlowTaskQuery query);

    /**
     * 提交流程
     *
     * @param flowSubmitDTO
     * @param processDefinitionKey
     * @return
     */
    ResultDTO<ProcessInstanceDTO> submitProcessor(String processDefinitionKey, FlowSubmitDTO flowSubmitDTO);

    /**
     * 完成节点
     *
     * @param taskId
     * @param flowCompleteDTO
     * @return
     */
    ResultDTO<Void> complete(String taskId, FlowCompleteDTO flowCompleteDTO);

    /**
     * 通过processInstanceId获取节点信息
     *
     * @param processInstanceId
     * @param options
     * @return
     */
    ResultDTO<List<ProcessNodeDTO>> getByInstanceId(String processInstanceId, FlowOptions options);

    /**
     * 查询表单formKey
     *
     * @param processKey processDefinitionId：holidayRequest:374:965004 或者流程的ID：holidayRequest
     * @return
     */
    ResultDTO<String> getStartFormKey(String processKey);

    /**
     * 查询表单formKey
     *
     * @param processDefinitionId holidayRequest:374:965004
     * @param taskDefinitionKey   节点ID：usertask
     * @return
     */
    ResultDTO<String> getFormKey(String processDefinitionId, String taskDefinitionKey);

}
