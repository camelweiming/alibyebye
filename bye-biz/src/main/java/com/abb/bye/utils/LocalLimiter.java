package com.abb.bye.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author cenpeng.lwm
 * @since 2019/3/13
 */
public class LocalLimiter {
    private int current;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    /**
     * 如果到达上限会阻塞，前台不要使用
     */
    public void add(int limit) throws InterruptedException {
        lock.lock();
        try {
            while (current >= limit) {
                condition.await();
            }
            current++;
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        lock.lock();
        try {
            current--;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int getCurrent() {
        return current;
    }
}
