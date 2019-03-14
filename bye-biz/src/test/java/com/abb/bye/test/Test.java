package com.abb.bye.test;

import com.abb.bye.client.domain.ProxyDO;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
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
        Map<String, Object> a = new HashMap<>();
        a.put(ProxyDO.ATTR_SWITCH_IP_URL, "http://ip.dobel.cn/switch-ip");
        a.put(ProxyDO.ATTR_SWITCH_IP_REQ_COUNT, 50);
        System.out.println(JSON.toJSON(a));
    }
}
