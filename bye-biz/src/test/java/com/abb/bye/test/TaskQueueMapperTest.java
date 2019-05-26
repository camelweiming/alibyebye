package com.abb.bye.test;

import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.TreeNode;
import com.abb.bye.client.domain.enums.Env;
import com.abb.bye.client.service.SequenceService;
import com.abb.bye.mapper.TaskQueueMapper;
import com.abb.bye.test.dao.BaseDAOTest;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/19
 */
public class TaskQueueMapperTest extends BaseDAOTest {
    @Resource
    private TaskQueueMapper taskQueueMapper;
    @Resource
    private SequenceService sequenceService;

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
    public void insertNode() {
        TaskQueueDO q = new TaskQueueDO();
        q.setType(1);
        q.setUniqueKey("test_12");
        q.setStartTime(DateTime.now().toDate());
        q.setTimeout(DateTime.now().plusMinutes(10).toDate());
        q.setExecuteTimeout(DateTime.now().plusSeconds(10).toDate());
        q.setOrigRetryCount(10);
        q.setExecuteIntervalSeconds(10);
        q.setAlarmThreshold(0);
        q.setEnv(Env.DAILY.name());
        q.setStatus(TaskQueueDO.STATUS_WAITING);
        q.setChildrenCount(0);
        TreeNode<TaskQueueDO> node = new TreeNode<>(q);

        TaskQueueDO q2 = new TaskQueueDO();
        q2.setType(1);
        q2.setUniqueKey("test_13");
        q2.setStartTime(DateTime.now().toDate());
        q2.setTimeout(DateTime.now().plusMinutes(10).toDate());
        q2.setExecuteTimeout(DateTime.now().plusSeconds(3).toDate());
        q2.setOrigRetryCount(10);
        q2.setExecuteIntervalSeconds(10);
        q2.setAlarmThreshold(0);
        q2.setEnv(Env.DAILY.name());
        q2.setStatus(TaskQueueDO.STATUS_WAITING);
        q2.setChildrenCount(0);
        node.add(new TreeNode<>(q2));

        List<TaskQueueDO> list = buildQueue(node);
        list.forEach(qq -> {
            taskQueueMapper.insert(qq);
        });
    }

    private List<TaskQueueDO> buildQueue(TreeNode<TaskQueueDO> node) {
        List<TaskQueueDO> taskQueueDOs = new ArrayList<>();
        boolean r = processNode(node, 0, taskQueueDOs);
        if (!r) {
            throw new RuntimeException("processNode failed");
        }
        return taskQueueDOs;
    }

    private boolean processNode(TreeNode<TaskQueueDO> node, long parentId, List<TaskQueueDO> taskQueueDOs) {
        TaskQueueDO taskDO = node.getData();
        taskDO.setParentId(parentId);
        taskDO.setId(sequenceService.next("task_queue"));
        taskDO.setChildrenCount(null == node.getChildren() ? 0 : node.getChildren().size());
        taskQueueDOs.add(taskDO);
        for (TreeNode<TaskQueueDO> child : node.getChildren()) {
            boolean r = processNode(child, taskDO.getId(), taskQueueDOs);
            if (!r) {
                return false;
            }
        }
        return true;
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
