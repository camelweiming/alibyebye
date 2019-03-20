package com.abb.bye.client.service;

import com.abb.bye.client.domain.TaskQueueDO;

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
     * 锁
     *
     * @param id
     * @return
     */
    boolean lock(long id);

    /**
     * 释放
     *
     * @param id
     */
    void release(long id);
}
