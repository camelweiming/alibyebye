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
        q.setEnv("daily");
        taskQueueMapper.insert(q);
    }
}
