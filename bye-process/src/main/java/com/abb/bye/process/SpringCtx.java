package com.abb.bye.process;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author cenpeng.lwm
 * @since 2019/5/23
 */
@Service
public class SpringCtx implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringCtx.applicationContext = applicationContext;
    }

    public static <T> T getBean(String name) {
        return (applicationContext == null ? null : (T)applicationContext.getBean(name));
    }

    public static <T> T getBean(Class<T> requiredType) {
        return (applicationContext == null ? null : (T)applicationContext.getBean(requiredType));
    }
}
