package com.abb.bye.test;

import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.mapper.TaskQueueMapper;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author cenpeng.lwm
 * @since 2019/3/19
 */
public class TaskQueueMapperTest extends BaseDAOTest {
    @Resource
    private TaskQueueMapper taskQueueMapper;

    @Test
    public void insert() {
        TaskQueueDO q = new TaskQueueDO();
        q.setType(1);
        q.setUniqueKey("test");
        q.setStartTime(DateTime.now().plusMinutes(10).toDate());
        q.setTimeout(DateTime.now().plusMinutes(60).toDate());
        q.setExecuteTimeout(DateTime.now().plusMinutes(1).toDate());
        q.setOrigRetryCount(30);
        q.setExecuteIntervalSeconds(3);
        q.setExecuteTimeout(DateTime.now().plusMinutes(1).toDate());
        q.setEnv("daily");
        q.setStatus(TaskQueueDO.STATUS_WAITING);
        taskQueueMapper.insert(q);
    }

    @Test
    public void testLock() {
        taskQueueMapper.lock(1l, "localhost");
    }

    @Test
    public void makeRetry() {
        taskQueueMapper.makeRetry(1l, null, "xxxx");
    }

    @Test
    public void makeFailed() {
        taskQueueMapper.makeFailed(1l, "de4444");
    }

    @Test
    public void makeSuccess() {
        taskQueueMapper.makeSuccess(1l);
    }
}
