package com.abb.bye.service;

import com.abb.bye.Constants;
import com.abb.bye.SystemEnv;
import com.abb.bye.client.domain.TaskQueueDO;
import com.abb.bye.client.domain.TaskResult;
import com.abb.bye.client.domain.TreeNode;
import com.abb.bye.client.service.TaskProcessor;
import com.abb.bye.client.service.TaskQueueService;
import com.abb.bye.mapper.TaskQueueMapper;
import com.abb.bye.utils.CommonThreadPool;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private SystemEnv systemEnv;
    @Resource
    private PlatformTransactionManager transactionManager;
    private Map<Integer, TaskProcessor> mapping = new HashMap<>();
    private TaskQueueDO lock;
    private int RETRY_COUNT = 10;

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
        if (taskQueueDO.getExecuteTimeout() == null) {
            taskQueueDO.setExecuteTimeout(new DateTime(taskQueueDO.getStartTime()).plusSeconds(10).toDate());
        }
        if (taskQueueDO.getAlarmThreshold() == null) {
            taskQueueDO.setAlarmThreshold(0);
        }
        taskQueueDO.setChildrenCount(0);
        taskQueueDO.setParentId(null);
        taskQueueMapper.insert(taskQueueDO);
    }

    @Override
    public void apply(TreeNode<TaskQueueDO> node) {

    }
    //private boolean processNode(TreeNode<TaskQueueDO> node, long parentId, List<TaskQueueDO> taskQueueDOs) {
    //    TaskQueueDO taskDO = node.getData();
    //    taskDO.setParentId(parentId);
    //    taskDO.setChildrenCount(null == node.getChildren() ? 0 : node.getChildren().size());
    //    taskQueueDOs.add(taskDO);
    //    for (TreeNode<TaskQueueDO> child : node.getChildren()) {
    //        boolean r = processNode(child, id, taskQueueDOs);
    //        if (!r) {
    //            return false;
    //        }
    //    }
    //    return true;
    //}

    @Override
    public boolean lock(long id) {
        return taskQueueMapper.lock(id, Constants.SERVER_IP) > 0;
    }

    @Override
    public void release(long id) {
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                taskQueueMapper.release(id);
                logger.info("release lock:" + id);
                return;
            } catch (Throwable e) {
                logger.warn("release " + id + " failed:" + i);
            }
        }
        throw new IllegalStateException("Error release :" + id);
    }

    @Override
    public void releaseDieTask() {
        taskQueueMapper.forceStop(new Date());
    }

    @Override
    public void makeFail(TaskQueueDO taskQueueDO, Date nextTime, String msg, boolean forceFail) {
        if (taskQueueDO.getRetryCount() == 1 || forceFail) {
            taskQueueMapper.makeFailed(taskQueueDO.getId(), StringUtils.substring(msg, 200));
        } else {
            taskQueueMapper.makeRetry(taskQueueDO.getId(), nextTime, StringUtils.substring(msg, 200));
        }
    }

    @Override
    public void makeSuccess(TaskQueueDO taskQueueDO) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
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

    void getJob() {
        if (!lock(lock.getId())) {
            return;
        }
        logger.info("get lock:" + lock.getId());
        try {
            List<TaskQueueDO> list = taskQueueMapper.listWaiting(100, systemEnv.current().name());
            for (TaskQueueDO taskQueueDO : list) {
                if (!StringUtils.equals(taskQueueDO.getEnv(), systemEnv.current().name())) {
                    continue;
                }
                CommonThreadPool.getCommonExecutor().submit(() -> dispatch(taskQueueDO));
            }
        } catch (Throwable e) {
            logger.error("Error getJob", e);
        } finally {
            release(lock.getId());
        }
    }

    void dispatch(TaskQueueDO q) {
        TaskProcessor taskProcessor = mapping.get(q.getType());
        if (taskProcessor == null) {
            taskQueueMapper.makeFailed(q.getId(), "PROCESSOR_NOT_FOUND");
            return;
        }
        if (!lock(q.getId())) {
            return;
        }
        try {
            Date now = new Date();
            TaskResult result = taskProcessor.process(q);
            if (logger.isDebugEnabled()) {
                logger.info("dispatch:" + result.getClass());
            }
            if (now.after(q.getTimeout())) {
                makeFail(q, null, "TIME_OUT", true);
                taskProcessor.notifyFailed(q);
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
            release(q.getId());
        }
    }

    void checkAndInitLock() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            TaskQueueDO q = taskQueueMapper.get(lock.getType(), lock.getUniqueKey());
            if (q != null) {
                lock.setId(q.getId());
                logger.info("getLock:" + q.getId());
                return;
            }
            try {
                apply(lock);
            } catch (Throwable e) {
                logger.error("Error checkAndInitLock", e);
            }
            Thread.sleep(500);
        }
        logger.error("Give up initLock");
        throw new IllegalStateException("Give up initLock");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, TaskProcessor> result = ctx.getBeansOfType(TaskProcessor.class);
        result.forEach((k, v) -> {
            if (mapping.put(v.type(), v) != null) {
                throw new RuntimeException("Duplicate type:" + v.type());
            }
            logger.info("Register taskProcessor:" + v.getClass().getCanonicalName());
        });

        lock = new TaskQueueDO();
        lock.setType(0);
        lock.setUniqueKey("TASK_LOCK_" + systemEnv.current().name());
        lock.setStartTime(new Date());
        lock.setTimeout(DateTime.now().plusYears(10).toDate());
        lock.setExecuteTimeout(DateTime.now().plusMinutes(3).toDate());
        lock.setOrigRetryCount(Integer.MAX_VALUE);
        lock.setStatus(TaskQueueDO.STATUS_WAITING);
        lock.setEnv(systemEnv.current().name());

        checkAndInitLock();

        CommonThreadPool.getScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                getJob();
            } catch (Throwable e) {
                logger.error("Error getJob", e);
            }
        }, 0, 2, TimeUnit.SECONDS);

        CommonThreadPool.getScheduledExecutor().scheduleAtFixedRate(() -> {
            try {releaseDieTask();} catch (Throwable e) {
                logger.error("Error getJob", e);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }
}
