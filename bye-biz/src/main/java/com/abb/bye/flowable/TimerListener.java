package com.abb.bye.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cenpeng.lwm
 * @since 2019/5/27
 */
public class TimerListener implements ExecutionListener {
    private static Logger logger = LoggerFactory.getLogger(TimerListener.class);

    @Override
    public void notify(DelegateExecution execution) {
        logger.info("#############");
    }
}
