package com.abb.flowable.tests.process.bank;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * @author cenpeng.lwm
 * @since 2019/5/28
 */
public class CancelTransferOutDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("转出银行取消：" + execution);
    }
}
