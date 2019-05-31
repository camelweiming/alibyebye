package com.abb.bye.service.taskqueue;

import com.abb.bye.client.service.taskqueue.TaskQueueDieTaskReleaseRunner;
import com.abb.bye.client.service.taskqueue.TaskQueueService;
import com.abb.bye.utils.CommonThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 简单实现，不适合每台机器都开启，尽量改为分布式调度
 *
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
@Service("taskQueueDieTaskReleaseRunner")
public class TaskQueueDieTaskReleaseRunnerImpl implements TaskQueueDieTaskReleaseRunner {
    private static final Logger logger = LoggerFactory.getLogger(TaskQueueDieTaskReleaseRunnerImpl.class);
    @Resource
    private TaskQueueService taskQueueService;

    @PostConstruct
    void init() {
        CommonThreadPool.getScheduledExecutor().scheduleAtFixedRate(() -> {
            try {doRelease();} catch (Throwable e) {
                logger.error("Error doRelease", e);
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    @Override
    public void doRelease() {
        int releaseCount = taskQueueService.forceStop(new Date());
        if (releaseCount > 0) {
            logger.info("releaseDieTask:" + releaseCount);
        }
    }
}
