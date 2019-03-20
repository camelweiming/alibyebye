package com.abb.bye.client.service;

import com.abb.bye.client.domain.TaskQueueDO;

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

    /**
     * 释放死线程
     */
    void releaseDieTask();

    /**
     * 标记为失败
     *
     * @param taskQueueDO
     * @param nextTime
     * @param msg
     * @param forceFail
     */
    void makeFail(TaskQueueDO taskQueueDO, Date nextTime, String msg, boolean forceFail);
}
