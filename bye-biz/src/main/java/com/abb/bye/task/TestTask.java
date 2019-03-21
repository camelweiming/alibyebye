package com.abb.bye.task;

import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.TaskResult;
import com.abb.bye.client.service.TaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author cenpeng.lwm
 * @since 2019/3/20
 */
@Component
public class TestTask implements TaskProcessor {
    private static Logger logger = LoggerFactory.getLogger(TestTask.class);

    @Override
    public TaskResult process(TaskQueueDO taskQueueDO) {
        logger.info("handle TestTask:" + taskQueueDO);
        return new TaskResult(false).setErrorMsg("error...");
    }

    @Override
    public void notifyFailed(TaskQueueDO taskQueueDO) {
        logger.info("Failed TestTask:" + taskQueueDO);
    }

    @Override
    public int type() {
        return 1;
    }
}
