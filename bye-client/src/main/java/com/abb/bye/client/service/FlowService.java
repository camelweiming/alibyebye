package com.abb.bye.client.service;

import com.abb.bye.client.domain.*;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/6/7
 */
public interface FlowService {
    /**
     * 查询任务
     *
     * @param taskId
     * @param options
     * @return
     */
    ResultDTO<FlowTaskDTO> getTask(String taskId, FlowOptions options);

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

}
