package com.abb.bye.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author cenpeng.lwm
 * @since 2019/6/11
 */
public class SpringCtx implements ApplicationContextAware {
    private static volatile ApplicationContext context;

    public static <T> T getBean(Class<T> clazz) {
        return (T)context.getBean(clazz);
    }

    public static Object getBean(String name) throws BeansException {
        return context.getBean(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
