package com.abb.bye.service;

import com.abb.bye.client.service.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

/**
 * @author cenpeng.lwm
 * @since 2019/3/7
 */
@Service("schedulerService")
public class ScheduleServiceImpl implements SchedulerService, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);
    private ThreadPoolTaskScheduler scheduler;
    private Map<String, ScheduledFuture<?>> jobsMap = new HashMap<>();

    @Override
    public synchronized void register(String id, Runnable runnable, String expression) {
        if (jobsMap.get(id) != null) {
            throw new IllegalStateException(id + " already exist");
        }
        ScheduledFuture<?> scheduledTask = scheduler.schedule(runnable, new CronTrigger(expression, TimeZone.getTimeZone(TimeZone.getDefault().getID())));
        jobsMap.put(id, scheduledTask);
        logger.info("register job:" + id);
    }

    @Override
    public synchronized void shutdown(String id) {
        synchronized (jobsMap) {
            ScheduledFuture<?> future = jobsMap.get(id);
            if (future != null) {
                future.cancel(true);
                jobsMap.remove(id);
                logger.info("shutdown job:" + id);
            }
        }
    }

    @Override
    public ScheduledFuture<?> get(String id) {
        return jobsMap.get(id);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("spider-");
        scheduler.initialize();
    }
}
