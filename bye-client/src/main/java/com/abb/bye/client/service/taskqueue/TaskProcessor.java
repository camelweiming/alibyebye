package com.abb.bye.client.service.taskqueue;

import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.TaskResult;
import com.abb.bye.client.domain.enums.TaskQueueType;

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
     * 置为失败通知
     *
     * @param taskQueueDO
     */
    void notifyTimeout(TaskQueueDO taskQueueDO);

    /**
     * 注册类型
     *
     * @return
     */
    TaskQueueType type();
}
