package com.abb.bye.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cenpeng.lwm
 * @since 2019/5/27
 */
public class TimerTask implements JavaDelegate {
    private static Logger logger = LoggerFactory.getLogger(TimerTask.class);

    @Override
    public void execute(DelegateExecution execution) {
        logger.info("##### timer execute..... #####");
    }
}
