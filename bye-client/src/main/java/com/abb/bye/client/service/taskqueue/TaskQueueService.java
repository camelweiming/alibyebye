package com.abb.bye.client.service.taskqueue;

import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.TreeNode;
import com.abb.bye.client.domain.enums.Env;
import com.abb.bye.client.domain.enums.TaskQueueType;

import java.util.Date;
import java.util.List;

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

    /**
     * 加锁
     *
     * @param queueDO
     * @return
     */
    boolean lock(TaskQueueDO queueDO);

    /**
     * 释放锁
     *
     * @param id
     */
    void release(long id);

    /**
     * 查询
     *
     * @param taskQueueType
     * @param uk
     * @return
     */
    TaskQueueDO get(TaskQueueType taskQueueType, String uk);

    /**
     * 查询待执行列表
     *
     * @param size
     * @param env
     * @return
     */
    List<TaskQueueDO> listWaiting(int size, Env env);

    /**
     * 释放执行超时的锁
     *
     * @param date
     */
    int forceStop(Date date);
}
