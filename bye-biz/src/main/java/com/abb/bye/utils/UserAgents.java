package com.abb.bye.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cenpeng.lwm
 * @since 2019/3/14
 */
public class UserAgents {
    private static UserAgents userAgents = new UserAgents();
    private static String[] list;
    private AtomicInteger c = new AtomicInteger();

    public static UserAgents get() {
        if (list == null) {
            synchronized (UserAgents.class) {
                if (list == null) {
                    try {
                        String content = IOUtils.toString(UserAgents.class.getClassLoader().getResourceAsStream("useragent.txt"), "UTF-8");
                        list = StringUtils.split(content, "\r\n");
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
        return userAgents;
    }

    public String getOne() {
        int current = c.incrementAndGet();
        if (current > list.length - 1) {
            current = 0;
            c.set(0);
        }
        return list[current];
    }
}
