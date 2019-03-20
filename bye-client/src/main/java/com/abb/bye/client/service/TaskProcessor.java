package com.abb.bye.client.service;

import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.TaskResult;

/**
 * @author cenpeng.lwm
 * @since 2019/3/20
 */
public interface TaskProcessor {
    /**
     * 处理任务
     *
     * @param taskQueueDO
     * @return
     */
    TaskResult process(TaskQueueDO taskQueueDO);

    /**
     * 注册类型
     *
     * @return
     */
    int type();
}
