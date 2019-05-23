package com.abb.bye.process.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * @author cenpeng.lwm
 * @since 2019/5/22
 */
public class ApprovalReportTask implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String userName = (String)delegateExecution.getVariable("user_name");
        if (userName.equals("weiming")) {
            delegateExecution.setVariable("pass", true);
        } else {
            delegateExecution.setVariable("pass", false);
        }
        System.out.println(getClass() + "" + delegateExecution);
    }
}
