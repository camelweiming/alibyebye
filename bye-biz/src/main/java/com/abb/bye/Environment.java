package com.abb.bye;

import com.abb.bye.client.domain.enums.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cenpeng.lwm
 * @date 2018/09/10
 * @since 2018/9/10
 */
public class Environment {
    private static Logger logger = LoggerFactory.getLogger(Environment.class);
    private static Env env;

    private Environment(String env) {
        for (Env e : Env.values()) {
            if (env.equals(e.getName())) {
                Environment.env = e;
                logger.info("current env:" + env);
                return;
            }
        }
        throw new IllegalStateException("Cant get local env");
    }

    public Env current() {
        return env;
    }

    public boolean isDaily() {
        return env == Env.DAILY;
    }
}
