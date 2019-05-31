package com.abb.bye.service;

import com.abb.bye.Constants;
import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.TaskResult;
import com.abb.bye.client.domain.TreeNode;
import com.abb.bye.client.domain.enums.TaskQueueType;
import com.abb.bye.client.service.TaskProcessor;
import com.abb.bye.client.service.TaskQueueService;
import com.abb.bye.mapper.TaskQueueMapper;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author cenpeng.lwm
 * @since 2019/3/20
 */
@Service("taskQueueService")
public class TaskQueueServiceImpl implements TaskQueueService, InitializingBean, ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(TaskQueueServiceImpl.class);
    private volatile ApplicationContext ctx;
    @Resource
    private TaskQueueMapper taskQueueMapper;
    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private Sequence sequence;
    private Map<Integer, TaskProcessor> mapping = new HashMap<>();
    private static String SEQUENCE_NAME = "task_queue";
    private static int RETRY_COUNT = 3;

    @Override
    public void apply(TaskQueueDO taskQueueDO) {
        if (taskQueueDO.getUniqueKey() == null || taskQueueDO.getType() == null) {
            throw new IllegalArgumentException("param error");
        }
        if (taskQueueDO.getStartTime() == null || taskQueueDO.getTimeout() == null) {
            throw new IllegalArgumentException("miss startTime or timeout");
        }
        if (taskQueueDO.getTimeout().before(taskQueueDO.getStartTime())) {
            throw new IllegalArgumentException("timeout before startTime");
        }
        taskQueueDO.setExecuteTimeout(taskQueueDO.getTimeout());
        if (taskQueueDO.getAlarmThreshold() == null) {
            taskQueueDO.setAlarmThreshold(0);
        }
        taskQueueDO.setStatus(TaskQueueDO.STATUS_WAITING);
        if (taskQueueDO.getId() != null) {
            taskQueueDO.setId(sequence.next(SEQUENCE_NAME));
        }
        taskQueueDO.setChildrenCount(0);
        taskQueueDO.setParentId(null);
        taskQueueMapper.insert(taskQueueDO);
    }

    @Override
    public void apply(TreeNode<TaskQueueDO> node) {
        List<TaskQueueDO> list = buildQueue(node);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.execute((TransactionCallback<Void>)transactionStatus -> {
            try {
                for (TaskQueueDO taskQueueDO : list) {
                    apply(taskQueueDO);
                }
            } catch (Throwable e) {
                transactionStatus.setRollbackOnly();
                logger.error("Error applyNode:" + node, e);
            }
            return null;
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
        taskDO.setId(sequence.next(SEQUENCE_NAME));
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

    @Override
    public void makeFail(TaskQueueDO taskQueueDO, Date nextTime, String msg, boolean forceFail) {
        if (taskQueueDO.getRemainRetryCount() == 1 || forceFail) {
            taskQueueMapper.makeFailed(taskQueueDO.getId(), StringUtils.substring(msg, 200));
        } else {
            taskQueueMapper.makeRetry(taskQueueDO.getId(), nextTime, StringUtils.substring(msg, 200));
        }
    }

    @Override
    public void makeSuccess(TaskQueueDO taskQueueDO) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.execute((TransactionCallback<Void>)transactionStatus -> {
            try {
                taskQueueMapper.makeSuccess(taskQueueDO.getId());
                if (taskQueueDO.getParentId() != null) {
                    taskQueueMapper.markChildFinish(taskQueueDO.getParentId());
                }
            } catch (Throwable e) {
                transactionStatus.setRollbackOnly();
                logger.error("Error makeSuccess:" + taskQueueDO.getId());
            }
            return null;
        });

    }

    @Override
    public void doJob(TaskQueueDO q) {
        TaskProcessor taskProcessor = mapping.get(q.getType());
        if (taskProcessor == null) {
            taskQueueMapper.makeFailed(q.getId(), "PROCESSOR_NOT_FOUND");
            return;
        }
        boolean lock = false;
        try {
            if (!lock(q)) {
                logger.info("get lock failed:" + q.getId());
                return;
            }
            lock = true;
            Date now = new Date();
            TaskResult result = taskProcessor.process(q);
            if (logger.isDebugEnabled()) {
                logger.info("dispatch:" + result.getClass());
            }
            if (now.after(q.getTimeout())) {
                makeFail(q, null, "TIME_OUT", true);
                taskProcessor.notifyTimeout(q);
                return;
            }
            if (result.isSuccess()) {
                makeSuccess(q);
                return;
            }
            if (result.isGiveUp()) {
                makeFail(q, null, result.getErrorMsg(), true);
                return;
            }
            Date nextTime = result.getNextExecuteTime();
            if (nextTime == null) {
                if (q.getExecuteIntervalSeconds() != null) {
                    nextTime = new DateTime().plusSeconds(q.getExecuteIntervalSeconds()).toDate();
                } else {
                    nextTime = new Date();
                }
            }
            makeFail(q, nextTime, result.getErrorMsg(), false);
        } catch (Throwable e) {
            logger.error("Error dispatch job:" + q.getId(), e);
        } finally {
            if (lock) {
                release(q.getId());
            }
        }
    }

    private boolean lock(TaskQueueDO q) {
        boolean lock = taskQueueMapper.lock(q.getId(), Constants.SERVER_IP, DateTime.now().plusSeconds(TaskQueueType.getByType(q.getType()).getExecuteTimeoutSeconds()).toDate()) > 0;
        logger.debug("lock:" + q.getId());
        return lock;
    }

    private void release(long lockId) {
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

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, TaskProcessor> result = ctx.getBeansOfType(TaskProcessor.class);
        result.forEach((k, v) -> {
            if (mapping.put(v.type().getType(), v) != null) {
                throw new RuntimeException("Duplicate type:" + v.type());
            }
            logger.info("Register taskProcessor:" + v.getClass().getCanonicalName());
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }
}
