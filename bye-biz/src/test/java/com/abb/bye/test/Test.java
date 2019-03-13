package com.abb.bye.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author camelweiming@163.com
 * @since 2018/6/7
 */
public class Test {
    static Logger logger = LoggerFactory.getLogger(Test.class);
    private static ArrayBlockingQueue queue = new ArrayBlockingQueue(10);
    private static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(0, 10, 30, TimeUnit.SECONDS, queue, r -> {
        Thread t = new Thread(r, "PROXY_THREAD_POOL");
        t.setDaemon(true);
        return t;
    }, (r, executor) -> {
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            final int c = i;
            EXECUTOR.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        System.out.println(c + " done "+ queue.size());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        Thread.sleep(1000000);
    }
}
