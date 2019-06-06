package com.abb.bye.service.taskqueue;

import com.abb.bye.SystemEnv;
import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.enums.TaskQueueType;
import com.abb.bye.client.service.taskqueue.TaskDispatcher;
import com.abb.bye.client.service.taskqueue.TaskQueueLock;
import com.abb.bye.client.service.taskqueue.TaskQueueService;
import com.abb.bye.utils.CommonThreadPool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 任务派发，最好使用分布式调度来处理，如果用分布式调度派发，不需要加lock
 *
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
@Service
public class TaskQueueDispatcher implements TaskDispatcher, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(TaskQueueDispatcher.class);
    @Resource
    private TaskQueueLock taskQueueLock;
    @Resource
    private SystemEnv systemEnv;
    @Resource
    private TaskQueueService taskQueueService;
    private static int GET_JOB_COUNT = 50;

    @Override
    public void doDispatcher(int size) {
        if (!taskQueueLock.lock()) {
            return;
        }
        try {
            List<TaskQueueDO> list = taskQueueService.listWaiting(size, systemEnv.current());
            for (TaskQueueDO taskQueueDO : list) {
                if (TaskQueueType.SYS_LOCK.getType() == taskQueueDO.getType()) {
                    continue;
                }
                if (!StringUtils.equals(taskQueueDO.getEnv(), systemEnv.current().name())) {
                    continue;
                }
                CommonThreadPool.getCommonExecutor().submit(() -> taskQueueService.doJob(taskQueueDO));
            }
        } catch (Throwable e) {
            logger.error("Error getJob", e);
        } finally {
            taskQueueLock.release();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //CommonThreadPool.getScheduledExecutor().scheduleAtFixedRate(() -> {
        //    try {
        //        doDispatcher(GET_JOB_COUNT);
        //    } catch (Throwable e) {
        //        logger.error("Error getJob", e);
        //    }
        //}, 0, 2, TimeUnit.SECONDS);
    }

}
