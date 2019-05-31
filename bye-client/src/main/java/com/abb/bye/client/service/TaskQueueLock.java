package com.abb.bye.client.service;

/**
 * 全局锁实现
 *
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
public interface TaskQueueLock {
    /**
     * 加锁
     *
     * @return
     */
    boolean lock();

    /**
     * 释放锁
     */
    void release();
}
