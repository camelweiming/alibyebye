package com.abb.bye.client.service;

import java.util.concurrent.ScheduledFuture;

/**
 * @author cenpeng.lwm
 * @since 2019/3/7
 */
public interface SchedulerService {
    /**
     * 注册
     *
     * @param id
     * @param runnable
     * @param expression
     */
    void register(String id, Runnable runnable, String expression);

    /**
     * 停止
     *
     * @param id
     */
    void shutdown(String id);

    /**
     * job是否存在
     *
     * @param id
     * @return
     */
    ScheduledFuture<?> get(String id);
}
