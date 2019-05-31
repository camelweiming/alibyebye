package com.abb.bye.client.service.taskqueue;

/**
 * 该方法只有一台机器执行即可
 * <p>
 * 释放因执行超时且程序异常退出而占用的锁
 * <p>
 * 比如方法3秒超时，2秒时程序被杀死，锁会一直占用，导致无法再执行
 *
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
public interface TaskQueueDieTaskReleaseRunner {
    /**
     * 执行
     */
    void doRelease();
}
