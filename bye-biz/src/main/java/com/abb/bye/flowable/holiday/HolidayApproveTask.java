package com.abb.bye.flowable.holiday;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cenpeng.lwm
 * @since 2019/5/25
 */
public class HolidayApproveTask implements JavaDelegate {
    private static final Logger logger = LoggerFactory.getLogger(HolidayApproveTask.class);
    @Override
    public void execute(DelegateExecution execution) {
        logger.info("##### holiday is pass #####");
    }
}
