package com.abb.flowable.tests;

import org.flowable.engine.test.Deployment;
import org.junit.Test;

/**
 * @author cenpeng.lwm
 * @since 2019/5/28
 */
public class CompensateTest extends BaseTest {
    @Test
    @Deployment
    public void testCompensateSubprocess() {
        runtimeService.startProcessInstanceByKey("compensateProcess");
    }
}
