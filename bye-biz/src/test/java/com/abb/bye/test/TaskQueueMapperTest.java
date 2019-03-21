package com.abb.bye.test;

import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.enums.Env;
import com.abb.bye.mapper.TaskQueueMapper;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

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
        q.setUniqueKey("test_3");
        q.setStartTime(DateTime.now().plusMinutes(1).toDate());
        q.setTimeout(DateTime.now().plusMinutes(10).toDate());
        q.setExecuteTimeout(DateTime.now().plusSeconds(10).toDate());
        q.setOrigRetryCount(10);
        q.setExecuteIntervalSeconds(10);
        q.setAlarmThreshold(0);
        q.setExecuteTimeout(DateTime.now().plusSeconds(30).toDate());
        q.setEnv(Env.DAILY.name());
        q.setStatus(TaskQueueDO.STATUS_WAITING);
        q.setChildrenCount(0);
        taskQueueMapper.insert(q);
    }

    @Test
    public void insertTimeout() {
        TaskQueueDO q = new TaskQueueDO();
        q.setType(1);
        q.setUniqueKey("test_4");
        q.setStartTime(DateTime.now().toDate());
        q.setTimeout(DateTime.now().plusMinutes(10).toDate());
        q.setExecuteTimeout(DateTime.now().plusSeconds(10).toDate());
        q.setOrigRetryCount(10);
        q.setExecuteIntervalSeconds(10);
        q.setAlarmThreshold(0);
        q.setEnv(Env.DAILY.name());
        q.setStatus(TaskQueueDO.STATUS_RUNNING);
        q.setChildrenCount(0);
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

    @Test
    public void forceStop() {
        taskQueueMapper.forceStop(new Date());
    }
}
