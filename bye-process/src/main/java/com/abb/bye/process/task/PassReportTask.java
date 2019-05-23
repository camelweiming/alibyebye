package com.abb.bye.process.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * @author cenpeng.lwm
 * @since 2019/5/22
 */
public class PassReportTask implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        System.out.println(getClass() + "" + delegateExecution);
    }
}
