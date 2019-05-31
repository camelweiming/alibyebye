package com.abb.bye.service.taskqueue;

import com.abb.bye.Constants;
import com.abb.bye.SystemEnv;
import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.enums.TaskQueueType;
import com.abb.bye.client.service.taskqueue.TaskQueueLock;
import com.abb.bye.mapper.TaskQueueMapper;
import com.abb.bye.service.Sequence;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

/**
 * 这是一个很低效的实现，尽量不要生产环境使用
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
    private TaskQueueMapper taskQueueMapper;
    @Resource
    private Sequence sequence;
    private static String SEQUENCE_NAME = "task_queue";
    private static int RETRY_COUNT = 3;
    private long lockId;

    @PostConstruct
    void init() throws InterruptedException {
        checkAndInitLock();
    }

    void checkAndInitLock() throws InterruptedException {
        TaskQueueDO lock = new TaskQueueDO();
        lock.setType(TaskQueueType.SYS_LOCK.getType());
        lock.setUniqueKey("TASK_LOCK_" + systemEnv.current().name());
        lock.setStartTime(new Date());
        lock.setTimeout(DateTime.now().plusYears(10).toDate());
        lock.setOrigRetryCount(Integer.MAX_VALUE);
        lock.setStatus(TaskQueueDO.STATUS_WAITING);
        lock.setEnv(systemEnv.current().name());
        lock.setChildrenCount(0);
        lock.setParentId(null);
        lock.setAlarmThreshold(0);
        for (int i = 0; i < 3; i++) {
            TaskQueueDO q = taskQueueMapper.get(lock.getType(), lock.getUniqueKey());
            if (q != null) {
                lockId = q.getId();
                logger.info("getLock:" + lockId);
                return;
            }
            try {
                lock.setId(sequence.next(SEQUENCE_NAME));
                taskQueueMapper.insert(lock);
                logger.info("createLock:" + lockId);
                lockId = lock.getId();
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
        boolean lock = taskQueueMapper.lock(lockId, Constants.SERVER_IP, DateTime.now().plusSeconds(TaskQueueType.SYS_LOCK.getExecuteTimeoutSeconds()).toDate()) > 0;
        logger.debug("lock:" + lockId);
        return lock;
    }

    @Override
    public void release() {
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                taskQueueMapper.release(lockId);
                logger.debug("release lock:" + lockId);
                return;
            } catch (Throwable e) {
                logger.warn("release " + lockId + " failed:" + i);
            }
        }
        throw new IllegalStateException("Error release :" + lockId);
    }
}
