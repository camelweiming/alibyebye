package com.abb.bye.test.dao;

import com.abb.bye.Constants;
import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.enums.TaskQueueType;
import com.abb.bye.mapper.TaskQueueMapper;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
public class TaskDAOTest extends BaseDAOTest {
    @Resource
    private TaskQueueMapper taskQueueMapper;

    @Test
    public void insert() {
        TaskQueueDO lock = new TaskQueueDO();
        lock.setType(TaskQueueType.DEMO.getType());
        lock.setUniqueKey("demo_" + System.currentTimeMillis());
        lock.setStartTime(new Date());
        lock.setTimeout(DateTime.now().plusMinutes(5).toDate());
        lock.setOrigRetryCount(1000);
        lock.setExecuteIntervalSeconds(10);
        lock.setStatus(TaskQueueDO.STATUS_WAITING);
        lock.setEnv("DAILY");
        lock.setChildrenCount(0);
        lock.setParentId(null);
        lock.setAlarmThreshold(0);
        taskQueueMapper.insert(lock);
    }

    @Test
    public void testLock() throws InterruptedException {
        TaskQueueDO q = new TaskQueueDO();
        q.setId(1516001l);
        q.setType(TaskQueueType.SYS_LOCK.getType());
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                while (true) {
                    if (!lock(q)) {
                        continue;
                    }
                    System.out.println("getLock");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    release(q.getId());
                }
            }).start();
        }
        Thread.sleep(100000l);
    }

    private boolean lock(TaskQueueDO q) {
        boolean lock = taskQueueMapper.lock(q.getId(), Constants.SERVER_IP, DateTime.now().plusSeconds(TaskQueueType.getByType(q.getType()).getExecuteTimeoutSeconds()).toDate()) > 0;
        return lock;
    }

    private void release(long lockId) {
        taskQueueMapper.release(lockId);
        System.out.println("release lock:" + lockId);
    }
}
