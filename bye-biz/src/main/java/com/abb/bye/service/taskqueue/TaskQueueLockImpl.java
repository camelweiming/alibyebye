package com.abb.bye.service.taskqueue;

import com.abb.bye.SystemEnv;
import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.enums.TaskQueueType;
import com.abb.bye.client.service.taskqueue.TaskQueueLock;
import com.abb.bye.client.service.taskqueue.TaskQueueService;
import com.abb.bye.service.Sequence;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

/**
 * 这是一个基于DB的锁，很低效的实现，尽量不要生产环境使用，如果Dispatcher是分布式调度派发的，没必要加锁
 *
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
@Service("taskQueueLock")
public class TaskQueueLockImpl implements TaskQueueLock {
    private static final Logger logger = LoggerFactory.getLogger(TaskQueueLockImpl.class);
    @Resource
    private SystemEnv systemEnv;
    @Resource
    private TaskQueueService taskQueueService;
    @Resource
    private Sequence sequence;
    private static String SEQUENCE_NAME = "task_queue";
    private TaskQueueDO lock = null;

    @PostConstruct
    void init() throws InterruptedException {
        checkAndInitLock();
    }

    void checkAndInitLock() throws InterruptedException {
        String uniqueName = "TASK_LOCK_" + systemEnv.current().name();
        for (int i = 0; i < 3; i++) {
            lock = taskQueueService.get(TaskQueueType.SYS_LOCK, uniqueName);
            if (lock != null) {
                logger.info("getLock:" + lock.getId());
                return;
            }
            try {
                lock = new TaskQueueDO();
                lock.setId(sequence.next(SEQUENCE_NAME));
                lock.setType(TaskQueueType.SYS_LOCK.getType());
                lock.setUniqueKey(uniqueName);
                lock.setStartTime(new Date());
                lock.setTimeout(DateTime.now().plusYears(10).toDate());
                lock.setOrigRetryCount(Integer.MAX_VALUE);
                lock.setStatus(TaskQueueDO.STATUS_WAITING);
                lock.setEnv(systemEnv.current().name());
                lock.setChildrenCount(0);
                lock.setParentId(null);
                lock.setAlarmThreshold(0);
                taskQueueService.apply(lock);
                logger.info("createLock:" + lock.getId());
            } catch (Throwable e) {
                logger.error("Error checkAndInitLock", e);
            }
            Thread.sleep(500);
        }
        logger.error("Give up initLock");
        throw new IllegalStateException("Give up initLock");
    }

    @Override
    public boolean lock() {
        return taskQueueService.lock(lock);
    }

    @Override
    public void release() {
        taskQueueService.release(lock.getId());
    }
}
