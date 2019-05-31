package com.abb.bye.tasks;

import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.TaskResult;
import com.abb.bye.client.domain.enums.TaskQueueType;
import com.abb.bye.client.service.taskqueue.TaskProcessor;
import org.springframework.stereotype.Service;

/**
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
@Service
public class DemoTask implements TaskProcessor {

    @Override
    public TaskResult process(TaskQueueDO taskQueueDO) {
        if (taskQueueDO.getRetryCount() < 5) {
            System.out.println("demo mock error:" + taskQueueDO.getId() + " retry:" + taskQueueDO.getRetryCount());
            return new TaskResult(false).setErrorMsg("mock error");
        }
        System.out.println("demo execute:" + taskQueueDO.getId());
        return new TaskResult(true);
    }

    @Override
    public void notifyTimeout(TaskQueueDO taskQueueDO) {
        System.out.println("task timeout:" + taskQueueDO);
    }

    @Override
    public TaskQueueType type() {
        return TaskQueueType.DEMO;
    }
}
