package com.abb.bye;

import com.abb.bye.client.domain.enums.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cenpeng.lwm
 * @date 2018/09/10
 * @since 2018/9/10
 */
public class SystemEnv {
    private static Logger logger = LoggerFactory.getLogger(SystemEnv.class);
    private Env env;

    public SystemEnv(String env) {
        for (Env e : Env.values()) {
            if (env.equals(e.name())) {
                this.env = e;
                logger.info("current env:" + env);
                return;
            }
        }
        throw new IllegalStateException("Cant get local env");
    }

    public Env current() {
        if (env == null) {
            throw new IllegalStateException("SystemEnv not init");
        }
        return env;
    }

    public boolean isDaily() {
        return current() == Env.DAILY;
    }
}
