package com.abb.bye.client.service.taskqueue;

import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.TreeNode;

import java.util.Date;

/**
 * @author cenpeng.lwm
 * @since 2019/3/20
 */
public interface TaskQueueService {
    /**
     * 必填：type,uniqueKey,timeout,retryCount
     *
     * @param taskQueueDO
     */
    void apply(TaskQueueDO taskQueueDO);

    /**
     * 创建级联节点
     *
     * @param node
     */
    void apply(TreeNode<TaskQueueDO> node);

    /**
     * 执行任务
     *
     * @param q
     */
    void doJob(TaskQueueDO q);

    /**
     * 标记为失败
     *
     * @param taskQueueDO
     * @param nextTime
     * @param msg
     * @param forceFail
     */
    void makeFail(TaskQueueDO taskQueueDO, Date nextTime, String msg, boolean forceFail);

    /**
     * 成功
     *
     * @param taskQueueDO
     */
    void makeSuccess(TaskQueueDO taskQueueDO);
}
