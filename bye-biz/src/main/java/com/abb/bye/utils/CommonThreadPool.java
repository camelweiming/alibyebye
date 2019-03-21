package com.abb.bye.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author cenpeng.lwm
 * @version $Id$
 * @since 1.0.0 2017/02/23
 */
public class CommonThreadPool {
    private static final Logger logger = LoggerFactory.getLogger(CommonThreadPool.class);
    /**
     * 公用线程池
     */
    private static ThreadPoolExecutor COMMON_EXECUTOR = new ThreadPoolExecutor(10, 500, 30, TimeUnit.SECONDS, new SynchronousQueue<>(), r -> {
        Thread t = new Thread(r, "COMMON_THREAD_POOL");
        t.setDaemon(true);
        return t;
    }, (r, executor) -> {
        String msg = "Task " + r.toString() + " rejected from " + executor.toString();
        logger.error(msg);
        throw new RejectedExecutionException(msg);
    });

    private static ScheduledExecutorService SCHEDULED_EXECUTOR = new ScheduledThreadPoolExecutor(3);

    public static ThreadPoolExecutor getCommonExecutor() {
        return COMMON_EXECUTOR;
    }

    public static ScheduledExecutorService getScheduledExecutor() {
        return SCHEDULED_EXECUTOR;
    }
}
