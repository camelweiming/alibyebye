package com.abb.flowable.tests.process.bank;

import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * @author cenpeng.lwm
 * @since 2019/5/28
 */
public class ValidateTransferDelegate implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        boolean result = (Boolean)execution.getVariable("result");
        if (result) {
            System.out.println("转账成功");
        } else {
            System.out.println("转账失败，抛出错误");
            throw new BpmnError("transferError");
        }
    }
}
